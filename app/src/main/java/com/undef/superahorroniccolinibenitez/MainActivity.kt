package com.undef.superahorroniccolinibenitez

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.undef.superahorroniccolinibenitez.data.datastore.UserPreferences
import com.undef.superahorroniccolinibenitez.navigation.AppNavigation
import com.undef.superahorroniccolinibenitez.ui.theme.SuperAhorroTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        /*
        UserPreferences se usa acá únicamente para leer el tema
        guardado y aplicarlo antes de dibujar la navegación.

        NOTA: Room y el resto de la lógica de negocio viven en
        HomeViewModel, instanciado como ViewModel compartido
        dentro de AppNavigation. No hace falta crear nada más aquí.
        */
        val userPreferences =
            UserPreferences(applicationContext)

        /*
        Permite ocupar toda la pantalla
        */
        enableEdgeToEdge()

        setContent {

            /*
            Leemos el tema guardado en DataStore
            */
            val darkThemeEnabled by
            userPreferences.darkTheme.collectAsState(initial = false)

            /*
            Aplicamos el tema dinámicamente
            */
            SuperAhorroTheme(
                darkTheme = darkThemeEnabled
            ) {

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    Box(
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        /*
                        Navegación principal
                        */
                        AppNavigation()
                    }
                }
            }
        }
    }
}
