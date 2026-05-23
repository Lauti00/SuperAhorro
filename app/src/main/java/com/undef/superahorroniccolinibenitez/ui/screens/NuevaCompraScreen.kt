package com.undef.superahorroniccolinibenitez.ui.screens

import android.content.Context
import android.net.Uri
import android.os.Build
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorroniccolinibenitez.ui.components.*
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel
import com.undef.superahorroniccolinibenitez.ui.viewmodel.NuevaCompraViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NuevaCompraScreen(
    homeViewModel: HomeViewModel,
    nuevaCompraViewModel: NuevaCompraViewModel = viewModel(),
    onBack: () -> Unit,
    onCompraGuardada: () -> Unit,
    onNavigateToNuevoProducto: () -> Unit
) {
    val localContext = LocalContext.current

    // Observamos el estado del nuevo ViewModel
    val state by nuevaCompraViewModel.uiState.collectAsState()
    val catalogo by homeViewModel.catalogo.collectAsState()

    // Datos automáticos solo para mostrar en UI (el guardado real pasa en ViewModel)
    val fechaDisplay = remember { LocalDate.now().toString() }
    val horaDisplay = remember { LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> nuevaCompraViewModel.onImagenUriChange(uri) }

    fun crearImagenUri(ctx: Context): Uri {
        val file = File.createTempFile("ticket_", ".jpg", ctx.cacheDir)
        return FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", file)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { }

    SimpleScreenContainer(title = "Nueva Compra", onBack = onBack) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 4.dp)
            ) {
                SuperAhorroTextField(
                    value = state.supermercado,
                    onValueChange = { nuevaCompraViewModel.onSupermercadoChange(it) },
                    label = "Supermercado"
                )

                EspacioPequeño()
                Text("Fecha: $fechaDisplay - Hora: $horaDisplay", style = MaterialTheme.typography.bodyMedium)
                EspacioNormal()

                ExposedDropdownMenuBox(
                    expanded = state.expanded,
                    onExpandedChange = { nuevaCompraViewModel.onExpandedChange(!state.expanded) }
                ) {
                    OutlinedTextField(
                        value = state.productoSeleccionado?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleccionar producto") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = state.expanded,
                        onDismissRequest = { nuevaCompraViewModel.onExpandedChange(false) }
                    ) {
                        catalogo.forEach { producto ->
                            DropdownMenuItem(
                                text = { Text("${producto.nombre} - $${producto.precio}") },
                                onClick = { nuevaCompraViewModel.onProductoSeleccionado(producto) }
                            )
                        }
                    }
                }

                SuperAhorroTextButton(
                    text = "¿No está en la lista? Crear producto",
                    onClick = onNavigateToNuevoProducto
                )

                SuperAhorroTextField(
                    value = state.cantidadProducto,
                    onValueChange = { nuevaCompraViewModel.onCantidadChange(it) },
                    label = "Cantidad"
                )

                EspacioPequeño()

                SuperAhorroButton(
                    text = "Agregar producto",
                    onClick = { nuevaCompraViewModel.agregarProductoLocal() }
                )

                if (state.errorProducto.isNotEmpty()) {
                    Text(state.errorProducto, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }

                EspacioNormal()

                if (state.productos.isNotEmpty()) {
                    Text("Productos agregados:", style = MaterialTheme.typography.titleSmall)
                    EspacioPequeño()

                    state.productos.forEach { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.producto.nombre, style = MaterialTheme.typography.bodyLarge)
                                    Text("${item.cantidad} x $${item.producto.precio} = $${"%.2f".format(item.subtotal())}",
                                        style = MaterialTheme.typography.bodySmall)
                                }

                                IconButton(onClick = { nuevaCompraViewModel.editarProductoLocal(item) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { nuevaCompraViewModel.eliminarProductoLocal(item) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }

                EspacioNormal()
                Text("Total: $${"%.2f".format(state.totalCalculado)}", style = MaterialTheme.typography.headlineSmall)

                state.imagenUri?.let { uri ->
                    EspacioNormal()
                    Text("Ticket cargado:", style = MaterialTheme.typography.labelLarge)
                    coil.compose.AsyncImage(
                        model = uri,
                        contentDescription = "Ticket",
                        modifier = Modifier.fillMaxWidth().height(150.dp).padding(vertical = 8.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                if (state.errorGeneral.isNotEmpty()) {
                    Text(state.errorGeneral, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.CenterHorizontally))
                    EspacioPequeño()
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuperAhorroButton(text = "Galería", onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.weight(1f))
                    SuperAhorroButton(text = "Cámara", onClick = {
                        val uri = crearImagenUri(localContext)
                        nuevaCompraViewModel.onImagenUriChange(uri)
                        cameraLauncher.launch(uri)
                    }, modifier = Modifier.weight(1f))
                }

                EspacioPequeño()

                SuperAhorroButton(
                    text = "Guardar Compra",
                    onClick = {
                        val nuevoId = homeViewModel.compras.value.size + 1
                        nuevaCompraViewModel.validarYGuardar(idNuevaCompra = nuevoId) { compraLista ->
                            homeViewModel.agregarCompra(compraLista)
                            onCompraGuardada()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}