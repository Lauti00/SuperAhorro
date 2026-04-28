package com.example.superahorro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.superahorro.model.Compra
import com.example.superahorro.ui.components.SimpleScreenContainer

@Composable
fun DetalleCompraScreen(
    compra: Compra,
    onBack: () -> Unit
) {
    SimpleScreenContainer(
        title = "Detalle de Compra",
        onBack = onBack
    ) {
        // Mostramos los datos principales
        Text(text = "Supermercado: ${compra.supermercado}")
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Fecha: ${compra.fecha}")
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Total: $${"%.2f".format(compra.total())}")
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Productos", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        /*
         Ahora usamos los productos REALES de la compra
        (ya no usamos mock)
        */
        if (compra.productos.isEmpty()) {

            // Caso sin productos
            Text("No hay productos en esta compra")

        } else {

            LazyColumn {
                items(compra.productos) { producto ->

                    /*
                     Mostramos cada producto con:
                     nombre, cantidad y precio
                    */
                    Text(
                        text = "${producto.producto.nombre} - ${producto.cantidad} x $${producto.producto.precio}",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
