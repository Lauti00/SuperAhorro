package com.example.superahorro.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.superahorro.ui.screens.HomeScreen
import com.example.superahorro.ui.screens.LoginScreen
import com.example.superahorro.ui.screens.RegistroScreen
import com.example.superahorro.ui.screens.OlvidarPasswordScreen
import com.example.superahorro.ui.screens.SplashScreen
import com.example.superahorro.ui.screens.ProfileScreen


// 1. Definimos las rutas de nuestra app
sealed class AppScreens(val route: String) {
    object Splash : AppScreens("splash_screen")
    object Login : AppScreens("login_screen")
    object Registro : AppScreens("registro_screen")
    object OlvidarPassword : AppScreens("olvidar_password_screen")
    object Home : AppScreens("home_screen")
    object NuevaCompra : AppScreens("nueva_compra_screen")
    object Historial : AppScreens("historial_screen")
    object Perfil : AppScreens("perfil_screen")
}

// 2. Creamos el orquestador de la navegación
@Composable
fun AppNavigation() {
    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = AppScreens.Splash.route) {

        // AGREGAMOS LA PANTALLA DE SPLASH
        composable(AppScreens.Splash.route) {
            SplashScreen(navController)
        }

        composable(AppScreens.Login.route) {
            LoginScreen(navController)
        }

        // AGREGAMOS LA PANTALLA DE REGISTRO
        composable(AppScreens.Registro.route) {
            RegistroScreen(navController)
        }

        //PANTALLA DE RECUPERAR CONTRASEÑA
        composable(AppScreens.OlvidarPassword.route) {
            OlvidarPasswordScreen(navController)
        }

        composable(AppScreens.Home.route) {
            HomeScreen(navController)
        }

        composable(AppScreens.Perfil.route){
            ProfileScreen(navController)
        }
    }
}