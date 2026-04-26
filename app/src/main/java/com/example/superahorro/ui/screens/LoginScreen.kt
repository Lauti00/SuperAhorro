package com.example.superahorro.ui.screens

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superahorro.R
import com.example.superahorro.ui.components.*
import com.example.superahorro.ui.viewmodel.LoginViewModel

@Composable
//fun LoginScreen(navController: NavController) {
fun LoginScreen(onLoginSuccess: () -> Unit,
                onNavigateToRegister: () -> Unit,
                onNavigateToForgotPassword: () -> Unit
    ) {

    // CAMBIO CLAVE: ahora usamos ViewModel en vez de remember
    //Usamos el LoginViewModel asociado a esta pantalla, lo crea si no existe y
    // lo mantiene vivo mientras la pantalla exista”
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

        // OLVIDÉ PASSWORD
        SuperAhorroTextButton(
            text = stringResource(id = R.string.btn_olvide_password),
            onClick = onNavigateToForgotPassword
        )

        EspacioNormal()

        // LOGIN
        SuperAhorroButton(
            text = stringResource(id = R.string.btn_ingresar),
            onClick = {
                // Validación desde ViewModel (NO en la UI)
                if (viewModel.login()) {
                    onLoginSuccess()
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

        // REGISTRO
        SuperAhorroTextButton(
            text = stringResource(id = R.string.btn_ir_registro),
            onClick = onNavigateToRegister
        )
    }
}