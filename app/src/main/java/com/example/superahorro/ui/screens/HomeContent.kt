package com.example.superahorro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.superahorro.model.Compra
import com.example.superahorro.ui.components.ItemCompra

@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    compras: List<Compra>,
    onItemClick: (Compra) -> Unit,
    /*
    * Función que se pasa como parametro, cuando alguien quiera compartir ejecuta esto.
    * */
    onShare: (Compra) -> Unit // <-- Nueva función para compartir
) {

    if (compras.isEmpty()) {

        // Caso vacío
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay compras registradas")
        }

    } else {

        // Caso con datos
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(compras) { compra ->
                ItemCompra(
                    compra = compra,
                    onItemClick = onItemClick,
                    onShare = onShare
                )
            }
        }
    }
}
