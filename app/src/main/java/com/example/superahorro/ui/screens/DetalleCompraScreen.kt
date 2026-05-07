package com.example.superahorro.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
        Text(text = "Supermercado: ${compra.supermercado}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))

        // Agregamos la hora que sumamos al modelo
        Text(text = "Fecha: ${compra.fecha} - ${compra.hora}")
        Spacer(modifier = Modifier.height(4.dp))

        Text(text = "Total: $${"%.2f".format(compra.total())}", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        /*
          mostramos imagen del ticket si existe
        */
        compra.imagenUri?.let { uriString ->

            val uri = Uri.parse(uriString)

            Text(
                text = "Ticket de compra",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = uri,
                contentDescription = "Imagen del ticket",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(text = "Productos", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        /*
         Ahora usamos los productos REALES de la compra
         y mostramos todo el detalle (código, nombre, descripción, precio y subtotal)
        */
        if (compra.productos.isEmpty()) {

            // Caso sin productos
            Text("No hay productos en esta compra")

        } else {

            LazyColumn {
                items(compra.productos) { item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Nombre y código
                            Text(
                                text = "${item.producto.nombre} (Cód: ${item.producto.codigo})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            // Descripción
                            Text(
                                text = item.producto.descripcion,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // Cantidad, precio unitario y subtotal
                            Text(
                                text = "${item.cantidad} x $${"%.2f".format(item.producto.precio)} = $${"%.2f".format(item.subtotal())}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}