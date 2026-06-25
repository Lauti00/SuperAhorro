package com.undef.superahorroniccolinibenitez.ui.screens

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.undef.superahorroniccolinibenitez.R
import com.undef.superahorroniccolinibenitez.model.Compra
import com.undef.superahorroniccolinibenitez.ui.components.ItemCompra
import com.undef.superahorroniccolinibenitez.ui.components.SimpleScreenContainer
import com.undef.superahorroniccolinibenitez.ui.components.SuperAhorroCard
import com.undef.superahorroniccolinibenitez.ui.components.SuperAhorroSectionTitle
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel
import java.time.LocalDate

enum class FiltroHistorial {
    TODAS,
    ESTE_MES,
    MES_ANTERIOR,
    ESTE_ANIO
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistorialScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    onCompraClick: (Int) -> Unit
) {
    val context = LocalContext.current

    SimpleScreenContainer(
        title = stringResource(id = R.string.title_historial_compras),
        onBack = onBack
    ) {
        /*
          Obtenemos las compras del ViewModel
        */
        val compras by viewModel.compras.collectAsState()

        var filtroSeleccionado by remember {
            mutableStateOf(FiltroHistorial.TODAS)
        }

        val comprasFiltradas =
            filtrarComprasPorPeriodo(
                compras = compras,
                filtro = filtroSeleccionado
            )

        if (compras.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                SuperAhorroCard {
                    Text(
                        text = stringResource(id = R.string.state_no_compras_registradas),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    SuperAhorroSectionTitle(
                        title = stringResource(id = R.string.title_historial_compras),
                        subtitle = stringResource(id = R.string.historial_subtitle)
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FiltroHistorialChip(
                                text = stringResource(id = R.string.filtro_todas),
                                selected = filtroSeleccionado == FiltroHistorial.TODAS,
                                onClick = {
                                    filtroSeleccionado = FiltroHistorial.TODAS
                                }
                            )
                        }

                        item {
                            FiltroHistorialChip(
                                text = stringResource(id = R.string.filtro_este_mes),
                                selected = filtroSeleccionado == FiltroHistorial.ESTE_MES,
                                onClick = {
                                    filtroSeleccionado = FiltroHistorial.ESTE_MES
                                }
                            )
                        }

                        item {
                            FiltroHistorialChip(
                                text = stringResource(id = R.string.filtro_mes_anterior),
                                selected = filtroSeleccionado == FiltroHistorial.MES_ANTERIOR,
                                onClick = {
                                    filtroSeleccionado = FiltroHistorial.MES_ANTERIOR
                                }
                            )
                        }

                        item {
                            FiltroHistorialChip(
                                text = stringResource(id = R.string.filtro_este_anio),
                                selected = filtroSeleccionado == FiltroHistorial.ESTE_ANIO,
                                onClick = {
                                    filtroSeleccionado = FiltroHistorial.ESTE_ANIO
                                }
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = stringResource(
                            id = R.string.historial_resultados,
                            comprasFiltradas.size
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (comprasFiltradas.isEmpty()) {

                    item {
                        SuperAhorroCard {
                            Text(
                                text = stringResource(id = R.string.historial_sin_resultados),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                } else {

                    items(comprasFiltradas) { compra ->

                        ItemCompra(
                            compra = compra,

                            onItemClick = { clickedCompra ->
                                viewModel.seleccionarCompra(clickedCompra)
                                onCompraClick(clickedCompra.id)
                            },

                            onShare = {
                                val texto = context.getString(
                                    R.string.formato_compartir_compra,
                                    compra.supermercado,
                                    compra.fecha,
                                    compra.hora,
                                    "%.2f".format(compra.total())
                                )

                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, texto)
                                }

                                context.startActivity(
                                    Intent.createChooser(
                                        intent,
                                        context.getString(R.string.chooser_compartir_compra)
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FiltroHistorialChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(text = text)
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun filtrarComprasPorPeriodo(
    compras: List<Compra>,
    filtro: FiltroHistorial
): List<Compra> {
    val hoy = LocalDate.now()

    return compras.filter { compra ->

        val fechaCompra =
            runCatching {
                LocalDate.parse(compra.fecha)
            }.getOrNull()

        if (fechaCompra == null) {

            filtro == FiltroHistorial.TODAS

        } else {

            when (filtro) {

                FiltroHistorial.TODAS -> true

                FiltroHistorial.ESTE_MES ->
                    fechaCompra.year == hoy.year &&
                            fechaCompra.month == hoy.month

                FiltroHistorial.MES_ANTERIOR -> {
                    val mesAnterior = hoy.minusMonths(1)

                    fechaCompra.year == mesAnterior.year &&
                            fechaCompra.month == mesAnterior.month
                }

                FiltroHistorial.ESTE_ANIO ->
                    fechaCompra.year == hoy.year
            }
        }
    }
}