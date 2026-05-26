package com.undef.superahorroniccolinibenitez.ui.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroniccolinibenitez.data.datastore.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences =
        UserPreferences(application)

    // Estado de los inputs
    private val _email =
        MutableStateFlow("")

    val email: StateFlow<String> =
        _email.asStateFlow()

    private val _password =
        MutableStateFlow("")

    val password: StateFlow<String> =
        _password.asStateFlow()

    // Estado de error simple
    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    // Actualizar email
    fun onEmailChange(newEmail: String) {

        _email.value = newEmail

        _errorMessage.value = when {

            _email.value.isBlank() -> null

            !Patterns.EMAIL_ADDRESS
                .matcher(_email.value)
                .matches() -> {

                "Formato de correo inválido"
            }

            else -> null
        }
    }

    // Actualizar password
    fun onPasswordChange(newPassword: String) {

        _password.value = newPassword
    }

    // LOGIN REAL (ahora guarda el usuario)
    fun login(onSuccess: () -> Unit) {

        if (
            _email.value.isBlank() ||
            _password.value.isBlank()
        ) {

            _errorMessage.value =
                "Completa todos los campos"

            return
        }

        // Verificamos que el correo sea el correcto antes de seguir
        if (
            !Patterns.EMAIL_ADDRESS
                .matcher(_email.value)
                .matches()
        ) {

            _errorMessage.value =
                "Formato de correo inválido"

            return
        }

        // Si está todo bien, limpiamos el error y seguimos
        _errorMessage.value = null

        /*
         GUARDAMOS LOS DATOS EN DATASTORE
        */
        viewModelScope.launch {

            /*
             Guardamos el email
            */
            userPreferences.saveUser(
                _email.value
            )

            /*
             Guardamos el estado de sesión
            */
            userPreferences.saveLoginState(
                true
            )

            /*
             También generamos el nombre automáticamente
             Ej: lautaro@gmail.com → lautaro
            */
            val nombre =
                _email.value.substringBefore("@")

            userPreferences.saveUserName(
                nombre
            )

            /*
             Navegamos al Home
            */
            onSuccess()
        }
    }
    /*
 LOGOUT
 Limpia la sesión guardada
*/
    fun logout(onLogoutComplete: () -> Unit) {

        viewModelScope.launch {

            userPreferences.logout()

            onLogoutComplete()
        }
    }
}