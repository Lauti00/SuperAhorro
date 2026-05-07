package com.example.superahorro.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text
import com.example.superahorro.ui.components.ItemCompra
import com.example.superahorro.ui.components.SimpleScreenContainer
import com.example.superahorro.ui.viewmodel.HomeViewModel

@Composable
fun HistorialScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    onCompraClick: (Int) -> Unit
) {
    val context = LocalContext.current

    SimpleScreenContainer(
        title = "Historial de Compras",
        onBack = onBack
    ) {

        /*
         Obtenemos las compras del ViewModel
        */
        val compras = viewModel.compras

        if (compras.isEmpty()) {

            // Caso sin compras 
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay compras registradas")
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
                            val texto = """
                                Compra en ${compra.supermercado}
                                Fecha: ${compra.fecha}
                                Total: $${"%.2f".format(compra.total())}
                            """.trimIndent()

                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, texto)
                            }

                            context.startActivity(
                                Intent.createChooser(intent, "Compartir compra")
                            )
                        }
                    )
                }
            }
        }
    }
}