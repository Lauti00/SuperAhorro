package com.example.superahorro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superahorro.R
import com.example.superahorro.navigation.AppScreens
import com.example.superahorro.ui.components.MainDrawerContainer
import com.example.superahorro.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    //  IMPORTANTE: creamos el ViewModel
    val viewModel: HomeViewModel = viewModel()

    MainDrawerContainer(
        drawerState = drawerState,

        // Pasamos el email del usuario (si implementaste DataStore)
        userEmail = viewModel.userEmail,

        /*
            Cuando se cierra la sesión, se llama a viewModel y este borra la sesión en dataStore.
            "Se olvida del usuario que se registro, NO ELIMINA AL USUARIO ELIMINA EL LOGUEO
            Sirve para notar que no esta un usuario en sesion"
        */
        onLogout = {
            viewModel.logout {
                navController.navigate(AppScreens.Login.route) {
                    popUpTo(AppScreens.Home.route) { inclusive = true }
                }
            }
        },

        onNavigateToHistorial = {
            navController.navigate(AppScreens.Historial.route)
        }
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    //  Usamos strings.xml
                    title = { Text(stringResource(R.string.home_title)) },

                    navigationIcon = {
                        IconButton(onClick = {
                            if (!drawerState.isOpen) {
                                scope.launch { drawerState.open() }
                            }
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(R.string.cd_menu)
                            )
                        }
                    }
                )
            },

            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(AppScreens.NuevaCompra.route)
                    }
                ) {
                    Text("+")
                }
            }

        ) { paddingValues ->

            /*IMPORTANTE:
            Ahora pasamos las compras del ViewModel (NO mock)
            es decir los datos ya no estan harcodeados , vienen del lugar central
            VIEWMODEL
            * */
            HomeContent(
                paddingValues = paddingValues,
                compras = viewModel.compras
            )
        }
    }
}