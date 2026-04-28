package com.example.superahorro.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.superahorro.ui.screens.*
import com.example.superahorro.ui.viewmodel.HomeViewModel

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
    object Historial : AppScreens("historial_screen")
    object Perfil : AppScreens("perfil_screen")
    object Estadisticas : AppScreens("estadisticas_screen")

    object DetalleCompra : AppScreens("detalle_compra/{id}") {
        fun createRoute(id: Int) = "detalle_compra/$id"
    }
}

/*
2. Creamos el orquestador de la navegación
*/
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.Splash.route
    ) {

        //Separo Navegacion de la UI, la pantalla no decide a donde ir, lo hace AppNavigation
        composable(AppScreens.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(AppScreens.Login.route) {
                        popUpTo(AppScreens.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        //  LOGIN
        composable(AppScreens.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppScreens.Home.route) {
                        //Borro de la pila de screen el login evitando deslogueo al dar "Atras"
                        popUpTo(AppScreens.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppScreens.Registro.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(AppScreens.OlvidarPassword.route)
                }
            )
        }

        //  REGISTRO
        composable(AppScreens.Registro.route) {
            RegistroScreen(
                onRegisterSuccess = {
                    navController.navigate(AppScreens.Home.route) {
                        popUpTo(AppScreens.Login.route) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        //  RECUPERAR PASSWORD
        composable(AppScreens.OlvidarPassword.route) {
            OlvidarPasswordScreen(
                onBack = {
                    navController.popBackStack()
                },
                onPasswordResetSuccess = {
                    navController.popBackStack()
                }
            )
        }

        //  HOME
        composable(AppScreens.Home.route) {
            HomeScreen(
                onLogout = {
                    navController.navigate(AppScreens.Login.route) {
                        popUpTo(AppScreens.Home.route) { inclusive = true }
                    }
                },
                onNavigateToHistorial = {
                    navController.navigate(AppScreens.Historial.route)
                },
                onNavigateToEstadisticas = {
                    navController.navigate(AppScreens.Estadisticas.route)
                },
                onNavigateToNuevaCompra = {
                    navController.navigate(AppScreens.NuevaCompra.route)
                },
                onCompraClick = { compra ->
                    navController.navigate(AppScreens.DetalleCompra.createRoute(compra.id))
                }
            )
        }

        //  PERFIL
        composable(AppScreens.Perfil.route) {
            ProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSaveProfile = {
                    navController.popBackStack()
                }
            )
        }

        //  HISTORIAL
        composable(AppScreens.Historial.route) {
            val parentEntry = remember {
                navController.getBackStackEntry(AppScreens.Home.route)
            }
            val homeViewModel: HomeViewModel = viewModel(parentEntry)

            HistorialScreen(
                viewModel = homeViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onCompraClick = { compraId ->
                    navController.navigate(AppScreens.DetalleCompra.createRoute(compraId))
                }
            )
        }

        //  ESTADÍSTICAS
        composable(AppScreens.Estadisticas.route) {
            EstadisticasScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        //  NUEVA COMPRA
        composable(AppScreens.NuevaCompra.route) {
            // Aca obtenemos el ViewModel compartido
            val parentEntry = remember {
                navController.getBackStackEntry(AppScreens.Home.route)
            }
            val homeViewModel: HomeViewModel = viewModel(parentEntry)

            NuevaCompraScreen(
                viewModel = homeViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onCompraGuardada = {
                    navController.popBackStack()
                }
            )
        }

        //DETALLE COMPRA
        composable(
            route = AppScreens.DetalleCompra.route
        )
        //Referenciamos al contexto de navegacion actual (ruta, argumentos, stack de navegacion)
        { backStackEntry ->
            val compraId = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            //Recuperamos el mismo ViewModel del Home
            val parentEntry = remember{
                navController.getBackStackEntry(AppScreens.Home.route)
            }

            val viewModel: HomeViewModel = viewModel(parentEntry)
            //Buscamos en la lista de compras del ViewModel la que coincida con el id de la ruta
            val compra = viewModel.compras.find { it.id == compraId }

            //Si la compra existe se pasa
            compra?.let {
                DetalleCompraScreen(
                    compra = it,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
