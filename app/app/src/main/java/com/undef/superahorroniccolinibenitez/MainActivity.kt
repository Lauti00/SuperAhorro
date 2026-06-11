package com.undef.superahorroniccolinibenitez

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.undef.superahorroniccolinibenitez.data.datastore.SuperAhorroDatabase
import com.undef.superahorroniccolinibenitez.data.datastore.UserPreferences
import com.undef.superahorroniccolinibenitez.data.datastore.repository.SuperAhorroRepository
import com.undef.superahorroniccolinibenitez.navigation.AppNavigation
import com.undef.superahorroniccolinibenitez.ui.theme.SuperAhorroTheme
import com.undef.superahorroniccolinibenitez.viewmodel.SuperAhorroViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        /*
        IMPORTANTE:
        Usamos SIEMPRE el singleton de Room.
        NO creamos otra base distinta.
        */
        val database =
            SuperAhorroDatabase.getDatabase(applicationContext)

        val repository =
            SuperAhorroRepository(
                database.superAhorroDao()
            )

        /*
        UserPreferences
        */
        val userPreferences =
            UserPreferences(applicationContext)

        /*
        Factory del ViewModel
        */
        val superAhorroViewModel: SuperAhorroViewModel by viewModels {

            object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>
                ): T {

                    return SuperAhorroViewModel(
                        repository
                    ) as T
                }
            }
        }

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
                        AppNavigation(
                            superAhorroViewModel =
                                superAhorroViewModel
                        )
                    }
                }
            }
        }
    }
}