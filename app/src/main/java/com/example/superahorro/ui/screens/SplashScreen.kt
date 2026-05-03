package com.example.superahorro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.superahorro.data.datastore.UserPreferences
import com.example.superahorro.ui.components.BrandHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit,
                 onNavigateToHome: () -> Unit)
{

    val context = LocalContext.current
    val userPreferences = UserPreferences(context)

    //Ahora SplashScreen solo demora 2,5 segundos y devuelve el control de la
    // navegacion a AppNavigation
    LaunchedEffect(Unit) {
        delay(1500)

        val email = userPreferences.userEmail.first()
        if (email.isNotEmpty()) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        //Pantalla de Bienvenida (o Splash Screen), previa a mostrar la pantalla de Login
        BrandHeader()
    }
}