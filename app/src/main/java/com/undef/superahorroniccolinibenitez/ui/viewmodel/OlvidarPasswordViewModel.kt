package com.undef.superahorroniccolinibenitez.ui.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class OlvidarPasswordViewModel(application: Application) : AndroidViewModel(application) {

    var email by mutableStateOf("")
        private set

    var pasoActual by mutableIntStateOf(1)
        private set

    var codigo by mutableStateOf("")
        private set

    var nuevaPassword by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

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

    fun onCodigoChange(newCodigo: String) {
        codigo = newCodigo
    }

    fun onNuevaPasswordChange(newPassword: String) {
        nuevaPassword = newPassword
        if (nuevaPassword.length > 0 && nuevaPassword.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres"
        } else {
            errorMessage = null
        }
    }

    fun enviarCodigo() {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            pasoActual = 2
            errorMessage = null
        } else {
            errorMessage = "Formato de correo inválido"
        }
    }

    fun guardarNuevaPassword(onSuccess: () -> Unit) {
        if (codigo.isBlank()) {
            errorMessage = "Ingresa el código"
            return
        }
        if (nuevaPassword.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres"
            return
        }
        
        errorMessage = null
        onSuccess()
    }

    fun volverAlPaso1() {
        pasoActual = 1
        errorMessage = null
    }
}
