package com.example.superahorro.ui.screens

import androidx.compose.runtime.*
import com.example.superahorro.ui.components.ProfileForm
import com.example.superahorro.ui.components.ProfileHeader
import com.example.superahorro.ui.components.SimpleScreenContainer

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onSaveProfile: () -> Unit
) {
    // 1. LÓGICA DE ESTADO
    var nombre by remember { mutableStateOf("Martín") }
    var email by remember { mutableStateOf("martin@correo.com") }

    // 2. ENSAMBLADO USANDO TUS COMPONENTES
    SimpleScreenContainer(
        title = "Mi Perfil",
        onBack = onBack
    ) {
        // Llamamos al Header que tenés en ProfileComponents
        ProfileHeader(nombre = nombre, email = email)

        // Llamamos al Formulario que tenés en ProfileComponents
        ProfileForm(
            nombre = nombre,
            onNombreChange = { nombre = it },
            email = email,
            onEmailChange = { email = it },
            onSave = {
                // Aquí irá la conexión a la base de datos después
                onSaveProfile()
            }
        )
    }
}