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
import com.undef.superahorroniccolinibenitez.ui.components.*
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel
import com.undef.superahorroniccolinibenitez.model.Compra
import com.undef.superahorroniccolinibenitez.model.Producto
import com.undef.superahorroniccolinibenitez.model.CatalogoProducto

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NuevaCompraScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    onCompraGuardada: () -> Unit,
    onNavigateToNuevoProducto: () -> Unit
) {
    val localContext = LocalContext.current

    //  Estado del supermercado
    var supermercado by remember { mutableStateOf("") }

    // Fecha automática
    val fecha = remember { java.time.LocalDate.now().toString() }

    // Hora automática (formato HH:mm)
    val hora = remember {
        java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
    }

    // Lista de productos (estado dinámico)
    val productos = remember { mutableStateListOf<Producto>() }

    // Catálogo dinámico del ViewModel
    val catalogo = viewModel.catalogo

    // Estados de selección y UI
    var productoSeleccionado by remember { mutableStateOf<CatalogoProducto?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var cantidadProducto by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    // ESTADOS DE ERROR
    var errorGeneral by remember { mutableStateOf("") }
    var errorProducto by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imagenUri = uri }

    val totalCalculado = productos.sumOf { it.subtotal() }

    fun crearImagenUri(ctx: Context): Uri {
        val file = File.createTempFile("ticket_", ".jpg", ctx.cacheDir)
        return FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", file)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { }

    SimpleScreenContainer(
        title = "Nueva Compra",
        onBack = onBack
    ) {
        // Columna principal que ocupa toda la pantalla
        Column(modifier = Modifier.fillMaxSize()) {

            // AREA SCROLLEABLE: Aquí va todo el contenido que puede ser largo
            Column(
                modifier = Modifier
                    .weight(1f) // Esto hace que esta parte use el espacio sobrante
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp)
            ) {
                // 1. Datos del Supermercado
                SuperAhorroTextField(
                    value = supermercado,
                    onValueChange = { supermercado = it; errorGeneral = "" },
                    label = "Supermercado"
                )

                EspacioPequeño()
                // NUEVO: Mostramos la fecha y la hora
                Text("Fecha: $fecha - Hora: $hora", style = MaterialTheme.typography.bodyMedium)
                EspacioNormal()

                // 2. Selector de Productos
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = productoSeleccionado?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleccionar producto") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        catalogo.forEach { producto ->
                            DropdownMenuItem(
                                text = { Text("${producto.nombre} - $${producto.precio}") },
                                onClick = {
                                    productoSeleccionado = producto
                                    expanded = false
                                    errorProducto = ""
                                }
                            )
                        }
                    }
                }

                SuperAhorroTextButton(
                    text = "¿No está en la lista? Crear producto",
                    onClick = onNavigateToNuevoProducto
                )

                // 3. Cantidad y Botón Agregar
                SuperAhorroTextField(
                    value = cantidadProducto,
                    onValueChange = { cantidadProducto = it; errorProducto = "" },
                    label = "Cantidad"
                )

                EspacioPequeño()

                SuperAhorroButton(
                    text = "Agregar producto",
                    onClick = {
                        val cantidad = cantidadProducto.toIntOrNull()
                        if (productoSeleccionado == null) {
                            errorProducto = "Seleccioná un producto"
                        } else if (cantidad == null || cantidad <= 0) {
                            errorProducto = "Cantidad inválida"
                        } else {
                            productos.add(Producto(producto = productoSeleccionado!!, cantidad = cantidad))
                            productoSeleccionado = null
                            cantidadProducto = ""
                            errorProducto = ""
                        }
                    }
                )

                if (errorProducto.isNotEmpty()) {
                    Text(errorProducto, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }

                EspacioNormal()

                // 4. LISTA DE PRODUCTOS AGREGADOS
                if (productos.isNotEmpty()) {
                    Text("Productos agregados:", style = MaterialTheme.typography.titleSmall)
                    EspacioPequeño()

                    productos.forEach { item ->
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

                                // Botón Editar
                                IconButton(onClick = {
                                    productoSeleccionado = item.producto
                                    cantidadProducto = item.cantidad.toString()
                                    productos.remove(item)
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                }

                                // Botón Eliminar
                                IconButton(onClick = { productos.remove(item) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }

                EspacioNormal()

                // 5. Total y Ticket
                Text("Total: $${"%.2f".format(totalCalculado)}", style = MaterialTheme.typography.headlineSmall)

                imagenUri?.let { uri ->
                    EspacioNormal()
                    Text("Ticket cargado:", style = MaterialTheme.typography.labelLarge)
                    coil.compose.AsyncImage(
                        model = uri,
                        contentDescription = "Ticket",
                        modifier = Modifier.fillMaxWidth().height(150.dp).padding(vertical = 8.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }

                // Espacio extra al final del scroll para que el último item no quede tapado por los botones
                Spacer(modifier = Modifier.height(20.dp))
            }

            // AREA FIJA ABAJO: Los botones de acción final
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                if (errorGeneral.isNotEmpty()) {
                    Text(errorGeneral, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.CenterHorizontally))
                    EspacioPequeño()
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuperAhorroButton(text = "Galería", onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.weight(1f))
                    SuperAhorroButton(text = "Cámara", onClick = {
                        val uri = crearImagenUri(localContext)
                        imagenUri = uri
                        cameraLauncher.launch(uri)
                    }, modifier = Modifier.weight(1f))
                }

                EspacioPequeño()

                SuperAhorroButton(
                    text = "Guardar Compra",
                    onClick = {
                        val superLimpio = supermercado.trim()

                        if (superLimpio.isEmpty()) {
                            errorGeneral = "El campo de supermercado no puede estar vacío"
                        } else if (productos.isEmpty()) {
                            errorGeneral = "Agregá al menos un producto"
                        } else {
                            // Normalizamos la primera letra en mayúscula
                            val superNormalizado = superLimpio.lowercase().replaceFirstChar { it.uppercase() }

                            val nuevaCompra = Compra(
                                id = viewModel.compras.size + 1,
                                supermercado = superNormalizado,
                                fecha = fecha,
                                hora = hora, // NUEVO: Pasamos la hora a la Compra
                                productos = productos.toList(),
                                imagenUri = imagenUri?.toString()
                            )
                            viewModel.agregarCompra(nuevaCompra)
                            onCompraGuardada()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}