package com.example.superahorro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.superahorro.model.CatalogoProducto
import com.example.superahorro.ui.components.*
import com.example.superahorro.ui.viewmodel.HomeViewModel

@Composable
fun NuevoProductoScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    onProductoCreado: () -> Unit
) {

    /*
    ESTADOS DE INPUT
    */
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    /*
    ESTADO ERROR
    */
    var error by remember { mutableStateOf("") }

    /*
    CATÁLOGO ACTUAL
    */
    val catalogo = viewModel.catalogo

    SimpleScreenContainer(
        title = "Gestionar Productos",
        onBack = onBack
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            /*
            TÍTULO
            */
            Text(
                text = "Agregar nuevo producto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            EspacioNormal()

            /*
            INPUT NOMBRE
            */
            SuperAhorroTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    error = ""
                },
                label = "Nombre del producto"
            )

            EspacioNormal()

            /*
            INPUT PRECIO
            */
            SuperAhorroTextField(
                value = precio,
                onValueChange = {
                    precio = it
                    error = ""
                },
                label = "Precio"
            )

            EspacioNormal()

            /*
            ERROR
            */
            if (error.isNotEmpty()) {

                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )

                EspacioNormal()
            }

            /*
            BOTÓN GUARDAR
            */
            SuperAhorroButton(
                text = "Guardar Producto",
                onClick = {

                    val precioDouble = precio.toDoubleOrNull()

                    when {

                        nombre.trim().length < 2 -> {
                            error = "El nombre es demasiado corto"
                        }

                        precioDouble == null -> {
                            error = "Precio inválido"
                        }

                        precioDouble <= 0 -> {
                            error = "El precio debe ser mayor a 0"
                        }

                        precioDouble > 999999 -> {
                            error = "Precio demasiado alto"
                        }

                        else -> {

                            /*
                            Intentamos agregar
                            */
                            val agregado =
                                viewModel.agregarProductoAlCatalogo(
                                    nombre = nombre,
                                    precio = precioDouble
                                )

                            if (!agregado) {

                                error = "Ese producto ya existe"

                            } else {

                                /*
                                Limpiamos inputs
                                */
                                nombre = ""
                                precio = ""
                                error = ""
                            }
                        }
                    }
                }
            )

            EspacioGrande()

            /*
            LISTA DE PRODUCTOS
            */
            Text(
                text = "Productos actuales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            EspacioNormal()

            if (catalogo.isEmpty()) {

                Text("No hay productos cargados")

            } else {

                catalogo.forEach { producto ->

                    ProductoCatalogoItem(
                        producto = producto,
                        onEliminar = {
                            viewModel.eliminarProductoDelCatalogo(producto)
                        }
                    )

                    EspacioPequeño()
                }
            }
        }
    }
}

/*
ITEM DEL CATÁLOGO
*/
@Composable
fun ProductoCatalogoItem(
    producto: CatalogoProducto,
    onEliminar: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {

                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "$${producto.precio}"
                )
            }

            TextButton(
                onClick = onEliminar
            ) {
                Text("Eliminar")
            }
        }
    }
}