package com.example.superahorro.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// --- 1. EL CONTENEDOR DEL MENÚ LATERAL ---
@Composable
fun MainDrawerContainer(
    drawerState: DrawerState,
    userEmail: String,
    onLogout: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    onNavigateToEstadisticas: () -> Unit,
    onNavigateToPerfil: () -> Unit, //  NUEVO
    content: @Composable () -> Unit
) {

    //  Necesario para abrir/cerrar el drawer
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Menú SuperAhorro",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = userEmail,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                HorizontalDivider()

                //  MIS COMPRAS
                NavigationDrawerItem(
                    label = { Text("Mis Compras") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        onNavigateToHistorial()
                    }
                )

                //  ESTADÍSTICAS
                NavigationDrawerItem(
                    label = { Text("Estadísticas") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        onNavigateToEstadisticas()
                    }
                )

                //  PERFIL (NUEVO)
                NavigationDrawerItem(
                    label = { Text("Mi Perfil") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        onNavigateToPerfil()
                    }
                )

                //  LOGOUT
                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        onLogout()
                    }
                )
            }
        },
        content = content
    )
}

// --- 2. LA TARJETA DE RESUMEN ---
@Composable
fun ResumenGastosCard(userName: String, montoTotal: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "¡Hola, $userName!", style = MaterialTheme.typography.headlineSmall)
            EspacioPequeño()
            Text(text = "Este mes has gastado:")
            Text(
                text = montoTotal,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}