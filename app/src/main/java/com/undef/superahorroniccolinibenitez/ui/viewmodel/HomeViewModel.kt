package com.undef.superahorroniccolinibenitez.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroniccolinibenitez.data.datastore.UserPreferences
import com.undef.superahorroniccolinibenitez.model.CatalogoProducto
import com.undef.superahorroniccolinibenitez.model.Compra
import com.undef.superahorroniccolinibenitez.model.Producto
import com.undef.superahorroniccolinibenitez.model.catalogoProductos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    // =========================
    // LISTA DE COMPRAS
    // =========================

    // Lista de compras en memoria (estado observable)
    private val _compras = MutableStateFlow<List<Compra>>(emptyList())

    val compras: StateFlow<List<Compra>> = _compras.asStateFlow()

    /*
    Compra seleccionada para navegación entre pantallas
    */
    private val _compraSeleccionada = MutableStateFlow<Compra?>(null)
    val compraSeleccionada: StateFlow<Compra?> = _compraSeleccionada.asStateFlow()

    // =========================
    // CRUD COMPRAS
    // =========================

    fun agregarCompra(compra: Compra) {
        _compras.value = _compras.value + compra
    }

    /*
    Seleccionar compra
    */
    fun seleccionarCompra(compra: Compra) {
        _compraSeleccionada.value = compra
    }

    /*
    ELIMINAR COMPRA
    */
    fun eliminarCompra(compra: Compra) {

        _compras.value = _compras.value - compra

        // Si era la seleccionada, la limpiamos
        if (_compraSeleccionada.value?.id == compra.id) {
            _compraSeleccionada.value = null
        }
    }

    /*
    EDITAR COMPRA
    Reemplazamos por ID (importante para mantener coherencia)
    */
    fun editarCompra(compraEditada: Compra) {

        val currentList = _compras.value.toMutableList()
        val index = currentList.indexOfFirst {
            it.id == compraEditada.id
        }

        if (index != -1) {

            currentList[index] = compraEditada
            _compras.value = currentList

            // Actualizamos también la seleccionada
            _compraSeleccionada.value = compraEditada
        }
    }

    /*
    ELIMINAR PRODUCTO DE COMPRA
    */
    fun eliminarProductoDeCompra(
        compraId: Int,
        producto: Producto
    ) {

        val currentList = _compras.value.toMutableList()
        val index = currentList.indexOfFirst {
            it.id == compraId
        }

        if (index != -1) {

            val compra = currentList[index]

            val nuevosProductos =
                compra.productos.toMutableList()

            nuevosProductos.remove(producto)

            val compraEditada = compra.copy(
                productos = nuevosProductos
            )

            currentList[index] = compraEditada
            _compras.value = currentList

            if (_compraSeleccionada.value?.id == compraId) {
                _compraSeleccionada.value = compraEditada
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
    private val _catalogo = MutableStateFlow(catalogoProductos.toList())

    val catalogo: StateFlow<List<CatalogoProducto>> = _catalogo.asStateFlow()

    /*
    AGREGAR NUEVO PRODUCTO AL CATÁLOGO

    RETORNA:
    true  -> si se agregó correctamente
    false -> si ya existe
    */
    fun agregarProductoAlCatalogo(
        codigo: String,
        nombre: String,
        descripcion: String,
        precio: Double
    ): Boolean {

        /*
        VALIDAMOS SI YA EXISTE
        Ignoramos mayúsculas/minúsculas
        */
        val currentCatalogo = _catalogo.value
        val yaExiste = currentCatalogo.any {

            it.nombre.trim().equals(
                nombre.trim(),
                ignoreCase = true
            )
        }

        /*
        Si existe NO agregamos
        */
        if (yaExiste) {
            return false
        }

        /*
        Creamos nuevo producto
        */
        val nuevoProducto = CatalogoProducto(

            id = (currentCatalogo.maxOfOrNull {
                it.id
            } ?: 0) + 1,

            codigo = codigo.trim(),

            nombre = nombre.trim(),

            descripcion = descripcion.trim(),

            precio = precio
        )

        /*
        Lo agregamos al catálogo
        */
        _catalogo.value = currentCatalogo + nuevoProducto

        return true
    }

    /*
    ELIMINAR PRODUCTO DEL CATÁLOGO
    */
    fun eliminarProductoDelCatalogo(
        producto: CatalogoProducto
    ) {

        /*
        Eliminamos del catálogo
        */
        _catalogo.value = _catalogo.value - producto

        /*
        También eliminamos el producto
        de TODAS las compras existentes
        */
        _compras.value = _compras.value.map { compra ->

            val nuevosProductos =
                compra.productos.filter {

                    it.producto.id != producto.id
                }

            compra.copy(
                productos = nuevosProductos
            )
        }

        /*
        Si la compra seleccionada tenía
        ese producto, también la actualizamos
        */
        _compraSeleccionada.value?.let { compra ->

            val nuevosProductos =
                compra.productos.filter {

                    it.producto.id != producto.id
                }

            _compraSeleccionada.value = compra.copy(
                productos = nuevosProductos
            )
        }
    }

    /*
    ACTUALIZAR PRODUCTO DEL CATÁLOGO
    */
    fun actualizarProductoDelCatalogo(
        id: Int,
        nuevoCodigo: String,
        nuevoNombre: String,
        nuevaDescripcion: String,
        nuevoPrecio: Double
    ) {
        val currentCatalogo = _catalogo.value.toMutableList()
        val index = currentCatalogo.indexOfFirst { it.id == id }

        if (index != -1) {
            // 1. Creamos el producto con los datos nuevos
            val productoActualizado = CatalogoProducto(
                id = id,
                codigo = nuevoCodigo.trim(),
                nombre = nuevoNombre.trim(),
                descripcion = nuevaDescripcion.trim(),
                precio = nuevoPrecio
            )

            // 2. Lo reemplazamos en el catálogo
            currentCatalogo[index] = productoActualizado
            _catalogo.value = currentCatalogo

            // 3. Actualizamos este producto en TODAS las compras existentes
            // (para que los precios y nombres se actualicen en el historial)
            _compras.value = _compras.value.map { compra ->
                val nuevosProductos = compra.productos.map { itemCompra ->
                    if (itemCompra.producto.id == id) {
                        // Reemplazamos el producto viejo por el actualizado, manteniendo la cantidad
                        itemCompra.copy(producto = productoActualizado)
                    } else {
                        itemCompra
                    }
                }
                compra.copy(productos = nuevosProductos)
            }

            // 4. Si la compra seleccionada lo tenía, también la actualizamos
            _compraSeleccionada.value?.let { compra ->
                val nuevosProductos = compra.productos.map { itemCompra ->
                    if (itemCompra.producto.id == id) {
                        itemCompra.copy(producto = productoActualizado)
                    } else {
                        itemCompra
                    }
                }
                _compraSeleccionada.value = compra.copy(productos = nuevosProductos)
            }
        }
    }

    // =========================
    // USER DATA
    // =========================

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    init {

        viewModelScope.launch {

            _userEmail.value =
                userPreferences.userEmail.first()

            val savedName =
                userPreferences.userName.first()

            // Si hay un nombre guardado lo usamos,
            // si no lo derivamos del email
            _userName.value =
                if (savedName.isNotEmpty()) {
                    savedName
                } else {
                    _userEmail.value.substringBefore("@")
                }
        }
    }

    fun guardarNombre(nuevoNombre: String) {

        viewModelScope.launch {

            userPreferences.saveUserName(
                nuevoNombre
            )

            _userName.value = nuevoNombre
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

        return _compras.value.sumOf {
            it.total()
        }
    }

    fun cantidadCompras(): Int {

        return _compras.value.size
    }

    fun gastoPorSupermercado(): Map<String, Double> {
        return _compras.value
            .groupBy {
                // Al agrupar, forzamos a que todo sea minúscula con la primera en mayúscula.
                it.supermercado.trim().lowercase().replaceFirstChar { char -> char.uppercase() }
            }
            .mapValues { entry ->
                entry.value.sumOf {
                    it.total()
                }
            }
    }

    fun productoMasComprado(): String {

        val todosProductos =
            _compras.value.flatMap {
                it.productos
            }

        if (todosProductos.isEmpty()) {
            return "Sin datos"
        }

        return todosProductos
            .groupBy {
                it.producto.nombre
            }
            .maxByOrNull { entry ->

                entry.value.sumOf {
                    it.cantidad
                }
            }
            ?.key ?: "Sin datos"
    }
}