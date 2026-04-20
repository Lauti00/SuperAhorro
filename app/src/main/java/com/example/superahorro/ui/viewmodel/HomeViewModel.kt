package com.example.superahorro.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.superahorro.data.datastore.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import com.example.superahorro.model.Compra

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _compras = mutableStateListOf<Compra>()
    val compras: List<Compra> = _compras


    fun agregarCompra(compra: Compra){
        _compras.add(compra)
    }

    // Email del usuario (para mostrar en UI)
    var userEmail by mutableStateOf("")
        private set

    init {
        // Cargamos el email guardado
        viewModelScope.launch {
            userEmail = userPreferences.userEmail.first()
        }
    }

    // Logout real
    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferences.logout()
            onLogoutComplete()
        }
    }
}
