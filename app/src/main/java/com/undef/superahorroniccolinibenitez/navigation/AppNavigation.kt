package com.undef.superahorroniccolinibenitez.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.undef.superahorroniccolinibenitez.ui.screens.*
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel
import com.undef.superahorroniccolinibenitez.viewmodel.SuperAhorroViewModel

/*
1. Definimos las rutas de nuestra app.
*/
sealed class AppScreens(val route: String) {
    object Splash : AppScreens("splash_screen")
    object Login : AppScreens("login_screen")
    object Registro : AppScreens("registro_screen")
    object OlvidarPassword : AppScreens("olvidar_password_screen")
    object Home : AppScreens("home_screen")
    object NuevaCompra : AppScreens("nueva_compra_screen")
    object NuevoProducto : AppScreens("nuevo_producto_screen")
    object Historial : AppScreens("historial_screen")
    object Perfil : AppScreens("perfil_screen")
    object Estadisticas : AppScreens("estadisticas_screen")
    object Settings : AppScreens("settings_screen")

    object DetalleCompra : AppScreens("detalle_compra/{id}") {
        fun createRoute(id: Int) = "detalle_compra/$id"
    }
}

/*
2. Creamos el orquestador de la navegación
*/
@Composable
fun AppNavigation(viewModel: SuperAhorroViewModel) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.Splash.route
    ) {

        // SPLASH
        composable(AppScreens.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(AppScreens.Login.route) {
                        popUpTo(AppScreens.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(AppScreens.Home.route) {
                        popUpTo(AppScreens.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // LOGIN
        composable(AppScreens.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppScreens.Home.route) {
                        popUpTo(AppScreens.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(AppScreens.Registro.route) },
                onNavigateToForgotPassword = { navController.navigate(AppScreens.OlvidarPassword.route) }
            )
        }

        // REGISTRO
        composable(AppScreens.Registro.route) {
            RegistroScreen(
                onRegisterSuccess = {
                    navController.navigate(AppScreens.Home.route) {
                        popUpTo(AppScreens.Login.route) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // RECUPERAR PASSWORD
        composable(AppScreens.OlvidarPassword.route) {
            OlvidarPasswordScreen(
                onBack = { navController.popBackStack() },
                onPasswordResetSuccess = { navController.popBackStack() }
            )
        }

        // HOME
        composable(AppScreens.Home.route) {
            HomeScreen(
                onLogout = {
                    navController.navigate(AppScreens.Login.route) {
                        popUpTo(AppScreens.Home.route) { inclusive = true }
                    }
                },
                onNavigateToHistorial = { navController.navigate(AppScreens.Historial.route) },
                onNavigateToEstadisticas = { navController.navigate(AppScreens.Estadisticas.route) },
                onNavigateToNuevaCompra = { navController.navigate(AppScreens.NuevaCompra.route) },
                onNavigateToPerfil = { navController.navigate(AppScreens.Perfil.route) },
                onNavigateToSettings = { navController.navigate(AppScreens.Settings.route) },
                onCompraClick = { compra ->
                    navController.navigate(AppScreens.DetalleCompra.createRoute(compra.id))
                }
            )
        }

        // PERFIL
        composable(AppScreens.Perfil.route) {
            val parentEntry = remember(it) { // CORREGIDO: Se añade clave 'it' (NavBackStackEntry) para asegurar estabilidad
                navController.getBackStackEntry(AppScreens.Home.route)
            }
            val homeViewModel: HomeViewModel = viewModel(parentEntry)

            ProfileScreen(
                viewModel = homeViewModel,
                onBack = { navController.popBackStack() },
                onSaveProfile = { navController.popBackStack() }
            )
        }

        // HISTORIAL
        composable(AppScreens.Historial.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(AppScreens.Home.route)
            }
            val homeViewModel: HomeViewModel = viewModel(parentEntry)

            HistorialScreen(
                viewModel = homeViewModel,
                onBack = { navController.popBackStack() },
                onCompraClick = { compraId ->
                    navController.navigate(AppScreens.DetalleCompra.createRoute(compraId))
                }
            )
        }

        // ESTADÍSTICAS
        composable(AppScreens.Estadisticas.route) { backStackEntry ->
            // CORREGIDO: Ahora envuelto en un remember seguro usando la entrada actual como clave
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(AppScreens.Home.route)
            }
            val viewModelHome: HomeViewModel = viewModel(parentEntry)

            EstadisticasScreen(
                viewModel = viewModelHome,
                onBack = { navController.popBackStack() }
            )
        }

        // SETTINGS
        composable(AppScreens.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToNuevoProducto = { navController.navigate(AppScreens.NuevoProducto.route) }
            )
        }

        // NUEVO PRODUCTO
        composable(AppScreens.NuevoProducto.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(AppScreens.Home.route)
            }
            val homeViewModel: HomeViewModel = viewModel(parentEntry)

            NuevoProductoScreen(
                homeViewModel = homeViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // NUEVA COMPRA
        composable(AppScreens.NuevaCompra.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(AppScreens.Home.route)
            }
            val homeViewModel: HomeViewModel = viewModel(parentEntry)

            NuevaCompraScreen(
                homeViewModel = homeViewModel,
                onBack = { navController.popBackStack() },
                onCompraGuardada = { navController.popBackStack() },
                onNavigateToNuevoProducto = { navController.navigate(AppScreens.NuevoProducto.route) }
            )
        }

        // DETALLE COMPRA
        composable(
            route = AppScreens.DetalleCompra.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType }) // CORREGIDO: Declaración explícita del argumento id como Entero
        ) { backStackEntry ->

            // CORREGIDO: Extraemos directamente usando la clave declarada de tipo Int
            val compraId = backStackEntry.arguments?.getInt("id")

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(AppScreens.Home.route)
            }
            val viewModelHome: HomeViewModel = viewModel(parentEntry)

            val compra = viewModelHome.compras.value.find { it.id == compraId }

            compra?.let {
                DetalleCompraScreen(
                    compra = it,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}