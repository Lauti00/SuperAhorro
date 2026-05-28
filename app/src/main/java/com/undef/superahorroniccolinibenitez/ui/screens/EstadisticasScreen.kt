package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.undef.superahorroniccolinibenitez.R
import com.undef.superahorroniccolinibenitez.ui.components.SimpleScreenContainer
import com.undef.superahorroniccolinibenitez.ui.components.SuperAhorroCard
import com.undef.superahorroniccolinibenitez.ui.components.SuperAhorroSectionTitle
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel

@Composable
fun EstadisticasScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val gastoTotal = viewModel.obtenerGastoTotal()
    val cantidadCompras = viewModel.cantidadCompras()
    val gastoPorSuper = viewModel.gastoPorSupermercado()
    val productoTop = viewModel.productoMasComprado()

    SimpleScreenContainer(
        title = stringResource(id = R.string.title_estadisticas),
        onBack = onBack
    ) {
        val maxGasto = gastoPorSuper.values.maxOrNull() ?: 1.0

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                SuperAhorroSectionTitle(
                    title = stringResource(id = R.string.title_estadisticas),
                    subtitle = stringResource(id = R.string.label_gasto_por_supermercado)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SuperAhorroCard(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.label_gasto_total),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = stringResource(
                                id = R.string.label_formato_monto_estadistica,
                                "%.2f".format(gastoTotal)
                            ),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    SuperAhorroCard(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.label_cantidad_compras),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = stringResource(
                                id = R.string.label_formato_cantidad_compras,
                                cantidadCompras
                            ),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                SuperAhorroCard {
                    Text(
                        text = stringResource(id = R.string.label_producto_mas_comprado),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = if (productoTop.isEmpty()) {
                            stringResource(id = R.string.state_sin_datos)
                        } else {
                            stringResource(id = R.string.label_formato_producto_top, productoTop)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                SuperAhorroSectionTitle(
                    title = stringResource(id = R.string.label_gasto_por_supermercado)
                )
            }

            items(gastoPorSuper.toList()) { (supermercado, total) ->

                val porcentaje = (total / maxGasto).toFloat()

                SuperAhorroCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = supermercado,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = stringResource(
                                id = R.string.label_formato_monto_estadistica,
                                "%.2f".format(total)
                            ),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { porcentaje },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = ProgressIndicatorDefaults.linearColor,
                        trackColor = ProgressIndicatorDefaults.linearTrackColor,
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                    )
                }
            }
        }
    }
}