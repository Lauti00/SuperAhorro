package com.example.superahorro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.superahorro.navigation.AppNavigation
import com.example.superahorro.ui.theme.SuperAhorroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Permite que la app ocupe toda la pantalla (incluyendo la zona de la barra de notificaciones)
        enableEdgeToEdge()

        setContent {
            SuperAhorroTheme {
                // El Scaffold principal de la aplicación
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    // Usamos un Box para respetar el padding (los márgenes) del sistema
                    // y que el contenido no quede tapado por la barra superior de Android
                    Box(modifier = Modifier.padding(innerPadding)) {

                        // Llamamos al archivo de navegación para que muestre el Login
                        AppNavigation()

                    }
                }
            }
        }
    }
}