package com.undef.superahorroniccolinibenitez.ui.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onNombreChange(newNombre: String) {
        _nombre.value = newNombre
    }

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

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        if (_password.value.length > 0 && _password.value.length < 6) {
            _errorMessage.value = "La contraseña debe tener al menos 6 caracteres"
        } else {
            validateEmail() // Vuelve a validar el email si la pass está ok
        }
    }

    fun register(onSuccess: () -> Unit) {
        if (_nombre.value.isBlank() || _email.value.isBlank() || _password.value.isBlank()) {
            _errorMessage.value = "Completa todos los campos"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
            _errorMessage.value = "Formato de correo inválido"
            return
        }

        if (_password.value.length < 6) {
            _errorMessage.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        _errorMessage.value = null
        // Aquí iría la lógica de registro real (API, DB, etc.)
        onSuccess()
    }
}
