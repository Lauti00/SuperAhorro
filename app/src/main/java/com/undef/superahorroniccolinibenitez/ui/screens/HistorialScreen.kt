package com.undef.superahorroniccolinibenitez.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.undef.superahorroniccolinibenitez.ui.components.ItemCompra
import com.undef.superahorroniccolinibenitez.ui.components.SimpleScreenContainer
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel
import com.undef.superahorroniccolinibenitez.R

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

        if (compras.isEmpty()) {

            // Caso sin compras
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.state_no_compras_registradas))
            }

        } else {

            // Mostramos la lista de compras del ViewModel
            LazyColumn {
                items(compras) { compra ->

                    ItemCompra(
                        compra = compra,

                        onItemClick = { clickedCompra ->
                            viewModel.seleccionarCompra(clickedCompra)
                            onCompraClick(clickedCompra.id)
                        },

                        onShare = {
                            // Armamos la cadena usando el recurso multilinea con sus respectivos marcadores de posición
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