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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorroniccolinibenitez.model.CatalogoProducto
import com.undef.superahorroniccolinibenitez.ui.components.*
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel
import com.undef.superahorroniccolinibenitez.ui.viewmodel.NuevoProductoViewModel

@Composable
fun NuevoProductoScreen(
    homeViewModel: HomeViewModel,
    nuevoProductoViewModel: NuevoProductoViewModel = viewModel(),
    onBack: () -> Unit
) {
    val state by nuevoProductoViewModel.uiState.collectAsState()
    val catalogo = homeViewModel.catalogo

    SimpleScreenContainer(title = "Gestionar Catálogo", onBack = onBack) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (state.idProductoEditando == null) "Agregar nuevo producto" else "Editando: ${state.nombre}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (state.idProductoEditando == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )

            EspacioNormal()

            SuperAhorroTextField(
                value = state.codigo,
                onValueChange = { nuevoProductoViewModel.onCodigoChange(it) },
                label = "Código (ej: 779...)"
            )

            EspacioNormal()

            SuperAhorroTextField(
                value = state.nombre,
                onValueChange = { nuevoProductoViewModel.onNombreChange(it) },
                label = "Nombre del producto (ej: Leche)"
            )

            EspacioNormal()

            SuperAhorroTextField(
                value = state.descripcion,
                onValueChange = { nuevoProductoViewModel.onDescripcionChange(it) },
                label = "Descripción"
            )

            EspacioNormal()

            SuperAhorroTextField(
                value = state.precio,
                onValueChange = { nuevoProductoViewModel.onPrecioChange(it) },
                label = "Precio (ej: 1200.50)"
            )

            EspacioNormal()

            if (state.error.isNotEmpty()) {
                Text(text = state.error, color = MaterialTheme.colorScheme.error)
                EspacioNormal()
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuperAhorroButton(
                    text = if (state.idProductoEditando == null) "Guardar Producto" else "Actualizar",
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
                        Text("X")
                    }
                }
            }

            EspacioGrande()

            Text(
                text = "Productos en lista (${catalogo.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            EspacioNormal()

            if (catalogo.isEmpty()) {
                Text("El catálogo está vacío.", style = MaterialTheme.typography.bodyMedium)
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
                Text(text = "${producto.codigo} - ${producto.descripcion}", style = MaterialTheme.typography.bodySmall)
                Text(text = "$${"%.2f".format(producto.precio)}", style = MaterialTheme.typography.bodyMedium)
            }

            Row {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}