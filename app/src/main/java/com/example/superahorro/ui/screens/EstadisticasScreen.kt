package com.example.superahorro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import com.example.superahorro.ui.components.SimpleScreenContainer
import com.example.superahorro.ui.viewmodel.HomeViewModel

@Composable
fun EstadisticasScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {

    /*     Obtenemos todas las compras
    */
    val compras = viewModel.compras

    /*
      TOTAL GENERAL
    */
    val totalGastado = compras.sumOf { it.total() }

    /*
      AGRUPAMOS POR SUPERMERCADO
    */
    val gastoPorSuper = compras
        .groupBy { it.supermercado }
        .mapValues { entry ->
            entry.value.sumOf { it.total() }
        }

    SimpleScreenContainer(
        title = "Estadísticas",
        onBack = onBack
    ) {

        if (compras.isEmpty()) {

            // Caso sin datos
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay datos para mostrar")
            }

        } else {

            /*
             TOTAL GENERAL
            */
            Text(
                text = "Total gastado: $${"%.2f".format(totalGastado)}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Gasto por supermercado",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            /*
             “GRÁFICO” SIMPLE (BARRAS)
            */
            gastoPorSuper.forEach { (supermercado, total) ->

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("$supermercado: $${"%.2f".format(total)}")

                    Spacer(modifier = Modifier.height(4.dp))

                    /*
                     Barra visual proporcional
                    */
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .background(Color.LightGray, RoundedCornerShape(6.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = (total / totalGastado).toFloat())
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
