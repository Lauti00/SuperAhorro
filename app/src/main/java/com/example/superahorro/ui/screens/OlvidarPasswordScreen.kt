package com.example.superahorro.ui.screens

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.example.superahorro.R
import com.example.superahorro.ui.components.*

@Composable
fun OlvidarPasswordScreen(
    onBack: () -> Unit,
    onPasswordResetSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pasoActual by remember { mutableStateOf(1) }
    var codigo by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }

    AuthContainer {

        SuperAhorroTitle(text = stringResource(id = R.string.title_recuperar))

        EspacioGrande()

        if (pasoActual == 1) {
            Text(
                text = stringResource(id = R.string.msg_ingresa_correo),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            EspacioGrande()

            SuperAhorroTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(id = R.string.label_email),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            EspacioGrande()

            SuperAhorroButton(
                text = stringResource(id = R.string.btn_enviar_codigo),
                onClick = { if (email.isNotBlank()) pasoActual = 2 }
            )

            EspacioNormal()

            SuperAhorroTextButton(
                text = stringResource(id = R.string.btn_cancelar),
                onClick = onBack
            )

        } else if (pasoActual == 2) {
            Text(
                text = stringResource(id = R.string.msg_codigo_enviado, email),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )

            EspacioGrande()

            SuperAhorroTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = stringResource(id = R.string.label_codigo),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            EspacioNormal()

            SuperAhorroTextField(
                value = nuevaPassword,
                onValueChange = { nuevaPassword = it },
                label = stringResource(id = R.string.label_nueva_password),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            EspacioGrande()

            SuperAhorroButton(
                text = stringResource(id = R.string.btn_guardar_password),
                onClick = {
                    if (codigo.isNotBlank() && nuevaPassword.isNotBlank()) {
                        onPasswordResetSuccess()
                    }
                }
            )

            EspacioNormal()

            SuperAhorroTextButton(
                text = stringResource(id = R.string.btn_reenviar_codigo),
                onClick = { pasoActual = 1 }
            )
        }
    }
}