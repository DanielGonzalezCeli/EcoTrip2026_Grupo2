package ec.edu.puce.ecotrip.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ec.edu.puce.ecotrip.R
import ec.edu.puce.ecotrip.data.TipoTransporte
import ec.edu.puce.ecotrip.ui.EstadoFormulario
import ec.edu.puce.ecotrip.ui.MAXIMO_DESTINO
import ec.edu.puce.ecotrip.ui.MAXIMO_NOMBRE

/** Devuelve el texto (de strings.xml) que corresponde a cada transporte. */
@StringRes
internal fun TipoTransporte.idDelTexto(): Int = when (this) {
    TipoTransporte.TREN -> R.string.transporte_tren
    TipoTransporte.BICICLETA -> R.string.transporte_bicicleta
    TipoTransporte.VEHICULO_ELECTRICO -> R.string.transporte_electrico
}

/**
 * PANTALLA 1: Formulario del viaje.
 *
 * Es una pantalla "sin estado propio" (State Hoisting):
 *  - los datos BAJAN: recibe `estado` ya listo para dibujar.
 *  - los eventos SUBEN: cada `alCambiar...` avisa al ViewModel (Flujo Unidireccional, UDF).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    estado: EstadoFormulario,
    alCambiarNombre: (String) -> Unit,
    alCambiarTransporte: (TipoTransporte) -> Unit,
    alCambiarBajaHuella: (Boolean) -> Unit,
    alCambiarDestino: (String) -> Unit,
    alCambiarDias: (String) -> Unit,
    alCambiarViajeGrupal: (Boolean) -> Unit,
    alPlanificar: () -> Unit,
) {
    // Scaffold = esqueleto de la pantalla: barra superior + botón flotante + contenido.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_formulario)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = alPlanificar,
                icon = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null) },
                text = { Text(stringResource(R.string.fab_planificar)) },
            )
        },
    ) { paddingDelScaffold ->

        Column(
            modifier = Modifier
                .padding(paddingDelScaffold)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ---------- Tarjeta 1: datos del viajero (preferencias globales) ----------
            TarjetaSeccion(titulo = stringResource(R.string.titulo_seccion_viajero)) {
                OutlinedTextField(
                    value = estado.nombreViajero,
                    onValueChange = alCambiarNombre,
                    label = { Text(stringResource(R.string.label_nombre)) },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    singleLine = true,
                    isError = estado.nombreConError,
                    supportingText = {
                        Text(
                            if (estado.nombreConError) {
                                stringResource(R.string.error_nombre)
                            } else {
                                stringResource(
                                    R.string.contador,
                                    estado.nombreViajero.length,
                                    MAXIMO_NOMBRE,
                                )
                            },
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                // Tipo de transporte (tres botones, solo uno seleccionado)
                Text(
                    text = stringResource(R.string.label_transporte),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    val todosLosTransportes = TipoTransporte.entries
                    todosLosTransportes.forEachIndexed { posicion, transporte ->
                        SegmentedButton(
                            selected = estado.tipoTransporte == transporte,
                            onClick = { alCambiarTransporte(transporte) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = posicion,
                                count = todosLosTransportes.size,
                            ),
                            label = { Text(stringResource(transporte.idDelTexto()), maxLines = 1) },
                        )
                    }
                }

                // Interruptor de baja huella de carbono
                FilaConInterruptor(
                    icono = Icons.Filled.Eco,
                    texto = stringResource(R.string.label_baja_huella),
                    activado = estado.soloBajaHuella,
                    alCambiar = alCambiarBajaHuella,
                )
            }

            // ---------- Tarjeta 2: detalles del viaje (cambian en cada ruta) ----------
            TarjetaSeccion(titulo = stringResource(R.string.titulo_seccion_viaje)) {
                OutlinedTextField(
                    value = estado.destino,
                    onValueChange = alCambiarDestino,
                    label = { Text(stringResource(R.string.label_destino)) },
                    leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null) },
                    singleLine = true,
                    isError = estado.destinoConError,
                    supportingText = {
                        Text(
                            if (estado.destinoConError) {
                                stringResource(R.string.error_destino)
                            } else {
                                stringResource(
                                    R.string.contador,
                                    estado.destino.length,
                                    MAXIMO_DESTINO,
                                )
                            },
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                // Días de duración (teclado numérico)
                OutlinedTextField(
                    value = estado.diasTexto,
                    onValueChange = alCambiarDias,
                    label = { Text(stringResource(R.string.label_dias)) },
                    leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    isError = estado.diasConError,
                    supportingText = {
                        Text(
                            stringResource(
                                if (estado.diasConError) R.string.error_dias else R.string.hint_dias,
                            ),
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Viaje grupal
                FilaConInterruptor(
                    icono = Icons.Filled.Groups,
                    texto = stringResource(R.string.label_grupal),
                    activado = estado.esViajeGrupal,
                    alCambiar = alCambiarViajeGrupal,
                )
            }

            // Espacio al final para que el botón flotante no tape el último campo.
            Spacer(Modifier.height(88.dp))
        }
    }
}

/** Tarjeta con un título de sección y su contenido adentro. */
@Composable
private fun TarjetaSeccion(
    titulo: String,
    contenido: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            contenido()
        }
    }
}

/** Una línea con ícono y texto a la izquierda y un interruptor (Switch) a la derecha. */
@Composable
private fun FilaConInterruptor(
    icono: ImageVector,
    texto: String,
    activado: Boolean,
    alCambiar: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Switch(checked = activado, onCheckedChange = alCambiar)
    }
}
