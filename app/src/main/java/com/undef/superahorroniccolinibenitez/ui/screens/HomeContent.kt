package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.undef.superahorroniccolinibenitez.model.Compra
import com.undef.superahorroniccolinibenitez.model.OfertaSupermercado
import com.undef.superahorroniccolinibenitez.ui.components.ItemCompra

/*
Orquestador de la pantalla Home.

Ahora delega cada bloque visual a su propia sección @Composable,
separada en su propio archivo (HomeDashboardSection, HomeOfertasSection,
HomeUltimasComprasSection). HomeContent solo arma el LazyColumn y decide
el orden en el que se muestran las secciones.
*/
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

        // Título, tarjeta de presupuesto, resumen de gastos e insights
        item {
            HomeDashboardSection(compras = compras)
        }

        // Chips de features + precios de referencia desde la API
        item {
            HomeOfertasSection(
                ofertas = ofertas,
                ofertasLoading = ofertasLoading,
                ofertasError = ofertasError,
                onRetryOfertas = onRetryOfertas,
                onOfertaClick = onOfertaClick
            )
        }

        // Encabezado de "Últimas compras"
        item {
            HomeUltimasComprasHeader()
        }

        // Lista de compras recientes (o estado vacío)
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