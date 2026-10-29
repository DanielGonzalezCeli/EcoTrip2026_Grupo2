package ec.edu.puce.ecotrip.data

/** Los tres medios de transporte ecológico que ofrece EcoTrip. */
enum class TipoTransporte { TREN, BICICLETA, VEHICULO_ELECTRICO }

/**
 * Modelo de datos INMUTABLE: todas las propiedades son `val`.
 * Para "cambiar" un dato se crea una copia nueva con `copy(...)`.
 */
data class PreferenciaViaje(
    val nombreViajero: String = "",
    val tipoTransporte: TipoTransporte = TipoTransporte.TREN,
    val soloBajaHuella: Boolean = false,
)

/**
 * Convierte un texto guardado (por ejemplo "BICICLETA") en el enum.
 * Si el texto es nulo o no existe, usa TREN gracias al operador `?:` (elvis).
 */
fun transporteDesdeTexto(texto: String?): TipoTransporte =
    TipoTransporte.entries.firstOrNull { it.name == texto } ?: TipoTransporte.TREN
