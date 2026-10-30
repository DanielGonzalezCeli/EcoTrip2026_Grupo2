package ec.edu.puce.ecotrip.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ec.edu.puce.ecotrip.ui.EcoTripViewModel
import ec.edu.puce.ecotrip.ui.screens.FormularioScreen
import ec.edu.puce.ecotrip.ui.screens.ResumenScreen

/**
 * Único NavHost de la app. Aquí se "elevan" (State Hoisting) el ViewModel y el
 * NavController; las pantallas solo reciben datos y funciones, no conocen a ninguno.
 */
@Composable
fun EcoTripNavHost() {
    val navController = rememberNavController()
    val viewModel: EcoTripViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = RutaFormulario,
    ) {
        // ----- Pantalla 1: Formulario -----
        composable<RutaFormulario> {
            val estado by viewModel.estado.collectAsStateWithLifecycle()

            FormularioScreen(
                estado = estado,
                alCambiarNombre = { texto -> viewModel.cambiarNombre(texto) },
                alCambiarTransporte = { tipo -> viewModel.cambiarTransporte(tipo) },
                alCambiarBajaHuella = { activado -> viewModel.cambiarSoloBajaHuella(activado) },
                alCambiarDestino = { texto -> viewModel.cambiarDestino(texto) },
                alCambiarDias = { texto -> viewModel.cambiarDias(texto) },
                alCambiarViajeGrupal = { activado -> viewModel.cambiarViajeGrupal(activado) },
                alPlanificar = {
                    // Si los datos no son válidos devuelve null y no se navega.
                    val rutaResumen = viewModel.crearRutaResumen()
                    if (rutaResumen != null) {
                        navController.navigate(rutaResumen)
                    }
                },
            )
        }

        // ----- Pantalla 2: Resumen -----
        composable<RutaResumen> { entrada ->
            // toRoute() reconstruye el objeto tipado: sin Strings ni Bundles manuales.
            val rutaResumen = entrada.toRoute<RutaResumen>()

            ResumenScreen(
                ruta = rutaResumen,
                // Flecha de la barra: vuelve al formulario CON los datos para poder editarlos.
                alVolver = { navController.popBackStack() },
                // Botón "Nueva ruta": borra el viaje anterior y deja el formulario en blanco.
                alNuevaRuta = {
                    viewModel.nuevaRuta()
                    navController.navigate(RutaFormulario) {
                        // Limpia la pila: borra el resumen y deja el formulario arriba.
                        popUpTo<RutaFormulario> { inclusive = false }
                        // Evita crear una segunda copia del formulario.
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}