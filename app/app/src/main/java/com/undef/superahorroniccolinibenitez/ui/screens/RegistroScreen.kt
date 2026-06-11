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
    // Colectamos los estados del ViewModel
    val nombre by viewModel.nombre.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    AuthContainer {
        SuperAhorroTitle(text = stringResource(id = R.string.title_registro))

        EspacioGrande()

        SuperAhorroTextField(
            value = nombre,
            onValueChange = { viewModel.onNombreChange(it) },
            label = stringResource(id = R.string.label_nombre)
        )

        EspacioNormal()

        SuperAhorroTextField(
            value = email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = stringResource(id = R.string.label_email),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        EspacioNormal()

        SuperAhorroTextField(
            value = password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = stringResource(id = R.string.label_password),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        //  Muestra los errores dinámicos del ViewModel de forma limpia
        errorMessage?.let {
            EspacioPequeño()
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        EspacioGrande()

        SuperAhorroButton(
            text = stringResource(id = R.string.btn_registrarse),
            onClick = {
                viewModel.register { onRegisterSuccess() }
            },
            // El botón se habilitará estrictamente si no hay errores en el ViewModel y ningún campo está vacío
            enabled = errorMessage == null &&
                    nombre.isNotBlank() &&
                    email.isNotBlank() &&
                    password.isNotBlank()
        )

        EspacioPequeño()

        SuperAhorroTextButton(
            text = stringResource(id = R.string.btn_volver_login),
            onClick = onBackToLogin
        )
    }
}