package ec.edu.puce.ecotrip.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Crea el archivo de DataStore en el disco del teléfono (una sola vez por app).
private val Context.almacenEcoTrip: DataStore<Preferences> by preferencesDataStore(
    name = "ecotrip_preferencias",
)

/**
 * NIVEL 3 de resiliencia: DISCO.
 * Guarda las preferencias globales del usuario. Sobreviven aunque se cierre la app por completo.
 */
class PreferenciasRepository(contexto: Context) {

    private val almacen: DataStore<Preferences> = contexto.applicationContext.almacenEcoTrip

    /** Flujo que entrega las preferencias guardadas (y avisa si cambian). */
    val preferencias: Flow<PreferenciaViaje> = almacen.data
        .catch { error ->
            // Si el archivo no se puede leer, se empieza con preferencias vacías.
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { datos ->
            PreferenciaViaje(
                nombreViajero = datos[CLAVE_NOMBRE] ?: "",
                tipoTransporte = transporteDesdeTexto(datos[CLAVE_TRANSPORTE]),
                soloBajaHuella = datos[CLAVE_BAJA_HUELLA] ?: false,
            )
        }

    /** Escribe las preferencias en disco (es asíncrono: se llama dentro de una corrutina). */
    suspend fun guardar(nuevas: PreferenciaViaje) {
        almacen.edit { datos ->
            datos[CLAVE_NOMBRE] = nuevas.nombreViajero
            datos[CLAVE_TRANSPORTE] = nuevas.tipoTransporte.name
            datos[CLAVE_BAJA_HUELLA] = nuevas.soloBajaHuella
        }
    }

    private companion object {
        val CLAVE_NOMBRE = stringPreferencesKey("nombre_viajero")
        val CLAVE_TRANSPORTE = stringPreferencesKey("tipo_transporte")
        val CLAVE_BAJA_HUELLA = booleanPreferencesKey("solo_baja_huella")
    }
}
