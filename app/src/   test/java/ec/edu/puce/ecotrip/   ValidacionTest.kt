package ec.edu.puce.ecotrip

import ec.edu.puce.ecotrip.ui.MAXIMO_NOMBRE
import ec.edu.puce.ecotrip.ui.destinoValido
import ec.edu.puce.ecotrip.ui.diasValidos
import ec.edu.puce.ecotrip.ui.limpiarDestino
import ec.edu.puce.ecotrip.ui.limpiarDias
import ec.edu.puce.ecotrip.ui.limpiarNombre
import ec.edu.puce.ecotrip.ui.nombreValido
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionTest {

    // ---------- Días ----------

    @Test
    fun limpiarDias_quitaLetrasYSimbolos() {
        assertEquals("12", limpiarDias("1a2-"))
    }

    @Test
    fun limpiarDias_permiteMaximoTresDigitos() {
        assertEquals("123", limpiarDias("123456"))
    }

    @Test
    fun diasValidos_aceptaDiasEntre1y365() {
        assertEquals(7, diasValidos("7"))
        assertEquals(365, diasValidos("365"))
    }

    @Test
    fun diasValidos_devuelveNullConDatosAnomalos() {
        assertNull(diasValidos(""))
        assertNull(diasValidos("0"))
        assertNull(diasValidos("366"))
        assertNull(diasValidos("abc"))
        assertNull(diasValidos("99999999999"))
    }

    // ---------- Nombre ----------

    @Test
    fun limpiarNombre_quitaNumerosYSimbolos() {
        assertEquals("Dilan Mena", limpiarNombre("Dilan 123 Mena@#"))
    }

    @Test
    fun limpiarNombre_aceptaTildesYEnie() {
        assertEquals("José Núñez", limpiarNombre("José Núñez"))
    }

    @Test
    fun limpiarNombre_respetaElLargoMaximo() {
        assertEquals(MAXIMO_NOMBRE, limpiarNombre("a".repeat(100)).length)
    }

    @Test
    fun limpiarNombre_quitaEspaciosRepetidosYDelInicio() {
        assertEquals("Ana Paz", limpiarNombre("  Ana   Paz"))
    }

    @Test
    fun nombreValido_pideAlMenosDosLetras() {
        assertTrue(nombreValido("Al"))
        assertFalse(nombreValido(""))
        assertFalse(nombreValido("A"))
        assertFalse(nombreValido("   "))
        assertFalse(nombreValido("123"))
    }

    // ---------- Destino ----------

    @Test
    fun limpiarDestino_quitaNumerosPeroDejaComaYPunto() {
        assertEquals("Quito, Ecuador", limpiarDestino("Quito, Ecuador 123"))
    }

    @Test
    fun destinoValido_rechazaTextoSinLetras() {
        assertTrue(destinoValido("Cuenca"))
        assertFalse(destinoValido(""))
        assertFalse(destinoValido("..."))
        assertFalse(destinoValido("1"))
    }
}