package com.undef.superahorroniccolinibenitez.ui.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroniccolinibenitez.data.datastore.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

    // LOGIN REAL LOCAL
    fun login(onSuccess: () -> Unit) {

        if (
            _email.value.isBlank() ||
            _password.value.isBlank()
        ) {

            _errorMessage.value =
                "Completa todos los campos"

            return
        }

        // Verificamos que el correo sea válido antes de seguir
        if (
            !Patterns.EMAIL_ADDRESS
                .matcher(_email.value)
                .matches()
        ) {

            _errorMessage.value =
                "Formato de correo inválido"

            return
        }

        /*

         Validamos contra el usuario registrado en DataStore.
        */
        viewModelScope.launch {

            val emailIngresado =
                _email.value.trim().lowercase()

            val passwordIngresada =
                _password.value

            val emailRegistrado =
                userPreferences.registeredEmail.first()

            val passwordRegistrada =
                userPreferences.registeredPassword.first()

            val nombreRegistrado =
                userPreferences.registeredName.first()

            if (emailRegistrado.isBlank() || passwordRegistrada.isBlank()) {

                _errorMessage.value =
                    "No hay ningún usuario registrado"

                return@launch
            }

            if (emailIngresado != emailRegistrado) {

                _errorMessage.value =
                    "Usuario no registrado"

                return@launch
            }

            if (passwordIngresada != passwordRegistrada) {

                _errorMessage.value =
                    "Contraseña incorrecta"

                return@launch
            }

            // Si está todo bien, limpiamos el error y seguimos
            _errorMessage.value = null

            /*
             Guardamos el email de la sesión actual
            */
            userPreferences.saveUser(
                emailIngresado
            )

            /*
             Guardamos el estado de sesión
            */
            userPreferences.saveLoginState(
                true
            )

            /*
             Guardamos el nombre del usuario activo
            */
            userPreferences.saveUserName(
                nombreRegistrado
            )

            /*
             Navegamos al Home
            */
            onSuccess()
        }
    }
}