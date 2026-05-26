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
        validarCampos() // EXPLICACIÓN: Validamos al cambiar el nombre
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        validarCampos() // EXPLICACIÓN: Validamos al cambiar el correo
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        validarCampos() //  Validamos al cambiar la contraseña
    }

    //  Una sola función centralizada que chequea todo en orden de prioridad y NO se pisa
    private fun validarCampos() {
        _errorMessage.value = when {
            // 1. Si los campos están vacíos mientras tipea, no mostramos error molesto todavía
            _email.value.isBlank() && _password.value.isBlank() -> null

            // 2. Controlamos el formato del correo si el usuario ya escribió algo
            _email.value.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(_email.value).matches() -> {
                "Formato de correo inválido"
            }

            // 3. Controlamos el largo de la contraseña si el usuario ya escribió algo
            _password.value.isNotBlank() && _password.value.length < 6 -> {
                "La contraseña debe tener al menos 6 caracteres"
            }

            // 4. Si pasa todos los filtros, limpiamos el error
            else -> null
        }
    }

    fun register(onSuccess: () -> Unit) {
        // Validación final antes de procesar el clic del botón
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

        onSuccess()
    }
}