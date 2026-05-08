package com.undef.superahorroniccolinibenitez.ui.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroniccolinibenitez.data.datastore.UserPreferences
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    //  Estado de los inputs
    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    //  Estado de error simple
    var errorMessage by mutableStateOf<String?>(null)
        private set

    //  Actualizar email
    fun onEmailChange(newEmail: String) {
        email = newEmail

        errorMessage = when {
            email.isBlank() -> null
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Formato de correo inválido"
            else -> null
        }
    }

    //  Actualizar password
    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    // LOGIN REAL (ahora guarda el usuario)
    fun login(onSuccess: () -> Unit) {

        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Completa todos los campos"
            return
        }

        // Verificamos que el correo sea el correcto antes de seguir
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Formato de correo inválido"
            return
        }

        //Si esta todo bien, limpiamos el error y seguimos
        errorMessage = null

        /*
         GUARDAMOS EL EMAIL EN DATASTORE
        */
        viewModelScope.launch {
            userPreferences.saveUser(email)

            /*
             También generamos el nombre automáticamente
             Ej: lautaro@gmail.com → lautaro
            */
            val nombre = email.substringBefore("@")
            userPreferences.saveUserName(nombre)
            onSuccess()
        }
    }
}