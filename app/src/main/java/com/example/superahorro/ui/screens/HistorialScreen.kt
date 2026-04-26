package com.example.superahorro.ui.screens

import android.content.Intent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.superahorro.model.listaComprasMock
import com.example.superahorro.ui.components.ItemCompra
import com.example.superahorro.ui.components.SimpleScreenContainer

@Composable
fun HistorialScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    SimpleScreenContainer(
        title = "Historial de Compras",
        onBack = onBack
    ) {
        // Mostramos la lista de compras usando el componente visual
        LazyColumn {
            items(listaComprasMock) { compra ->
                ItemCompra(
                    compra = compra,
                    onShare = {
                        val texto = """
                            Compra en ${compra.supermercado}
                            Fecha: ${compra.fecha}
                            Total: $${compra.total}
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
