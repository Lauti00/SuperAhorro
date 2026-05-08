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
    AuthContainer {
        SuperAhorroTitle(text = stringResource(id = R.string.title_recuperar))

        EspacioGrande()

        if (viewModel.pasoActual == 1) {
            Text(
                text = stringResource(id = R.string.msg_ingresa_correo),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            EspacioGrande()

            SuperAhorroTextField(
                value = viewModel.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = stringResource(id = R.string.label_email),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            viewModel.errorMessage?.let {
                EspacioPequeño()
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            EspacioGrande()

            SuperAhorroButton(
                text = stringResource(id = R.string.btn_enviar_codigo),
                onClick = { viewModel.enviarCodigo() },
                enabled = viewModel.errorMessage == null && viewModel.email.isNotBlank()
            )

            EspacioNormal()

            SuperAhorroTextButton(
                text = stringResource(id = R.string.btn_cancelar),
                onClick = onBack
            )

        } else if (viewModel.pasoActual == 2) {
            Text(
                text = stringResource(id = R.string.msg_codigo_enviado, viewModel.email),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )

            EspacioGrande()

            SuperAhorroTextField(
                value = viewModel.codigo,
                onValueChange = { viewModel.onCodigoChange(it) },
                label = stringResource(id = R.string.label_codigo),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            EspacioNormal()

            SuperAhorroTextField(
                value = viewModel.nuevaPassword,
                onValueChange = { viewModel.onNuevaPasswordChange(it) },
                label = stringResource(id = R.string.label_nueva_password),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            viewModel.errorMessage?.let {
                EspacioPequeño()
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            EspacioGrande()

            SuperAhorroButton(
                text = stringResource(id = R.string.btn_guardar_password),
                onClick = {
                    viewModel.guardarNuevaPassword { onPasswordResetSuccess() }
                },
                enabled = viewModel.errorMessage == null && 
                          viewModel.codigo.isNotBlank() && 
                          viewModel.nuevaPassword.isNotBlank()
            )

            EspacioNormal()

            SuperAhorroTextButton(
                text = stringResource(id = R.string.btn_reenviar_codigo),
                onClick = { viewModel.volverAlPaso1() }
            )
        }
    }
}
