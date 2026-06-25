package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.undef.superahorroniccolinibenitez.ui.components.ProfileForm
import com.undef.superahorroniccolinibenitez.ui.components.ProfileHeader
import com.undef.superahorroniccolinibenitez.ui.components.SimpleScreenContainer
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel

@Composable
fun ProfileScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    onSaveProfile: () -> Unit
) {
    /*
      IMPORTANTE:
     Usamos el MISMO ViewModel del Home
    */

    /*
      Estados inicializados con datos reales
    */
    val userNameState by viewModel.userName.collectAsState()
    var nombre by remember(userNameState) { mutableStateOf(userNameState) }
    
    val email by viewModel.userEmail.collectAsState()

    SimpleScreenContainer(
        title = "Mi Perfil",
        onBack = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            ProfileHeader(
                nombre = nombre,
                email = email
            )

            ProfileForm(
                nombre = nombre,
                onNombreChange = { nombre = it },
                email = email,
                onEmailChange = {},
                onSave = {
                    viewModel.guardarNombre(nombre)
                    onSaveProfile()
                }
            )
        }
    }
}