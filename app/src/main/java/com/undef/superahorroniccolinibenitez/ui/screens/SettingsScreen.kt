package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.undef.superahorroniccolinibenitez.ui.components.*
import com.undef.superahorroniccolinibenitez.R

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToNuevoProducto: () -> Unit
) {

    SimpleScreenContainer(
        title = stringResource(id = R.string.title_configuracion),
        onBack = onBack
    ) {

        Text(
            text = stringResource(id = R.string.label_gestion),
            style = MaterialTheme.typography.titleMedium
        )

        EspacioNormal()

        /*
        BOTÓN GESTIONAR PRODUCTOS
        */
        SuperAhorroButton(
            text = stringResource(id = R.string.btn_gestionar_productos),
            onClick = onNavigateToNuevoProducto
        )
    }
}