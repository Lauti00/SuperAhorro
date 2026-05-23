package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorroniccolinibenitez.R
import com.undef.superahorroniccolinibenitez.ui.components.*
import com.undef.superahorroniccolinibenitez.ui.viewmodel.OlvidarPasswordViewModel

@Composable
fun OlvidarPasswordScreen(
    onBack: () -> Unit,
    onPasswordResetSuccess: () -> Unit,
    viewModel: OlvidarPasswordViewModel = viewModel()
) {
    // Colectamos los estados
    val email by viewModel.email.collectAsState()
    val pasoActual by viewModel.pasoActual.collectAsState()
    val codigo by viewModel.codigo.collectAsState()
    val nuevaPassword by viewModel.nuevaPassword.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

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
                onValueChange = { viewModel.onEmailChange(it) },
                label = stringResource(id = R.string.label_email),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            errorMessage?.let {
                EspacioPequeño()
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            EspacioGrande()

            SuperAhorroButton(
                text = stringResource(id = R.string.btn_enviar_codigo),
                onClick = { viewModel.enviarCodigo() },
                enabled = errorMessage == null && email.isNotBlank()
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
                onValueChange = { viewModel.onCodigoChange(it) },
                label = stringResource(id = R.string.label_codigo),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            EspacioNormal()

            SuperAhorroTextField(
                value = nuevaPassword,
                onValueChange = { viewModel.onNuevaPasswordChange(it) },
                label = stringResource(id = R.string.label_nueva_password),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            errorMessage?.let {
                EspacioPequeño()
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            EspacioGrande()

            SuperAhorroButton(
                text = stringResource(id = R.string.btn_guardar_password),
                onClick = {
                    viewModel.guardarNuevaPassword { onPasswordResetSuccess() }
                },
                enabled = errorMessage == null && 
                          codigo.isNotBlank() && 
                          nuevaPassword.isNotBlank()
            )

            EspacioNormal()

            SuperAhorroTextButton(
                text = stringResource(id = R.string.btn_reenviar_codigo),
                onClick = { viewModel.volverAlPaso1() }
            )
        }
    }
}
