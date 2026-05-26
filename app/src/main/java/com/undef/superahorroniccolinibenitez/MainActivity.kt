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
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.undef.superahorroniccolinibenitez.data.datastore.SuperAhorroDatabase
import com.undef.superahorroniccolinibenitez.data.datastore.repository.SuperAhorroRepository
import com.undef.superahorroniccolinibenitez.navigation.AppNavigation
import com.undef.superahorroniccolinibenitez.ui.theme.SuperAhorroTheme
import com.undef.superahorroniccolinibenitez.viewmodel.SuperAhorroViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. INICIALIZAMOS LA BASE DE DATOS DE ROOM
        // Construimos la base de datos en el contexto de la App. "super_ahorro_db" es el nombre del archivo físico en el celular.
        val database = Room.databaseBuilder(
            applicationContext,
            SuperAhorroDatabase::class.java,
            "super_ahorro_db"
        ).build()

        // 2. EXTRAEMOS EL DAO
        // Le pedimos a la base de datos la interfaz con las órdenes (insertar, obtener, etc.)
        val dao = database.superAhorroDao()

        // 3. CREAMOS EL REPOSITORIO
        // Le pasamos el DAO al repositorio que creamos recién para que sirva de puente de datos
        val repository = SuperAhorroRepository(dao)

        // 4. INSTANCIAMOS EL VIEWMODEL USANDO UN FACTORY
        // Como el ViewModel recibe el repositorio por parámetro en su constructor, Android necesita un "Factory"
        // para saber cómo fabricarlo correctamente sin romper el ciclo de vida de la app.
        val superAhorroViewModel: SuperAhorroViewModel by viewModels {
            object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {

                    return SuperAhorroViewModel(repository) as T
                }
            }
        }

        // Permite que la app ocupe toda la pantalla (incluyendo la zona de la barra de notificaciones)
        enableEdgeToEdge()

        setContent {

            SuperAhorroTheme {

                // El Scaffold principal de la aplicación
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    // Usamos un Box para respetar el padding (los márgenes) del sistema
                    // y que el contenido no quede tapado por la barra superior de Android
                    Box(
                        modifier = Modifier.padding(innerPadding)
                    ) {


                        AppNavigation()
                    }
                }
            }
        }
    }
}