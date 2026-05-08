package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.runtime.*
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
    var nombre by remember { mutableStateOf(viewModel.userName) }
    val email = viewModel.userEmail

    SimpleScreenContainer(
        title = "Mi Perfil",
        onBack = onBack
    ) {

        // Header con datos reales
        ProfileHeader(
            nombre = nombre,
            email = email
        )

        // Formulario editable
        ProfileForm(
            nombre = nombre,
            onNombreChange = { nombre = it },
            email = email,
            onEmailChange = {}, //  no dejamos editar email por ahora

            onSave = {

                /*
                  Guardamos el nombre en DataStore
                */
                viewModel.guardarNombre(nombre)

                onSaveProfile()
            }
        )
    }
}