package com.undef.superahorroniccolinibenitez.ui.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OlvidarPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _pasoActual = MutableStateFlow(1)
    val pasoActual: StateFlow<Int> = _pasoActual.asStateFlow()

    private val _codigo = MutableStateFlow("")
    val codigo: StateFlow<String> = _codigo.asStateFlow()

    private val _nuevaPassword = MutableStateFlow("")
    val nuevaPassword: StateFlow<String> = _nuevaPassword.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        validateEmail()
    }

    private fun validateEmail() {
        _errorMessage.value = when {
            _email.value.isBlank() -> null
            !Patterns.EMAIL_ADDRESS.matcher(_email.value).matches() -> "Formato de correo inválido"
            else -> null
        }
    }

    fun onCodigoChange(newCodigo: String) {
        _codigo.value = newCodigo
    }

    fun onNuevaPasswordChange(newPassword: String) {
        _nuevaPassword.value = newPassword
        if (_nuevaPassword.value.length in 1..<6) {
            _errorMessage.value = "La contraseña debe tener al menos 6 caracteres"
        } else {
            _errorMessage.value = null
        }
    }

    fun enviarCodigo() {
        if (Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
            _pasoActual.value = 2
            _errorMessage.value = null
        } else {
            _errorMessage.value = "Formato de correo inválido"
        }
    }

    fun guardarNuevaPassword(onSuccess: () -> Unit) {
        if (_codigo.value.isBlank()) {
            _errorMessage.value = "Ingresa el código"
            return
        }
        if (_nuevaPassword.value.length < 6) {
            _errorMessage.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }
        
        _errorMessage.value = null
        onSuccess()
    }

    fun volverAlPaso1() {
        _pasoActual.value = 1
        _errorMessage.value = null
    }
}
