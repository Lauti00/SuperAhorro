package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.undef.superahorroniccolinibenitez.R
import com.undef.superahorroniccolinibenitez.model.OfertaSupermercado
import com.undef.superahorroniccolinibenitez.ui.components.SuperAhorroCard
import com.undef.superahorroniccolinibenitez.ui.components.SuperAhorroSectionTitle

/*
Sección de "features implementadas" (chips) + precios de referencia
consumidos desde la API (Retrofit). Maneja los 4 estados posibles:
cargando / error / vacío / con datos.

Extraída de HomeContent.kt.
*/
@Composable
fun HomeOfertasSection(
    ofertas: List<OfertaSupermercado>,
    ofertasLoading: Boolean,
    ofertasError: String?,
    onRetryOfertas: () -> Unit,
    onOfertaClick: (OfertaSupermercado) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

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

        /*
        SECCIÓN DE OFERTAS DESDE API

        Esta sección consume datos remotos usando Retrofit.
        Puede mostrar carga, error o las ofertas recibidas.
        */
        SuperAhorroSectionTitle(
            title = stringResource(id = R.string.home_ofertas_title),
            subtitle = stringResource(id = R.string.home_ofertas_subtitle)
        )

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
}

@Composable
private fun OfertaCard(
    oferta: OfertaSupermercado,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.width(250.dp).height(160.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Categoría traducida al español como etiqueta superior
            Text(
                text = oferta.descripcion,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            // Columna intermedia que absorbe el espacio restante
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = oferta.producto,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    maxLines = 2
                )
            }

            // Precio real de la API, alineado abajo
            Text(
                text = stringResource(
                    id = R.string.home_oferta_precio,
                    "%.2f".format(oferta.precio)
                ),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}