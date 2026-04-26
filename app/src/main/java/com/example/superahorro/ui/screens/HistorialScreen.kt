package com.example.superahorro.ui.screens

import android.content.Intent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.superahorro.model.listaComprasMock
import com.example.superahorro.ui.components.ItemCompra
import com.example.superahorro.ui.components.SimpleScreenContainer

@Composable
fun HistorialScreen(navController: NavController) {
    val context = LocalContext.current

    SimpleScreenContainer(
        title = "Historial de Compras",
        onBack = { navController.popBackStack() }
    ) {
        // Mostramos la lista de compras usando el componente visual
        LazyColumn {
            items(listaComprasMock) { compra ->
                ItemCompra(
                    compra = compra,
                    onItemClick = { clickedCompra ->
                        navController.navigate("detalle_compra/${clickedCompra.id}")
                    },
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
