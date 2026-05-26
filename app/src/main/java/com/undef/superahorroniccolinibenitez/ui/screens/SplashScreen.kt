package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.undef.superahorroniccolinibenitez.data.datastore.UserPreferences
import com.undef.superahorroniccolinibenitez.ui.components.BrandHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {

    val context = LocalContext.current

    val userPreferences =
        UserPreferences(context)

    /*
    Ahora SplashScreen espera un momento y decide
    automáticamente si el usuario ya inició sesión
    */
    LaunchedEffect(Unit) {

        delay(1500)

        /*
        Consultamos el estado de sesión guardado
        */
        val isLoggedIn =
            userPreferences.isLoggedIn.first()

        /*
        Si hay sesión iniciada → Home
        Si no → Login
        */
        if (isLoggedIn) {

            onNavigateToHome()

        } else {

            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),

        contentAlignment = Alignment.Center
    ) {

        /*
        Pantalla de Bienvenida (Splash Screen)
        */
        BrandHeader()
    }
}