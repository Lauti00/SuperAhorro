package com.example.superahorro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.superahorro.ui.components.SimpleScreenContainer
import com.example.superahorro.ui.viewmodel.HomeViewModel

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
        title = "Estadísticas",
        onBack = onBack
    ) {

        /*
         Obtenemos el valor máximo para escalar las barras
         (evita división por 0 usando 1.0 como fallback)
        */
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
                        Text("Gasto total", style = MaterialTheme.typography.labelLarge)
                        Text(
                            "$${"%.2f".format(gastoTotal)}",
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
                        Text("Cantidad de compras", style = MaterialTheme.typography.labelLarge)
                        Text(
                            "$cantidadCompras",
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
                        Text("Producto más comprado", style = MaterialTheme.typography.labelLarge)
                        Text(
                            productoTop,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            //  GASTO POR SUPERMERCADO
            item {
                Text(
                    "Gasto por supermercado",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            /*
              LISTA CON GRÁFICO (barra proporcional)
            */
            items(gastoPorSuper.toList()) { (supermercado, total) ->

                /*
                 Calculamos el porcentaje respecto al mayor gasto
                */
                val porcentaje = (total / maxGasto).toFloat()

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        // Nombre + monto
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(supermercado)
                            Text("$${"%.2f".format(total)}")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        /*
                          BARRA VISUAL (tipo gráfico)
                        */
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