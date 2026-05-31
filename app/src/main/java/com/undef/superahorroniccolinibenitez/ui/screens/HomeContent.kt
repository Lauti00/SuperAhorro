package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.undef.superahorroniccolinibenitez.R
import com.undef.superahorroniccolinibenitez.model.Compra
import com.undef.superahorroniccolinibenitez.model.OfertaSupermercado
import com.undef.superahorroniccolinibenitez.ui.components.ItemCompra
import com.undef.superahorroniccolinibenitez.ui.components.SuperAhorroCard
import com.undef.superahorroniccolinibenitez.ui.components.SuperAhorroSectionTitle

@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    compras: List<Compra>,
    ofertas: List<OfertaSupermercado>,
    ofertasLoading: Boolean,
    ofertasError: String?,
    onRetryOfertas: () -> Unit,
    onOfertaClick: (OfertaSupermercado) -> Unit,
    onItemClick: (Compra) -> Unit,
    /*
    * Función que se pasa como parametro, cuando alguien quiera compartir ejecuta esto.
    * */
    onShare: (Compra) -> Unit
) {
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

    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            top = paddingValues.calculateTopPadding() + 16.dp,
            end = 16.dp,
            bottom = paddingValues.calculateBottomPadding() + 88.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {

        item {
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
        }

        item {
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
        }

        item {
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
                            imageVector = Icons.Default.ReceiptLong,
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
        }

        item {
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

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = {
                        Text(
                            text = stringResource(id = R.string.home_chip_room)
                        )
                    }
                )

                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = {
                        Text(
                            text = stringResource(id = R.string.home_chip_datastore)
                        )
                    }
                )
            }
        }

        /*
        SECCIÓN DE OFERTAS DESDE API

        Esta sección consume datos remotos usando Retrofit.
        Puede mostrar carga, error o las ofertas recibidas.
        */
        item {
            SuperAhorroSectionTitle(
                title = stringResource(id = R.string.home_ofertas_title),
                subtitle = stringResource(id = R.string.home_ofertas_subtitle)
            )
        }

        item {
            when {
                ofertasLoading -> {
                    SuperAhorroCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator()

                            Text(
                                text = stringResource(id = R.string.home_ofertas_loading),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                ofertasError != null -> {
                    SuperAhorroCard {
                        Text(
                            text = ofertasError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )

                        TextButton(onClick = onRetryOfertas) {
                            Text(
                                text = stringResource(id = R.string.btn_reintentar)
                            )
                        }
                    }
                }

                ofertas.isEmpty() -> {
                    SuperAhorroCard {
                        Text(
                            text = stringResource(id = R.string.home_ofertas_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(ofertas) { oferta ->
                            OfertaCard(
                                oferta = oferta,
                                onClick = {
                                    onOfertaClick(oferta)
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.home_ultimas_compras),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(id = R.string.home_ultimas_compras_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (compras.isEmpty()) {

            item {
                EmptyHomeState()
            }

        } else {

            items(compras.take(5)) { compra ->

                ItemCompra(
                    compra = compra,
                    onItemClick = onItemClick,
                    onShare = onShare
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

@Composable
private fun OfertaCard(
    oferta: OfertaSupermercado,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.width(250.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = oferta.supermercado,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Text(
                text = oferta.producto,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Text(
                text = oferta.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Text(
                text = stringResource(
                    id = R.string.home_oferta_precio_descuento,
                    "%.2f".format(oferta.precio),
                    oferta.descuento
                ),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(id = R.string.home_oferta_ver),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyHomeState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.state_no_compras_registradas),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(id = R.string.home_empty_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}