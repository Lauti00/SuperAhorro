package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorroniccolinibenitez.R
import com.undef.superahorroniccolinibenitez.model.Supermercado
import com.undef.superahorroniccolinibenitez.ui.components.*
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel
import com.undef.superahorroniccolinibenitez.ui.viewmodel.NuevoSupermercadoViewModel

@Composable
fun NuevoSupermercadoScreen(
    homeViewModel: HomeViewModel,
    nuevoSupermercadoViewModel: NuevoSupermercadoViewModel = viewModel(),
    onBack: () -> Unit
) {
    val state by nuevoSupermercadoViewModel.uiState.collectAsState()
    val supermercados by homeViewModel.supermercados.collectAsState()

    SimpleScreenContainer(
        title = stringResource(id = R.string.title_gestionar_supermercados),
        onBack = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            SuperAhorroCard {

                SuperAhorroSectionTitle(
                    title = if (state.idSupermercadoEditando == null) {
                        stringResource(id = R.string.title_agregar_supermercado)
                    } else {
                        stringResource(id = R.string.title_editando_supermercado, state.nombre)
                    },
                    subtitle = stringResource(id = R.string.supermercado_form_subtitle)
                )

                EspacioNormal()

                SuperAhorroTextField(
                    value = state.nombre,
                    onValueChange = { nuevoSupermercadoViewModel.onNombreChange(it) },
                    label = stringResource(id = R.string.placeholder_nombre_supermercado)
                )

                EspacioNormal()

                if (state.error.isNotEmpty()) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    EspacioNormal()
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuperAhorroButton(
                        text = if (state.idSupermercadoEditando == null) {
                            stringResource(id = R.string.btn_guardar_supermercado)
                        } else {
                            stringResource(id = R.string.btn_actualizar)
                        },
                        modifier = Modifier.weight(1f),
                        onClick = {
                            nuevoSupermercadoViewModel.validarYGuardar(
                                supermercadosExistentes = supermercados,
                                onCrear = { nombre ->
                                    homeViewModel.agregarSupermercado(nombre)
                                },
                                onEditar = { id, nombre ->
                                    homeViewModel.actualizarSupermercado(id, nombre)
                                    nuevoSupermercadoViewModel.cancelarEdicion()
                                }
                            )
                        }
                    )

                    if (state.idSupermercadoEditando != null) {
                        OutlinedButton(
                            onClick = { nuevoSupermercadoViewModel.cancelarEdicion() },
                            modifier = Modifier.height(50.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(stringResource(id = R.string.btn_cancelar_abreviado))
                        }
                    }
                }
            }

            EspacioGrande()

            SuperAhorroSectionTitle(
                title = stringResource(id = R.string.label_supermercados_en_lista, supermercados.size),
                subtitle = stringResource(id = R.string.supermercado_list_subtitle)
            )

            EspacioNormal()

            if (supermercados.isEmpty()) {
                SuperAhorroCard {
                    Text(
                        text = stringResource(id = R.string.msg_lista_supermercados_vacia),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                supermercados.forEach { supermercado ->
                    SupermercadoItem(
                        supermercado = supermercado,
                        onEliminar = { homeViewModel.eliminarSupermercado(supermercado) },
                        onEditar = { nuevoSupermercadoViewModel.cargarParaEdicion(supermercado) }
                    )
                    EspacioPequeño()
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun SupermercadoItem(
    supermercado: Supermercado,
    onEliminar: () -> Unit,
    onEditar: () -> Unit
) {
    SuperAhorroCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = supermercado.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Row {
                IconButton(onClick = onEditar) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(id = R.string.cd_editar),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onEliminar) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.cd_eliminar),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
