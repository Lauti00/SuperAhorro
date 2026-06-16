package com.undef.superahorroniccolinibenitez.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.undef.superahorroniccolinibenitez.R
import com.undef.superahorroniccolinibenitez.model.Compra
import com.undef.superahorroniccolinibenitez.ui.components.SimpleScreenContainer
import com.undef.superahorroniccolinibenitez.ui.components.SuperAhorroButton
import com.undef.superahorroniccolinibenitez.ui.components.SuperAhorroCard
import com.undef.superahorroniccolinibenitez.ui.components.SuperAhorroSectionTitle
import com.undef.superahorroniccolinibenitez.ui.components.EspacioNormal

@Composable
fun DetalleCompraScreen(
    compra: Compra,
    onBack: () -> Unit,
    onEditarCompra: () -> Unit
) {
    SimpleScreenContainer(
        title = stringResource(id = R.string.title_detalle_compra),
        onBack = onBack
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                SuperAhorroButton(
                    text = stringResource(id = R.string.btn_editar_compra),
                    onClick = onEditarCompra,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                SuperAhorroCard {
                    Text(
                        text = compra.supermercado,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(
                            id = R.string.formato_fecha_hora,
                            compra.fecha,
                            compra.hora
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = stringResource(
                            id = R.string.formato_total_compra,
                            "%.2f".format(compra.total())
                        ),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            compra.imagenUri?.let { uriString ->

                item {
                    val uri = Uri.parse(uriString)

                    SuperAhorroSectionTitle(
                        title = stringResource(id = R.string.label_ticket_compra)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SuperAhorroCard {
                        AsyncImage(
                            model = uri,
                            contentDescription = stringResource(id = R.string.desc_imagen_ticket),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            item {
                SuperAhorroSectionTitle(
                    title = stringResource(id = R.string.label_productos)
                )
            }

            if (compra.productos.isEmpty()) {

                item {
                    SuperAhorroCard {
                        Text(text = stringResource(id = R.string.state_no_productos_compra))
                    }
                }

            } else {

                items(compra.productos) { item ->

                    SuperAhorroCard {
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
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(
                                id = R.string.formato_producto_valores,
                                item.cantidad,
                                "%.2f".format(item.producto.precio),
                                "%.2f".format(item.subtotal())
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}