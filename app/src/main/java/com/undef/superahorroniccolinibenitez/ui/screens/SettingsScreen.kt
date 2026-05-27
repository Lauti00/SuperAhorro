package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.undef.superahorroniccolinibenitez.ui.components.*
import com.undef.superahorroniccolinibenitez.R
import com.undef.superahorroniccolinibenitez.data.datastore.UserPreferences
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToNuevoProducto: () -> Unit
) {

    val context = LocalContext.current
    val userPreferences = remember {
        UserPreferences(context)
    }

    val coroutineScope = rememberCoroutineScope()

    /*
     Estado del switch
    */
    val darkThemeEnabled by userPreferences.darkTheme.collectAsState(initial = false)

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

        EspacioNormal()

        /*
         CONFIGURACIÓN DEL TEMA
        */
        Card {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Modo oscuro",
                    style = MaterialTheme.typography.bodyLarge
                )

                Switch(
                    checked = darkThemeEnabled,

                    onCheckedChange = { enabled ->

                        coroutineScope.launch {

                            userPreferences.saveTheme(enabled)
                        }
                    }
                )
            }
        }
    }
}