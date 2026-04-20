package com.example.superahorro.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.superahorro.model.listaComprasMock
import com.example.superahorro.ui.components.ItemCompra
import com.example.superahorro.ui.components.SimpleScreenContainer

@Composable
fun HistorialScreen(navController: NavController) {

    SimpleScreenContainer(
        title = "Historial de Compras",
        onBack = { navController.popBackStack() }
    ) {
        // Mostramos la lista de compras usando el componente visual
        LazyColumn {
            items(listaComprasMock) { compra ->
                ItemCompra(compra = compra)
            }
        }
    }
}
