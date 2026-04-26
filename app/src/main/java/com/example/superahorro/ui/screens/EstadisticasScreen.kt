package com.example.superahorro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.superahorro.ui.components.SimpleScreenContainer

@Composable
fun EstadisticasScreen(navController: NavController) {
    SimpleScreenContainer(
        title = "Estadísticas",
        onBack = { navController.popBackStack() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Próximamente: Gráficos y análisis de tus compras.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
