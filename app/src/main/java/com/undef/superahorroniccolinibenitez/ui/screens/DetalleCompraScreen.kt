package com.undef.superahorroniccolinibenitez.ui.screens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.undef.superahorroniccolinibenitez.model.Compra
import com.undef.superahorroniccolinibenitez.ui.components.SimpleScreenContainer
import com.undef.superahorroniccolinibenitez.R

@Composable
fun DetalleCompraScreen(
    compra: Compra,
    onBack: () -> Unit
) {
    SimpleScreenContainer(
        title = stringResource(id = R.string.title_detalle_compra),
        onBack = onBack
    ) {

        Text(
            text = stringResource(id = R.string.formato_supermercado, compra.supermercado),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(text = stringResource(id = R.string.formato_fecha_hora, compra.fecha, compra.hora))
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.formato_total_compra, "%.2f".format(compra.total())),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        compra.imagenUri?.let { uriString ->

            val uri = Uri.parse(uriString)

            Text(
                text = stringResource(id = R.string.label_ticket_compra),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = uri,
                contentDescription = stringResource(id = R.string.desc_imagen_ticket),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(text = stringResource(id = R.string.label_productos), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (compra.productos.isEmpty()) {

            Text(text = stringResource(id = R.string.state_no_productos_compra))

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

                            Text(
                                text = stringResource(
                                    id = R.string.formato_producto_titulo,
                                    item.producto.nombre,
                                    item.producto.codigo
                                ),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.producto.descripcion,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = stringResource(
                                    id = R.string.formato_producto_valores,
                                    item.cantidad, // Usamos la cantidad directamente (o mapeada a Int)
                                    "%.2f".format(item.producto.precio),
                                    "%.2f".format(item.subtotal())
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}