package com.example.superahorro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.superahorro.model.Compra
import com.example.superahorro.ui.components.SimpleScreenContainer

@Composable
fun DetalleCompraScreen(
    navController: NavController,
    compra: Compra
) {
    SimpleScreenContainer(
        title = "Detalle de Compra",
        onBack = { navController.popBackStack() }
    ) {
        // Mostramos los datos principales
        Text(text = "Supermercado: ${compra.supermercado}")
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Fecha: ${compra.fecha}")
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Total: $${compra.total}")
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Productos", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Por ahora usamos productos MOCK
        val productosMock = listOf(
            "Leche - $100",
            "Pan - $200",
            "Arroz - $300"
        )

        LazyColumn {
            items(productosMock) { producto ->
                Text(text = producto, modifier = Modifier.padding(8.dp))
            }
        }
    }
}
