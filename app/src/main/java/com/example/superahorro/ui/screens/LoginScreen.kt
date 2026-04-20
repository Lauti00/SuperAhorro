package com.example.superahorro.ui.screens

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.superahorro.R
import com.example.superahorro.navigation.AppScreens
import com.example.superahorro.ui.components.*
import com.example.superahorro.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(navController: NavController) {

    // CAMBIO CLAVE: ahora usamos ViewModel en vez de remember
    val viewModel: LoginViewModel = viewModel()

    AuthContainer {

        BrandHeader()

        EspacioGrande()

        // Email ahora viene del ViewModel
        SuperAhorroTextField(
            value = viewModel.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = stringResource(id = R.string.label_email),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        EspacioNormal()

        //  Password también controlado por ViewModel
        SuperAhorroTextField(
            value = viewModel.password,
            onValueChange = { viewModel.onPasswordChange(it) },
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
                // Validación desde ViewModel (NO en la UI)
                if (viewModel.login()) {
                    navController.navigate(AppScreens.Home.route) {
                        popUpTo(AppScreens.Login.route) { inclusive = true }
                    }
                }
            }
        )

        //  Mostrar error si existe (mejora UX)
        viewModel.errorMessage?.let {
            EspacioPequeño()
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        EspacioPequeño()

        SuperAhorroTextButton(
            text = stringResource(id = R.string.btn_ir_registro),
            onClick = { navController.navigate(AppScreens.Registro.route) }
        )
    }
}