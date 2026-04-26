package com.example.superahorro.ui.screens

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.superahorro.R
import com.example.superahorro.ui.components.*

@Composable
fun RegistroScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthContainer {

        SuperAhorroTitle(text = stringResource(id = R.string.title_registro))

        EspacioGrande()

        SuperAhorroTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = stringResource(id = R.string.label_nombre)
        )

        EspacioNormal()

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

        EspacioGrande()

        SuperAhorroButton(
            text = stringResource(id = R.string.btn_registrarse),
            onClick = {
                    onRegisterSuccess()
            }
        )

        EspacioPequeño()

        SuperAhorroTextButton(
            text = stringResource(id = R.string.btn_volver_login),
            onClick = onBackToLogin
        )
    }
}