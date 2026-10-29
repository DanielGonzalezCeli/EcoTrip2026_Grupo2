package ec.edu.puce.ecotrip.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ec.edu.puce.ecotrip.data.PreferenciaViaje
import ec.edu.puce.ecotrip.data.PreferenciasRepository
import ec.edu.puce.ecotrip.data.TipoTransporte
import ec.edu.puce.ecotrip.navigation.RutaResumen
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.Serializable

/**
 * Estado completo del formulario. La pantalla SOLO dibuja lo que hay aquí.
 * Es inmutable (val) y Serializable para que SavedStateHandle pueda guardarlo.
 */
data class EstadoFormulario(
    val nombreViajero: String = "",
    val tipoTransporte: TipoTransporte = TipoTransporte.TREN,
    val soloBajaHuella: Boolean = false,
    val destino: String = "",
    val diasTexto: String = "",
    val esViajeGrupal: Boolean = false,
    val mostrarErrores: Boolean = false,
) : Serializable {
    // Los errores solo se muestran después de intentar planificar.
    val nombreConError: Boolean get() = mostrarErrores && !nombreValido(nombreViajero)
    val destinoConError: Boolean get() = mostrarErrores && !destinoValido(destino)
    val diasConError: Boolean get() = mostrarErrores && diasValidos(diasTexto) == null
}

/**
 * ÚNICA FUENTE DE VERDAD de la pantalla de formulario.
 *
 * Los tres niveles de resiliencia:
 *  1. MEMORIA  -> el ViewModel sobrevive a la rotación de pantalla.
 *  2. PROCESO  -> SavedStateHandle sobrevive a la muerte del proceso (Process Death).
 *  3. DISCO    -> DataStore (nombre, transporte y baja huella) sobrevive al cierre total.
 */
class EcoTripViewModel(
    application: Application,
    private val estadoGuardado: SavedStateHandle,
) : AndroidViewModel(application) {

    private val repositorio = PreferenciasRepository(application)

    /** Estado que la pantalla observa. Viene de SavedStateHandle (nivel 2). */
    val estado: StateFlow<EstadoFormulario> =
        estadoGuardado.getStateFlow(CLAVE_ESTADO, EstadoFormulario())

    init {
        cargarPreferenciasDelDisco()
    }

    /**
     * Si la app arranca "desde cero" (no hay estado guardado), se rellenan
     * nombre, transporte y baja huella con lo que hay en disco (nivel 3).
     * Si ya hay estado (rotación o process death) NO se pisa nada.
     */
    private fun cargarPreferenciasDelDisco() {
        viewModelScope.launch {
            val delDisco = repositorio.preferencias.first()
            if (!estadoGuardado.contains(CLAVE_ESTADO)) {
                estadoGuardado[CLAVE_ESTADO] = EstadoFormulario(
                    nombreViajero = limpiarNombre(delDisco.nombreViajero), // limpia datos viejos del disco
                    tipoTransporte = delDisco.tipoTransporte,
                    soloBajaHuella = delDisco.soloBajaHuella,
                )
            }
        }
    }

    // ---------- Eventos que llegan desde la pantalla (los datos "suben") ----------

    fun cambiarNombre(nuevoNombre: String) {
        actualizar { it.copy(nombreViajero = limpiarNombre(nuevoNombre)) }
        guardarPreferenciasEnDisco()
    }

    fun cambiarTransporte(nuevoTransporte: TipoTransporte) {
        actualizar { it.copy(tipoTransporte = nuevoTransporte) }
        guardarPreferenciasEnDisco()
    }

    fun cambiarSoloBajaHuella(activado: Boolean) {
        actualizar { it.copy(soloBajaHuella = activado) }
        guardarPreferenciasEnDisco()
    }

    fun cambiarDestino(nuevoDestino: String) {
        actualizar { it.copy(destino = limpiarDestino(nuevoDestino)) }
    }

    fun cambiarDias(textoEscrito: String) {
        actualizar { it.copy(diasTexto = limpiarDias(textoEscrito)) }
    }

    fun cambiarViajeGrupal(activado: Boolean) {
        actualizar { it.copy(esViajeGrupal = activado) }
    }

    /**
     * Valida los datos y arma la ruta tipada hacia el Resumen.
     * Devuelve null si algo es inválido (la pantalla mostrará los errores).
     */
    fun crearRutaResumen(): RutaResumen? {
        actualizar { it.copy(mostrarErrores = true) }

        val actual = estado.value
        val dias = diasValidos(actual.diasTexto)
        if (!nombreValido(actual.nombreViajero) || !destinoValido(actual.destino) || dias == null) {
            return null
        }

        return RutaResumen(
            nombreViajero = actual.nombreViajero.trim(),
            tipoTransporte = actual.tipoTransporte,
            soloBajaHuella = actual.soloBajaHuella,
            destino = actual.destino.trim(),
            diasDuracion = dias,
            esViajeGrupal = actual.esViajeGrupal,
        )
    }

    /**
     * Prepara el formulario para una ruta nueva: borra los datos del viaje anterior
     * (destino, días, grupal) y los errores. Las preferencias (nombre, transporte y
     * baja huella) se conservan porque son globales y viven en DataStore.
     */
    fun nuevaRuta() {
        actualizar { it.copy(destino = "", diasTexto = "", esViajeGrupal = false, mostrarErrores = false) }
    }

    // ---------- Ayudas privadas ----------

    /** Crea una copia modificada del estado y la guarda en SavedStateHandle. */
    private fun actualizar(cambio: (EstadoFormulario) -> EstadoFormulario) {
        estadoGuardado[CLAVE_ESTADO] = cambio(estado.value)
    }

    private fun guardarPreferenciasEnDisco() {
        val actual = estado.value
        val preferencias = PreferenciaViaje(
            nombreViajero = actual.nombreViajero,
            tipoTransporte = actual.tipoTransporte,
            soloBajaHuella = actual.soloBajaHuella,
        )
        viewModelScope.launch { repositorio.guardar(preferencias) }
    }

    private companion object {
        const val CLAVE_ESTADO = "estado_formulario"
    }
}
