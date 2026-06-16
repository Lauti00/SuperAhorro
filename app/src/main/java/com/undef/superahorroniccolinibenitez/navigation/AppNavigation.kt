package com.undef.superahorroniccolinibenitez.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.undef.superahorroniccolinibenitez.ui.screens.*
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel
import com.undef.superahorroniccolinibenitez.ui.viewmodel.NuevoProductoViewModel
import com.undef.superahorroniccolinibenitez.ui.viewmodel.NuevoSupermercadoViewModel
import com.undef.superahorroniccolinibenitez.ui.viewmodel.NuevaCompraViewModel
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

    object NuevoSupermercado : AppScreens("nuevo_supermercado_screen")

    object Historial : AppScreens("historial_screen")

    object Perfil : AppScreens("perfil_screen")

    object Estadisticas : AppScreens("estadisticas_screen")

    object Settings : AppScreens("settings_screen")

    object DetalleCompra : AppScreens("detalle_compra/{id}") {

        fun createRoute(id: Int): String {
            return "detalle_compra/$id"
        }
    }

    object EditarCompra : AppScreens("editar_compra/{id}") {

        fun createRoute(id: Int): String {
            return "editar_compra/$id"
        }
    }
}

/*
2. Creamos el orquestador de navegación
*/
@Composable
fun AppNavigation(
    superAhorroViewModel: SuperAhorroViewModel
) {

    val navController = rememberNavController()

    /*
    INSTANCIAS COMPARTIDAS CENTRALES

    Evita que Room pierda los datos al destruir
    pantallas o sesiones.
    */
    val sharedHomeViewModel: HomeViewModel = viewModel()

    val sharedNuevoProductoViewModel: NuevoProductoViewModel =
        viewModel()

    val sharedNuevoSupermercadoViewModel: NuevoSupermercadoViewModel =
        viewModel()

    val sharedEditarCompraViewModel: NuevaCompraViewModel =
        viewModel(key = "editar_compra_vm")

    NavHost(
        navController = navController,
        startDestination = AppScreens.Splash.route
    ) {

        // =========================
        // SPLASH
        // =========================

        composable(AppScreens.Splash.route) {

            SplashScreen(

                onNavigateToLogin = {

                    navController.navigate(
                        AppScreens.Login.route
                    ) {

                        popUpTo(
                            AppScreens.Splash.route
                        ) {
                            inclusive = true
                        }
                    }
                },

                onNavigateToHome = {

                    navController.navigate(
                        AppScreens.Home.route
                    ) {

                        popUpTo(
                            AppScreens.Splash.route
                        ) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // =========================
        // LOGIN
        // =========================

        composable(AppScreens.Login.route) {

            LoginScreen(

                onLoginSuccess = {

                    navController.navigate(
                        AppScreens.Home.route
                    ) {

                        popUpTo(
                            AppScreens.Login.route
                        ) {
                            inclusive = true
                        }
                    }
                },

                onNavigateToRegister = {

                    navController.navigate(
                        AppScreens.Registro.route
                    )
                },

                onNavigateToForgotPassword = {

                    navController.navigate(
                        AppScreens.OlvidarPassword.route
                    )
                }
            )
        }

        // =========================
        // REGISTRO
        // =========================

        composable(AppScreens.Registro.route) {

            RegistroScreen(

                onRegisterSuccess = {

                    navController.navigate(
                        AppScreens.Home.route
                    ) {

                        popUpTo(
                            AppScreens.Login.route
                        ) {
                            inclusive = true
                        }
                    }
                },

                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // =========================
        // RECUPERAR PASSWORD
        // =========================

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

        // =========================
        // HOME
        // =========================

        composable(AppScreens.Home.route) {

            HomeScreen(

                /*
                IMPORTANTE:
                Usamos el ViewModel compartido
                */
                viewModel = sharedHomeViewModel,

                onLogout = {

                    navController.navigate(
                        AppScreens.Login.route
                    ) {

                        popUpTo(
                            AppScreens.Home.route
                        ) {
                            inclusive = true
                        }
                    }
                },

                onNavigateToHistorial = {

                    navController.navigate(
                        AppScreens.Historial.route
                    )
                },

                onNavigateToEstadisticas = {

                    navController.navigate(
                        AppScreens.Estadisticas.route
                    )
                },

                onNavigateToNuevaCompra = {

                    navController.navigate(
                        AppScreens.NuevaCompra.route
                    )
                },

                onNavigateToPerfil = {

                    navController.navigate(
                        AppScreens.Perfil.route
                    )
                },

                onNavigateToSettings = {

                    navController.navigate(
                        AppScreens.Settings.route
                    )
                },

                onCompraClick = { compra ->

                    navController.navigate(
                        AppScreens.DetalleCompra.createRoute(
                            compra.id
                        )
                    )
                }
            )
        }

        // =========================
        // PERFIL
        // =========================

        composable(AppScreens.Perfil.route) {

            ProfileScreen(
                viewModel = sharedHomeViewModel,

                onBack = {
                    navController.popBackStack()
                },

                onSaveProfile = {
                    navController.popBackStack()
                }
            )
        }

        // =========================
        // HISTORIAL
        // =========================

        composable(AppScreens.Historial.route) {

            HistorialScreen(

                viewModel = sharedHomeViewModel,

                onBack = {
                    navController.popBackStack()
                },

                onCompraClick = { compraId ->

                    navController.navigate(
                        AppScreens.DetalleCompra.createRoute(
                            compraId
                        )
                    )
                }
            )
        }

        // =========================
        // ESTADÍSTICAS
        // =========================

        composable(AppScreens.Estadisticas.route) {

            EstadisticasScreen(

                viewModel = sharedHomeViewModel,

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // =========================
        // SETTINGS
        // =========================

        composable(AppScreens.Settings.route) {

            SettingsScreen(

                homeViewModel = sharedHomeViewModel,

                onBack = {
                    navController.popBackStack()
                },

                onNavigateToNuevoProducto = {

                    navController.navigate(
                        AppScreens.NuevoProducto.route
                    )
                },

                onNavigateToNuevoSupermercado = {

                    navController.navigate(
                        AppScreens.NuevoSupermercado.route
                    )
                }
            )
        }

        // =========================
        // NUEVO PRODUCTO
        // =========================

        composable(AppScreens.NuevoProducto.route) {

            /*
            IMPORTANTE:
            Ahora usamos el ViewModel REAL de Room
            */
            NuevoProductoScreen(

                homeViewModel =
                    sharedHomeViewModel,

                nuevoProductoViewModel =
                    sharedNuevoProductoViewModel,

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // =========================
        // NUEVO SUPERMERCADO
        // =========================

        composable(AppScreens.NuevoSupermercado.route) {

            NuevoSupermercadoScreen(

                homeViewModel =
                    sharedHomeViewModel,

                nuevoSupermercadoViewModel =
                    sharedNuevoSupermercadoViewModel,

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // =========================
        // NUEVA COMPRA
        // =========================

        composable(AppScreens.NuevaCompra.route) {

            NuevaCompraScreen(

                homeViewModel =
                    sharedHomeViewModel,

                onBack = {
                    navController.popBackStack()
                },

                onCompraGuardada = {
                    navController.popBackStack()
                },

                onNavigateToNuevoProducto = {

                    navController.navigate(
                        AppScreens.NuevoProducto.route
                    )
                },

                onNavigateToNuevoSupermercado = {

                    navController.navigate(
                        AppScreens.NuevoSupermercado.route
                    )
                }
            )
        }

        // =========================
        // DETALLE COMPRA
        // =========================

        composable(

            route = AppScreens.DetalleCompra.route,

            arguments = listOf(

                navArgument("id") {

                    type = NavType.IntType
                }
            )

        ) { backStackEntry ->

            val compraId =
                backStackEntry.arguments?.getInt("id")

            val compra =
                sharedHomeViewModel.compras.value.find {

                    it.id == compraId
                }

            compra?.let {

                DetalleCompraScreen(

                    compra = it,

                    onBack = {
                        navController.popBackStack()
                    },

                    onEditarCompra = {
                        navController.navigate(
                            AppScreens.EditarCompra.createRoute(it.id)
                        )
                    }
                )
            }
        }

        // =========================
        // EDITAR COMPRA
        // =========================

        composable(
            route = AppScreens.EditarCompra.route,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val compraId =
                backStackEntry.arguments?.getInt("id")

            val compra =
                sharedHomeViewModel.compras.value.find {
                    it.id == compraId
                }

            compra?.let {

                EditarCompraScreen(

                    compra = it,

                    homeViewModel = sharedHomeViewModel,

                    editarCompraViewModel = sharedEditarCompraViewModel,

                    onBack = {
                        sharedEditarCompraViewModel.limpiarEstado()
                        navController.popBackStack()
                    },

                    onCompraGuardada = {
                        sharedEditarCompraViewModel.limpiarEstado()
                        navController.popBackStack()
                        navController.popBackStack()
                    },

                    onNavigateToNuevoProducto = {
                        navController.navigate(AppScreens.NuevoProducto.route)
                    },

                    onNavigateToNuevoSupermercado = {
                        navController.navigate(AppScreens.NuevoSupermercado.route)
                    }
                )
            }
        }
    }
}