package com.example.superahorro.ui.screens

import android.content.Intent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.superahorro.model.Compra
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
        // Mostramos la lista de compras del ViewModel
        LazyColumn {
            items(viewModel.compras) { compra ->
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
