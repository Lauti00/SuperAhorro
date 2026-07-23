package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.undef.superahorroniccolinibenitez.R
import com.undef.superahorroniccolinibenitez.model.Compra

/*
Sección superior del Home: título, tarjeta de presupuesto,
resumen de gastos (cantidad y promedio) e insights
(supermercado más usado, producto más comprado).

Extraída de HomeContent.kt para que cada archivo tenga
una responsabilidad clara y no supere las ~200 líneas.
*/
@Composable
fun HomeDashboardSection(compras: List<Compra>) {

    val totalGastado =
        compras.sumOf { it.total() }

    val cantidadCompras =
        compras.size

    val promedioPorCompra =
        if (cantidadCompras > 0) {
            totalGastado / cantidadCompras
        } else {
            0.0
        }

    val supermercadoMasUsado =
        compras
            .groupBy { it.supermercado }
            .maxByOrNull { it.value.size }
            ?.key ?: stringResource(id = R.string.state_sin_datos)

    val productoMasComprado =
        compras
            .flatMap { it.productos }
            .groupBy { it.producto.nombre }
            .maxByOrNull { entry ->
                entry.value.sumOf { producto ->
                    producto.cantidad
                }
            }
            ?.key ?: stringResource(id = R.string.state_sin_datos)

    /*
    Presupuesto visual de referencia.
    Más adelante esto puede pasar a Settings/DataStore para que el usuario lo configure.
    */
    val presupuestoMensual =
        100000.0

    val porcentajePresupuesto =
        (totalGastado / presupuestoMensual)
            .toFloat()
            .coerceIn(0f, 1f)

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

        Text(
            text = stringResource(id = R.string.home_dashboard_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(id = R.string.home_dashboard_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.home_total_mes),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = stringResource(
                        id = R.string.home_money_format,
                        "%.2f".format(totalGastado)
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(
                        id = R.string.home_presupuesto_usado,
                        "%.2f".format(totalGastado),
                        "%.2f".format(presupuestoMensual)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { porcentajePresupuesto },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surface
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ResumenCard(
                modifier = Modifier.weight(1f),
                titulo = stringResource(id = R.string.home_cantidad_compras),
                valor = cantidadCompras.toString(),
                icono = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null
                    )
                }
            )

            ResumenCard(
                modifier = Modifier.weight(1f),
                titulo = stringResource(id = R.string.home_promedio_compra),
                valor = stringResource(
                    id = R.string.home_money_format,
                    "%.2f".format(promedioPorCompra)
                ),
                icono = {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = null
                    )
                }
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.home_insights_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                InsightRow(
                    titulo = stringResource(id = R.string.home_supermercado_frecuente),
                    valor = supermercadoMasUsado,
                    icono = {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null
                        )
                    }
                )

                InsightRow(
                    titulo = stringResource(id = R.string.home_producto_top),
                    valor = productoMasComprado,
                    icono = {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ResumenCard(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: String,
    icono: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Box(
                    modifier = Modifier.padding(8.dp)
                ) {
                    icono()
                }
            }

            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InsightRow(
    titulo: String,
    valor: String,
    icono: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.small
        ) {
            Box(
                modifier = Modifier.padding(8.dp)
            ) {
                icono()
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}