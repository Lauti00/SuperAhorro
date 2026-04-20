package com.example.superahorro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.superahorro.R
import com.example.superahorro.navigation.AppScreens
import com.example.superahorro.ui.components.ItemCompra
import com.example.superahorro.ui.components.MainDrawerContainer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    MainDrawerContainer(
        drawerState = drawerState,
        onLogout = {
            navController.navigate(AppScreens.Login.route) {
                popUpTo(AppScreens.Home.route) { inclusive = true }
            }
        },
        onNavigateToHistorial = {
            navController.navigate(AppScreens.Historial.route)
        }
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    // 🔹 Usamos strings.xml
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

            // Delegamos el contenido a otro composable
            HomeContent(paddingValues)
        }
    }
}