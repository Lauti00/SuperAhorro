package com.example.superahorro.ui.screens

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.superahorro.navigation.AppScreens
import com.example.superahorro.ui.components.ProfileForm
import com.example.superahorro.ui.components.ProfileHeader
import com.example.superahorro.ui.components.SimpleScreenContainer
import com.example.superahorro.ui.viewmodel.HomeViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    onBack: () -> Unit,
    onSaveProfile: () -> Unit
) {

    /*
      IMPORTANTE:
     Usamos el MISMO ViewModel del Home
    */
    val viewModel: HomeViewModel = viewModel(
        navController.getBackStackEntry(AppScreens.Home.route)
    )

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