package com.example.superahorro.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileHeader(nombre: String, email: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
    ) {
        // Aquí podrías poner una imagen circular más adelante
        Surface(
            modifier = Modifier.size(100.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(nombre.take(1), style = MaterialTheme.typography.displayMedium)
            }
        }
        EspacioNormal()
        Text(text = nombre, style = MaterialTheme.typography.headlineMedium)
        Text(text = email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
    }
}

// En ProfileComponents.kt (donde ya tenés el Header)
@Composable
fun ProfileForm(
    nombre: String,
    onNombreChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SuperAhorroTextField(value = nombre, onValueChange = onNombreChange, label = "Nombre")
        EspacioNormal()
        SuperAhorroTextField(value = email, onValueChange = onEmailChange, label = "Email")
        EspacioGrande()
        SuperAhorroButton(text = "Guardar Cambios", onClick = onSave)
    }
}