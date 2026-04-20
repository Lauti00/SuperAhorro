package com.example.superahorro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.superahorro.model.Compra

// --- 1. EL CONTENEDOR DEL MENÚ LATERAL ---
@Composable
fun MainDrawerContainer(
    drawerState: DrawerState,
    onLogout: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Menú SuperAhorro", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Mis Compras") },
                    selected = false,
                    onClick = onNavigateToHistorial // <--- USA LA FUNCIÓN PARA IR AL HISTORIAL
                )
                NavigationDrawerItem(label = { Text("Estadísticas") }, selected = false, onClick = {})
                NavigationDrawerItem(label = { Text("Cerrar Sesión") }, selected = false, onClick = onLogout)
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

// --- 3. EL ITEM DE LA LISTA ---
@Composable
fun ItemCompra(compra: Compra) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(compra.supermercado.take(1), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = compra.supermercado, style = MaterialTheme.typography.titleMedium)
                Text(text = compra.fecha, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "$${compra.total}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
