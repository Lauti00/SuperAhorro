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

    //  Lista de compras en memoria (estado observable)
    private val _compras = mutableStateListOf<Compra>()
    val compras: List<Compra> = _compras

    /*
    Variable para guardar la compra seleccionada
    Esto nos permite compartir datos entre pantallas SIN tener base de datos todavía
    */
    var compraSeleccionada by mutableStateOf<Compra?>(null)
        private set

    // Función para agregar una compra a la lista
    fun agregarCompra(compra: Compra){
        _compras.add(compra)
    }

    /*
    Función para seleccionar una compra cuando el usuario la toca
    */
    fun seleccionarCompra(compra: Compra){
        compraSeleccionada = compra
    }

    // Email del usuario (para mostrar en UI)
    var userEmail by mutableStateOf("")
        private set

    /*
     Nombre del usuario (para mostrar en UI)
    */
    var userName by mutableStateOf("")
        private set

    init {
        //  Cargamos el email y el nombre guardado desde DataStore
        viewModelScope.launch {
            userEmail = userPreferences.userEmail.first()
            val savedName = userPreferences.userName.first()
            // Si hay un nombre guardado lo usamos, si no, derivamos del email
            userName = if (savedName.isNotEmpty()) savedName else userEmail.substringBefore("@")
        }
    }

    // Función para guardar el nombre en DataStore y actualizar el estado
    fun guardarNombre(nuevoNombre: String) {
        viewModelScope.launch {
            userPreferences.saveUserName(nuevoNombre)
            userName = nuevoNombre
        }
    }

    //  Logout real
    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferences.logout()
            onLogoutComplete()
        }
    }
}
