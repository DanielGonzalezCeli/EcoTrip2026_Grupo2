package ec.edu.puce.ecotrip.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ec.edu.puce.ecotrip.R
import ec.edu.puce.ecotrip.data.TipoTransporte
import ec.edu.puce.ecotrip.navigation.RutaResumen

/** Ícono que representa a cada medio de transporte. */
private fun TipoTransporte.icono(): ImageVector = when (this) {
    TipoTransporte.TREN -> Icons.Filled.Train
    TipoTransporte.BICICLETA -> Icons.Filled.DirectionsBike
    TipoTransporte.VEHICULO_ELECTRICO -> Icons.Filled.ElectricCar
}

/**
 * PANTALLA 2: Resumen de la ruta.
 *
 * Recibe el objeto tipado `RutaResumen` y lo muestra. Todos los colores salen de
 * MaterialTheme.colorScheme (roles semánticos), por eso cambian solos con el
 * Color Dinámico (Material You) al cambiar el fondo de pantalla del teléfono.
 *
 *  - `alVolver`: flecha de la barra; regresa al formulario CON los datos para editarlos.
 *  - `alNuevaRuta`: botón flotante; regresa al formulario con el viaje en blanco.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    ruta: RutaResumen,
    alVolver: () -> Unit,
    alNuevaRuta: () -> Unit,
) {
    // Textos preparados con plantillas de cadena de Kotlin ("${...}").
    val viajero = ruta.nombreViajero.ifBlank { stringResource(R.string.viajero_anonimo) }
    val transporte = stringResource(ruta.tipoTransporte.idDelTexto())
    val unidadDias = stringResource(R.string.unidad_dias)
    val tipoDeViaje = stringResource(
        if (ruta.esViajeGrupal) R.string.viaje_grupal else R.string.viaje_individual,
    )
    val bajaHuella = stringResource(if (ruta.soloBajaHuella) R.string.si else R.string.no)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_resumen)) },
                // Botón de salida en la barra superior.
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_volver),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = alNuevaRuta,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                icon = { Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.cd_nueva_ruta)) },
                text = { Text(stringResource(R.string.fab_nueva_ruta)) },
            )
        },
    ) { paddingDelScaffold ->

        Column(
            modifier = Modifier
                .padding(paddingDelScaffold)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Tarjeta de cabecera: destino grande + duración y tipo de viaje.
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        imageVector = ruta.tipoTransporte.icono(),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                    Column {
                        Text(text = ruta.destino, style = MaterialTheme.typography.headlineMedium)
                        Text(
                            text = stringResource(R.string.resumen_subtitulo, ruta.diasDuracion, tipoDeViaje),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }

            // Tarjeta con el detalle, una fila por dato.
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    FilaDeDetalle(Icons.Filled.Person, stringResource(R.string.resumen_viajero), viajero)
                    FilaDeDetalle(ruta.tipoTransporte.icono(), stringResource(R.string.resumen_transporte), transporte)
                    FilaDeDetalle(
                        Icons.Filled.DateRange,
                        stringResource(R.string.resumen_duracion),
                        "${ruta.diasDuracion} $unidadDias",
                    )
                    FilaDeDetalle(Icons.Filled.Groups, stringResource(R.string.resumen_tipo), tipoDeViaje)
                    FilaDeDetalle(Icons.Filled.Eco, stringResource(R.string.resumen_huella), bajaHuella)
                }
            }

            // Mensaje ecológico: solo aparece si eligió baja huella de carbono.
            if (ruta.soloBajaHuella) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(Icons.Filled.Eco, contentDescription = null)
                        Text(
                            text = stringResource(R.string.mensaje_eco),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }

            // Espacio al final para que el botón flotante no tape la última tarjeta.
            Spacer(Modifier.height(80.dp))
        }
    }
}

/** Una fila del detalle: ícono a la izquierda, etiqueta pequeña y valor debajo. */
@Composable
private fun FilaDeDetalle(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(imageVector = icono, contentDescription = null, modifier = Modifier.size(24.dp))
        Column {
            Text(text = etiqueta, style = MaterialTheme.typography.labelMedium)
            Text(text = valor, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
