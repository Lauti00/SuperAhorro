package com.example.superahorro.ui.screens

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.example.superahorro.R
import com.example.superahorro.navigation.AppScreens
import com.example.superahorro.ui.components.* // Importamos AuthContainer, Spacers, Botones, etc.

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Usamos nuestro contenedor maestro para el diseño base
    AuthContainer {

        BrandHeader()

        EspacioGrande()

        SuperAhorroTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(id = R.string.label_email),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        EspacioNormal()

        SuperAhorroTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(id = R.string.label_password),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        EspacioPequeño()

        SuperAhorroTextButton(
            text = stringResource(id = R.string.btn_olvide_password),
            onClick = { navController.navigate(AppScreens.OlvidarPassword.route) }
        )

        EspacioNormal()

        SuperAhorroButton(
            text = stringResource(id = R.string.btn_ingresar),
            onClick = {
                navController.navigate(AppScreens.Home.route) {
                    popUpTo(AppScreens.Login.route) { inclusive = true }
                }
            }
        )

        EspacioPequeño()

        SuperAhorroTextButton(
            text = stringResource(id = R.string.btn_ir_registro),
            onClick = { navController.navigate(AppScreens.Registro.route) }
        )
    }
}