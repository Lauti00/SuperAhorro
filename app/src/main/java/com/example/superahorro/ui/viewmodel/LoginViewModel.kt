package com.example.superahorro.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.superahorro.data.datastore.UserPreferences
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
            !email.contains("@") -> "Email inválido"
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