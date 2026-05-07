package com.example.superahorro.ui.screens

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
import com.example.superahorro.model.CatalogoProducto
import com.example.superahorro.ui.components.*
import com.example.superahorro.ui.viewmodel.HomeViewModel

@Composable
fun NuevoProductoScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    // ESTADOS DE INPUT
    var codigo by remember { mutableStateOf("") } // NUEVO
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") } // NUEVO
    var precio by remember { mutableStateOf("") }

    // ESTADO PARA EDICIÓN
    // Si es null, estamos creando. Si tiene un ID, estamos editando.
    var idProductoEditando by remember { mutableStateOf<Int?>(null) }

    // ESTADO ERROR
    var error by remember { mutableStateOf("") }

    val catalogo = viewModel.catalogo

    SimpleScreenContainer(
        title = "Gestionar Catálogo",
        onBack = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            /*
            FORMULARIO DE CARGA / EDICIÓN
            */
            Text(
                text = if (idProductoEditando == null) "Agregar nuevo producto" else "Editando: $nombre",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (idProductoEditando == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )

            EspacioNormal()

            // NUEVO: Campo de Código
            SuperAhorroTextField(
                value = codigo,
                onValueChange = { codigo = it; error = "" },
                label = "Código (ej: 779...)"
            )

            EspacioNormal()

            SuperAhorroTextField(
                value = nombre,
                onValueChange = { nombre = it; error = "" },
                label = "Nombre del producto (ej: Leche)"
            )

            EspacioNormal()

            // NUEVO: Campo de Descripción
            SuperAhorroTextField(
                value = descripcion,
                onValueChange = { descripcion = it; error = "" },
                label = "Descripción"
            )

            EspacioNormal()

            SuperAhorroTextField(
                value = precio,
                onValueChange = {
                    // Permitimos solo números y un punto/coma
                    if (it.all { char -> char.isDigit() || char == '.' || char == ',' }) {
                        precio = it
                        error = ""
                    }
                },
                label = "Precio (ej: 1200.50)"
            )

            EspacioNormal()

            if (error.isNotEmpty()) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
                EspacioNormal()
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // BOTÓN PRINCIPAL
                SuperAhorroButton(
                    text = if (idProductoEditando == null) "Guardar Producto" else "Actualizar",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        // 1. Limpieza de datos
                        val codigoTrim = codigo.trim()
                        val nombreTrim = nombre.trim()
                        val descripcionTrim = descripcion.trim()
                        val precioLimpio = precio.replace(",", ".") // Cambiamos coma por punto por las dudas
                        val precioDouble = precioLimpio.toDoubleOrNull()

                        // 2. VALIDACIONES
                        when {
                            codigoTrim.isEmpty() -> error = "El código no puede estar vacío"
                            nombreTrim.isEmpty() -> error = "El nombre no puede estar vacío"
                            nombreTrim.length < 2 -> error = "Nombre demasiado corto"
                            descripcionTrim.isEmpty() -> error = "La descripción no puede estar vacía"
                            precioLimpio.isEmpty() -> error = "Debes ingresar un precio"
                            precioDouble == null -> error = "Formato de precio inválido"
                            precioDouble <= 0 -> error = "El precio debe ser mayor a 0"
                            else -> {
                                if (idProductoEditando == null) {
                                    // MODO CREAR
                                    val agregado = viewModel.agregarProductoAlCatalogo(
                                        codigo = codigoTrim,
                                        nombre = nombreTrim,
                                        descripcion = descripcionTrim,
                                        precio = precioDouble
                                    )
                                    if (!agregado) error = "Ese producto ya existe"
                                } else {
                                    // MODO EDITAR
                                    viewModel.actualizarProductoDelCatalogo(
                                        id = idProductoEditando!!,
                                        nuevoCodigo = codigoTrim,
                                        nuevoNombre = nombreTrim,
                                        nuevaDescripcion = descripcionTrim,
                                        nuevoPrecio = precioDouble
                                    )
                                    idProductoEditando = null
                                }

                                if (error.isEmpty()) {
                                    // Limpiamos todo al terminar
                                    codigo = ""; nombre = ""; descripcion = ""; precio = ""; error = ""
                                }
                            }
                        }
                    }
                )

                // BOTÓN CANCELAR (Solo aparece al editar)
                if (idProductoEditando != null) {
                    OutlinedButton(
                        onClick = {
                            idProductoEditando = null
                            codigo = ""
                            nombre = ""
                            descripcion = ""
                            precio = ""
                            error = ""
                        },
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("X")
                    }
                }
            }

            EspacioGrande()

            /*
            LISTA DE PRODUCTOS ACTUALES
            */
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
                        onEliminar = { viewModel.eliminarProductoDelCatalogo(producto) },
                        onEditar = {
                            // Subimos los datos al formulario para editar
                            idProductoEditando = producto.id
                            codigo = producto.codigo
                            nombre = producto.nombre
                            descripcion = producto.descripcion
                            precio = producto.precio.toString()
                            error = ""
                        }
                    )
                    EspacioPequeño()
                }
            }

            // Espacio final para que el scroll sea cómodo
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                // Agregamos código y descripción a la vista
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