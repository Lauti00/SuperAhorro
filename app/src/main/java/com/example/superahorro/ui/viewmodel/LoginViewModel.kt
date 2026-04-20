package com.example.superahorro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

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
    }

    //  Actualizar password
    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    // Validación básica
    fun login(): Boolean {
        return if (email.isBlank() || password.isBlank()) {
            errorMessage = "Completa todos los campos"
            false
        } else {
            errorMessage = null
            true
        }
    }
}