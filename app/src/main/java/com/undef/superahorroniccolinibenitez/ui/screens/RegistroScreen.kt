package com.undef.superahorroniccolinibenitez.ui.screens

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorroniccolinibenitez.R
import com.undef.superahorroniccolinibenitez.ui.components.*
import com.undef.superahorroniccolinibenitez.ui.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: RegistroViewModel = viewModel()
) {
    AuthContainer {
        SuperAhorroTitle(text = stringResource(id = R.string.title_registro))

        EspacioGrande()

        SuperAhorroTextField(
            value = viewModel.nombre,
            onValueChange = { viewModel.onNombreChange(it) },
            label = stringResource(id = R.string.label_nombre)
        )

        EspacioNormal()

        SuperAhorroTextField(
            value = viewModel.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = stringResource(id = R.string.label_email),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        EspacioNormal()

        SuperAhorroTextField(
            value = viewModel.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = stringResource(id = R.string.label_password),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        viewModel.errorMessage?.let {
            EspacioPequeño()
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        EspacioGrande()

        SuperAhorroButton(
            text = stringResource(id = R.string.btn_registrarse),
            onClick = {
                viewModel.register { onRegisterSuccess() }
            },
            enabled = viewModel.errorMessage == null && 
                      viewModel.nombre.isNotBlank() && 
                      viewModel.email.isNotBlank() && 
                      viewModel.password.isNotBlank()
        )

        EspacioPequeño()

        SuperAhorroTextButton(
            text = stringResource(id = R.string.btn_volver_login),
            onClick = onBackToLogin
        )
    }
}
