package com.undef.superahorroniccolinibenitez.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.undef.superahorroniccolinibenitez.R
import kotlinx.coroutines.launch

// --- 1. EL CONTENEDOR DEL MENÚ LATERAL ---
@Composable
fun MainDrawerContainer(
    drawerState: DrawerState,
    userEmail: String,
    onLogout: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    onNavigateToEstadisticas: () -> Unit,
    onNavigateToPerfil: () -> Unit,

    /*

    Navegación a configuración
    */
    onNavigateToSettings: () -> Unit,

    content: @Composable () -> Unit
) {

    // Necesario para abrir/cerrar el drawer
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,

        drawerContent = {

            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = stringResource(id = R.string.menu_title),
                    modifier = Modifier.padding(horizontal = 18.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                /*
                EMAIL DEL USUARIO
                */
                Text(
                    text = userEmail,
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 8.dp
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(id = R.string.menu_historial)) },
                    selected = false,

                    onClick = {

                        scope.launch {
                            drawerState.close()
                        }

                        onNavigateToHistorial()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(id = R.string.menu_estadisticas)) },
                    selected = false,

                    onClick = {

                        scope.launch {
                            drawerState.close()
                        }

                        onNavigateToEstadisticas()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(id = R.string.menu_perfil)) },
                    selected = false,

                    onClick = {

                        scope.launch {
                            drawerState.close()
                        }

                        onNavigateToPerfil()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(id = R.string.menu_configuracion)) },
                    selected = false,

                    onClick = {

                        scope.launch {
                            drawerState.close()
                        }

                        onNavigateToSettings()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(id = R.string.menu_logout)) },
                    selected = false,

                    onClick = {

                        scope.launch {
                            drawerState.close()
                        }

                        onLogout()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                )
            }
        },

        content = content
    )
}