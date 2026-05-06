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
import com.example.superahorro.model.Producto
import com.example.superahorro.model.CatalogoProducto
import com.example.superahorro.model.catalogoProductos

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    // =========================
    // LISTA DE COMPRAS
    // =========================

    // Lista de compras en memoria (estado observable)
    private val _compras = mutableStateListOf<Compra>()
    val compras: List<Compra> = _compras

    /*
    Compra seleccionada para navegación entre pantallas
    */
    var compraSeleccionada by mutableStateOf<Compra?>(null)
        private set

    // =========================
    // CRUD COMPRAS
    // =========================

    fun agregarCompra(compra: Compra){
        _compras.add(compra)
    }

    /*
    Seleccionar compra
    */
    fun seleccionarCompra(compra: Compra){
        compraSeleccionada = compra
    }

    /*
    ELIMINAR COMPRA
    */
    fun eliminarCompra(compra: Compra) {
        _compras.remove(compra)

        // Si era la seleccionada, la limpiamos
        if (compraSeleccionada?.id == compra.id) {
            compraSeleccionada = null
        }
    }

    /*
    EDITAR COMPRA
    Reemplazamos por ID (importante para mantener coherencia)
    */
    fun editarCompra(compraEditada: Compra) {
        val index = _compras.indexOfFirst { it.id == compraEditada.id }

        if (index != -1) {
            _compras[index] = compraEditada

            // Actualizamos también la seleccionada
            compraSeleccionada = compraEditada
        }
    }

    /*
    ELIMINAR PRODUCTO DE COMPRA
    */
    fun eliminarProductoDeCompra(compraId: Int, producto: Producto) {
        val index = _compras.indexOfFirst { it.id == compraId }
        if (index != -1) {
            val compra = _compras[index]
            val nuevosProductos = compra.productos.toMutableList()
            nuevosProductos.remove(producto)

            val compraEditada = compra.copy(productos = nuevosProductos)
            _compras[index] = compraEditada

            if (compraSeleccionada?.id == compraId) {
                compraSeleccionada = compraEditada
            }
        }
    }

    // =========================
    // CATALOGO DINÁMICO
    // =========================

    /*
    Catálogo de productos:
    - Incluye los predefinidos
    - Permite agregar nuevos dinámicamente
    */
    private val _catalogo = mutableStateListOf<CatalogoProducto>().apply {
        addAll(catalogoProductos)
    }

    val catalogo: List<CatalogoProducto> = _catalogo

    /*
    AGREGAR NUEVO PRODUCTO AL CATÁLOGO
    */
    fun agregarProductoAlCatalogo(nombre: String, precio: Double) {

        val nuevoProducto = CatalogoProducto(
            id = (_catalogo.maxOfOrNull { it.id } ?: 0) + 1,
            nombre = nombre,
            precio = precio
        )

        _catalogo.add(nuevoProducto)
    }

    // =========================
    // USER DATA
    // =========================

    var userEmail by mutableStateOf("")
        private set

    var userName by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            userEmail = userPreferences.userEmail.first()
            val savedName = userPreferences.userName.first()

            // Si hay un nombre guardado lo usamos, si no lo derivamos del email
            userName = if (savedName.isNotEmpty()) {
                savedName
            } else {
                userEmail.substringBefore("@")
            }
        }
    }

    fun guardarNombre(nuevoNombre: String) {
        viewModelScope.launch {
            userPreferences.saveUserName(nuevoNombre)
            userName = nuevoNombre
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferences.logout()
            onLogoutComplete()
        }
    }

    // =========================
    // ESTADÍSTICAS
    // =========================

    fun obtenerGastoTotal(): Double {
        return compras.sumOf { it.total() }
    }

    fun cantidadCompras(): Int {
        return compras.size
    }

    fun gastoPorSupermercado(): Map<String, Double> {
        return compras
            .groupBy { it.supermercado }
            .mapValues { entry ->
                entry.value.sumOf { it.total() }
            }
    }

    fun productoMasComprado(): String {
        val todosProductos = compras.flatMap { it.productos }

        if (todosProductos.isEmpty()) return "Sin datos"

        return todosProductos
            .groupBy { it.producto.nombre }
            .maxByOrNull { it.value.sumOf { prod -> prod.cantidad } }
            ?.key ?: "Sin datos"
    }
}