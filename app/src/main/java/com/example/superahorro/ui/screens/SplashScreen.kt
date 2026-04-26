package com.example.superahorro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.superahorro.ui.components.BrandHeader
import kotlinx.coroutines.delay

@Composable
//fun SplashScreen(navController: NavController, onNavigateToLogin: () -> Unit) {
fun SplashScreen(onNavigateToLogin: () -> Unit) {

    //Ahora SplashScreen solo demora 2,5 segundos y devuelve el control de la
    // navegacion a AppNavigation
    LaunchedEffect(Unit) {
        delay(2500)
        onNavigateToLogin()
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        //Pantalla de Bienvenida (o Splash Screen), previa a mostrar la pantalla de Login
        BrandHeader()
    }
}