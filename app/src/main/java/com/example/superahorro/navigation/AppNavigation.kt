package com.example.superahorro.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.superahorro.ui.screens.*

/*
1. Definimos las rutas de nuestra app
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

        //  SPLASH
        composable(AppScreens.Splash.route) {
            SplashScreen(navController)
        }

        //  LOGIN
        composable(AppScreens.Login.route) {
            LoginScreen(navController)
        }

        //  REGISTRO
        composable(AppScreens.Registro.route) {
            RegistroScreen(navController)
        }

        //  RECUPERAR PASSWORD
        composable(AppScreens.OlvidarPassword.route) {
            OlvidarPasswordScreen(navController)
        }

        //  HOME
        composable(AppScreens.Home.route) {
            HomeScreen(navController)
        }

        //  PERFIL
        composable(AppScreens.Perfil.route) {
            ProfileScreen(navController)
        }

        //  HISTORIAL
        composable(AppScreens.Historial.route) {
            HistorialScreen(navController)
        }

        //  ESTADÍSTICAS
        composable(AppScreens.Estadisticas.route) {
            EstadisticasScreen(navController)
        }

        //  NUEVA COMPRA
        composable(AppScreens.NuevaCompra.route) {
            NuevaCompraScreen(navController)
        }
    }
}