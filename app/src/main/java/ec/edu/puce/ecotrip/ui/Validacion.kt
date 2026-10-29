package ec.edu.puce.ecotrip.ui

private const val DIAS_MINIMO = 1
private const val DIAS_MAXIMO = 365
private const val MAXIMO_DIGITOS = 3
private const val MINIMO_LETRAS = 2

/** Largo máximo de los campos de texto (también se muestra en el contador). */
const val MAXIMO_NOMBRE = 40
const val MAXIMO_DESTINO = 50

/** Quita los espacios repetidos y los espacios del principio. */
private fun ordenarEspacios(texto: String): String =
    texto.replace(Regex(" {2,}"), " ").trimStart()

/**
 * Se ejecuta MIENTRAS el usuario escribe el nombre: solo deja letras
 * (con tildes y ñ), espacios, apóstrofe y guion. Los números y símbolos
 * nunca llegan al estado.
 */
fun limpiarNombre(textoEscrito: String): String =
    ordenarEspacios(
        textoEscrito.filter { it.isLetter() || it == ' ' || it == '\'' || it == '-' },
    ).take(MAXIMO_NOMBRE)

/** El nombre es válido si tiene al menos 2 letras. */
fun nombreValido(texto: String): Boolean = texto.count { it.isLetter() } >= MINIMO_LETRAS

/**
 * Igual que el nombre, pero el destino también admite coma y punto
 * (por ejemplo "Quito, Ecuador").
 */
fun limpiarDestino(textoEscrito: String): String =
    ordenarEspacios(
        textoEscrito.filter {
            it.isLetter() || it == ' ' || it == '\'' || it == '-' || it == ',' || it == '.'
        },
    ).take(MAXIMO_DESTINO)

/** El destino es válido si tiene al menos 2 letras (no basta con puntos o comas). */
fun destinoValido(texto: String): Boolean = texto.count { it.isLetter() } >= MINIMO_LETRAS

/**
 * Se ejecuta MIENTRAS el usuario escribe: deja solo los dígitos 0-9
 * (máximo 3), así nunca llegan letras ni símbolos al estado.
 */
fun limpiarDias(textoEscrito: String): String =
    textoEscrito.filter { it in '0'..'9' }.take(MAXIMO_DIGITOS)

/**
 * Convierte el texto a un número de días válido (1 a 365).
 * Devuelve null si el texto está vacío, no es número o está fuera de rango.
 */
fun diasValidos(texto: String): Int? {
    val dias = texto.toIntOrNull() ?: return null // si no es número -> null, sin crash
    return if (dias in DIAS_MINIMO..DIAS_MAXIMO) dias else null
}
