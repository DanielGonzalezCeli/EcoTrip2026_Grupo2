package ec.edu.puce.ecotrip.navigation

import ec.edu.puce.ecotrip.data.TipoTransporte
import kotlinx.serialization.Serializable

/** Ruta de la pantalla 1 (Formulario). No necesita datos. */
@Serializable
object RutaFormulario

/**
 * Ruta de la pantalla 2 (Resumen) con TIPOS FUERTES.
 * Para navegar hay que entregar los 6 datos con su tipo correcto:
 * si falta uno o es del tipo equivocado, el código ni siquiera compila.
 */
@Serializable
data class RutaResumen(
    val nombreViajero: String,
    val tipoTransporte: TipoTransporte,
    val soloBajaHuella: Boolean,
    val destino: String,
    val diasDuracion: Int,
    val esViajeGrupal: Boolean,
)
