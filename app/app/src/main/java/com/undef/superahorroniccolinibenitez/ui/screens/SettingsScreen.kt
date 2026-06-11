package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.undef.superahorroniccolinibenitez.R
import com.undef.superahorroniccolinibenitez.data.datastore.UserPreferences
import com.undef.superahorroniccolinibenitez.ui.components.*
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    homeViewModel: HomeViewModel,
    onBack: () -> Unit,
    onNavigateToNuevoProducto: () -> Unit,
    onNavigateToNuevoSupermercado: () -> Unit
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            SuperAhorroSectionTitle(
                title = stringResource(id = R.string.title_configuracion),
                subtitle = stringResource(id = R.string.settings_subtitle)
            )

            EspacioNormal()

            SuperAhorroCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = stringResource(id = R.string.label_gestion),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                EspacioNormal()

                /*
                BOTÓN GESTIONAR PRODUCTOS
                */
                SuperAhorroButton(
                    text = stringResource(id = R.string.btn_gestionar_productos),
                    onClick = onNavigateToNuevoProducto,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            EspacioNormal()

            /*
            GESTIÓN DE SUPERMERCADOS
            */
            SuperAhorroCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = stringResource(id = R.string.label_gestion_supermercados),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                EspacioNormal()

                /*
                BOTÓN GESTIONAR SUPERMERCADOS
                */
                SuperAhorroButton(
                    text = stringResource(id = R.string.btn_gestionar_supermercados),
                    onClick = onNavigateToNuevoSupermercado,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            EspacioNormal()

            /*
             CONFIGURACIÓN DEL TEMA
            */
            SuperAhorroCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DarkMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = stringResource(id = R.string.label_modo_oscuro),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
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
}