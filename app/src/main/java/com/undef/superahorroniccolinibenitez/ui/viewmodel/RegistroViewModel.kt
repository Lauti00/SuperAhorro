package com.undef.superahorroniccolinibenitez.ui.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    var nombre by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onNombreChange(newNombre: String) {
        nombre = newNombre
    }

    fun onEmailChange(newEmail: String) {
        email = newEmail
        validateEmail()
    }

    private fun validateEmail() {
        errorMessage = when {
            email.isBlank() -> null
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Formato de correo inválido"
            else -> null
        }
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
        if (password.length > 0 && password.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres"
        } else {
            validateEmail() // Vuelve a validar el email si la pass está ok
        }
    }

    fun register(onSuccess: () -> Unit) {
        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
            errorMessage = "Completa todos los campos"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Formato de correo inválido"
            return
        }

        if (password.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        errorMessage = null
        // Aquí iría la lógica de registro real (API, DB, etc.)
        onSuccess()
    }
}
