package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.undef.superahorroniccolinibenitez.ui.components.SimpleScreenContainer
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel
import com.undef.superahorroniccolinibenitez.R

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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            //  GASTO TOTAL
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(id = R.string.label_gasto_total), style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = stringResource(id = R.string.label_formato_monto_estadistica, "%.2f".format(gastoTotal)),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            //  CANTIDAD DE COMPRAS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(id = R.string.label_cantidad_compras), style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = stringResource(id = R.string.label_formato_cantidad_compras, cantidadCompras),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }

            // PRODUCTO TOP
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(id = R.string.label_producto_mas_comprado), style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = if (productoTop.isEmpty()) {
                                stringResource(id = R.string.state_sin_datos)
                            } else {
                                stringResource(id = R.string.label_formato_producto_top, productoTop)
                            },
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            //  GASTO POR SUPERMERCADO
            item {
                Text(
                    text = stringResource(id = R.string.label_gasto_por_supermercado),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // LISTA CON GRÁFICO
            items(gastoPorSuper.toList()) { (supermercado, total) ->

                val porcentaje = (total / maxGasto).toFloat()

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(supermercado)
                            Text(text = stringResource(id = R.string.label_formato_monto_estadistica, "%.2f".format(total)))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = porcentaje,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                        )
                    }
                }
            }
        }
    }
}