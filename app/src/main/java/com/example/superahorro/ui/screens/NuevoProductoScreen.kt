package com.example.superahorro.ui.screens

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    ESTADO ERROR SIMPLE
    */
    var error by remember { mutableStateOf("") }

    SimpleScreenContainer(
        title = "Nuevo Producto",
        onBack = onBack
    ) {

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
        ERROR (si hay)
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
                    nombre.isBlank() -> {
                        error = "El nombre no puede estar vacío"
                    }

                    precioDouble == null -> {
                        error = "Precio inválido"
                    }

                    precioDouble <= 0 -> {
                        error = "El precio debe ser mayor a 0"
                    }

                    else -> {
                        /*
                        Guardamos en el catálogo
                        */
                        viewModel.agregarProductoAlCatalogo(
                            nombre = nombre,
                            precio = precioDouble
                        )

                        /*
                        Volvemos atrás
                        */
                        onProductoCreado()
                    }
                }
            }
        )
    }
}