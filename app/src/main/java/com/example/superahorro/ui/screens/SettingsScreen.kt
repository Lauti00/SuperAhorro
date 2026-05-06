package com.example.superahorro.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.superahorro.ui.components.*

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToNuevoProducto: () -> Unit
) {

    SimpleScreenContainer(
        title = "Configuración",
        onBack = onBack
    ) {

        Text(
            text = "Gestión",
            style = MaterialTheme.typography.titleMedium
        )

        EspacioNormal()

        /*
        BOTÓN GESTIONAR PRODUCTOS
        */
        SuperAhorroButton(
            text = "Gestionar productos",
            onClick = onNavigateToNuevoProducto
        )
    }
}