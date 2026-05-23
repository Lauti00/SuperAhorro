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
import com.undef.superahorroniccolinibenitez.model.CatalogoProducto
import com.undef.superahorroniccolinibenitez.ui.components.*
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel
import com.undef.superahorroniccolinibenitez.ui.viewmodel.NuevoProductoViewModel
import com.undef.superahorroniccolinibenitez.R

@Composable
fun NuevoProductoScreen(
    homeViewModel: HomeViewModel,
    nuevoProductoViewModel: NuevoProductoViewModel = viewModel(),
    onBack: () -> Unit
) {
    val state by nuevoProductoViewModel.uiState.collectAsState()
    val catalogo by homeViewModel.catalogo.collectAsState()

    SimpleScreenContainer(title = stringResource(id = R.string.title_gestionar_catalogo), onBack = onBack) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
        ) {

            Text(
                text = if (state.idProductoEditando == null) {
                    stringResource(id = R.string.title_agregar_nuevo_producto)
                } else {
                    stringResource(id = R.string.title_editando_producto, state.nombre)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (state.idProductoEditando == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )

            EspacioNormal()

            SuperAhorroTextField(
                value = state.codigo,
                onValueChange = { nuevoProductoViewModel.onCodigoChange(it) },
                label = stringResource(id = R.string.label_codigo_placeholder)
            )

            EspacioNormal()

            SuperAhorroTextField(
                value = state.nombre,
                onValueChange = { nuevoProductoViewModel.onNombreChange(it) },
                label = stringResource(id = R.string.label_nombre_placeholder)
            )

            EspacioNormal()

            SuperAhorroTextField(
                value = state.descripcion,
                onValueChange = { nuevoProductoViewModel.onDescripcionChange(it)},
                label = stringResource(id = R.string.label_descripcion)
            )

            EspacioNormal()

            SuperAhorroTextField(
                value = state.precio,
                onValueChange = { nuevoProductoViewModel.onPrecioChange(it) },
                label = stringResource(id = R.string.label_precio_placeholder)
            )

            EspacioNormal()

            if (state.error.isNotEmpty()) {
                Text(text = state.error, color = MaterialTheme.colorScheme.error)
                EspacioNormal()
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuperAhorroButton(

                    text = if (state.idProductoEditando == null) {
                        stringResource(id = R.string.btn_guardar_producto)
                    } else {
                        stringResource(id = R.string.btn_actualizar)
                    },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        nuevoProductoViewModel.validarYGuardar(
                            catalogoExistente = catalogo,
                            onCrear = { codigo, nombre, descripcion, precio ->
                                homeViewModel.agregarProductoAlCatalogo(codigo, nombre, descripcion, precio)
                            },
                            onEditar = { id, codigo, nombre, descripcion, precio ->
                                homeViewModel.actualizarProductoDelCatalogo(id, codigo, nombre, descripcion, precio)
                            }
                        )
                    }
                )

                if (state.idProductoEditando != null) {
                    OutlinedButton(
                        onClick = { nuevoProductoViewModel.cancelarEdicion() },
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(stringResource(id = R.string.btn_cancelar_abreviado))
                    }
                }
            }

            EspacioGrande()

            Text(
                text = stringResource(id = R.string.label_productos_en_lista, catalogo.size),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            EspacioNormal()

            if (catalogo.isEmpty()) {
                Text(stringResource(id = R.string.msg_catalogo_vacio), style = MaterialTheme.typography.bodyMedium)
            } else {
                catalogo.forEach { producto ->
                    ProductoCatalogoItem(
                        producto = producto,
                        onEliminar = { homeViewModel.eliminarProductoDelCatalogo(producto) },
                        onEditar = { nuevoProductoViewModel.cargarParaEdicion(producto) }
                    )
                    EspacioPequeño()
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun ProductoCatalogoItem(
    producto: CatalogoProducto,
    onEliminar: () -> Unit,
    onEditar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Text(
                    text = stringResource(id = R.string.label_formato_codigo_descripcion, producto.codigo, producto.descripcion),
                    style = MaterialTheme.typography.bodySmall
                )

                val precioFormateado = "%.2f".format(producto.precio)
                Text(
                    text = stringResource(id = R.string.label_formato_precio_catalogo, precioFormateado),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

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