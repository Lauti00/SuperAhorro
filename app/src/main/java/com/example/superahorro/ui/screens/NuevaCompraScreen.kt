package com.example.superahorro.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superahorro.ui.components.*
import com.example.superahorro.ui.viewmodel.HomeViewModel
import com.example.superahorro.model.Compra

@Composable
fun NuevaCompraScreen(navController: NavController) {

    val viewModel: HomeViewModel = viewModel()

    //  Estados locales del formulario
    var supermercado by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }

    SimpleScreenContainer(
        title = "Nueva Compra",
        onBack = { navController.popBackStack() }
    ) {

        SuperAhorroTextField(
            value = supermercado,
            onValueChange = { supermercado = it },
            label = "Supermercado"
        )

        EspacioNormal()

        SuperAhorroTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = "Fecha (YYYY-MM-DD)"
        )

        EspacioNormal()

        SuperAhorroTextField(
            value = total,
            onValueChange = { total = it },
            label = "Total"
        )

        EspacioGrande()

        SuperAhorroButton(
            text = "Guardar Compra",
            onClick = {
                // Generamos un ID simple
                val nuevoId = (viewModel.compras.size + 1)
                
                // Creamos la compra
                val nuevaCompra = Compra(
                    id = nuevoId,
                    supermercado = supermercado,
                    fecha = fecha,
                    total = total.toDoubleOrNull() ?: 0.0
                )

                // La guardamos en el ViewModel
                viewModel.agregarCompra(nuevaCompra)

                // Volvemos atrás (Home)
                navController.popBackStack()
            }
        )
    }
}
