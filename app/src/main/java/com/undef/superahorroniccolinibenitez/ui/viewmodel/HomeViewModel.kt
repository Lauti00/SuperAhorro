package com.undef.superahorroniccolinibenitez.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroniccolinibenitez.data.datastore.UserPreferences
import com.undef.superahorroniccolinibenitez.data.datastore.SuperAhorroDatabase
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CatalogoEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.DetalleCompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.repository.SuperAhorroRepository
import com.undef.superahorroniccolinibenitez.model.CatalogoProducto
import com.undef.superahorroniccolinibenitez.model.Compra
import com.undef.superahorroniccolinibenitez.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val database = SuperAhorroDatabase.getDatabase(application)
    private val repository = SuperAhorroRepository(database.superAhorroDao())

    // =========================
    // LISTA DE COMPRAS (ROOM)
    // =========================
    private val _compras = MutableStateFlow<List<Compra>>(emptyList())
    val compras: StateFlow<List<Compra>> = _compras.asStateFlow()

    private val _compraSeleccionada = MutableStateFlow<Compra?>(null)
    val compraSeleccionada: StateFlow<Compra?> = _compraSeleccionada.asStateFlow()

    // =========================
    // CATALOGO (Modelos de UI y Entidades de Base de Datos)
    // =========================
    private val _catalogo = MutableStateFlow<List<CatalogoProducto>>(emptyList())
    val catalogo: StateFlow<List<CatalogoProducto>> = _catalogo.asStateFlow()

    // Exponemos las entidades crudas para que NuevoProductoScreen no rompa
    private val _listaCatalogo = MutableStateFlow<List<CatalogoEntity>>(emptyList())
    val listaCatalogo: StateFlow<List<CatalogoEntity>> = _listaCatalogo.asStateFlow()

    init {
        viewModelScope.launch {
            repository.catalogo.collectLatest { entidadesCatalogo ->
                _listaCatalogo.value = entidadesCatalogo // Sincroniza entidades crudas
                val mapaCatalogo = entidadesCatalogo.associateBy { it.id }

                repository.todasLasCompras.collectLatest { entidadesCompras ->
                    val todosLosDetalles = withContext(Dispatchers.IO) {
                        repository.obtenerTodosLosDetallesLista()
                    }
                    val mapaDetalles = todosLosDetalles.groupBy { it.idCompra }

                    val listaComprasUI = entidadesCompras.map { entidadCompra ->
                        val detallesDeEstaCompra = mapaDetalles[entidadCompra.id] ?: emptyList()

                        val productosMapeados = detallesDeEstaCompra.mapNotNull { detalle ->
                            val itemCatalogo = mapaCatalogo[detalle.idProducto]
                            if (itemCatalogo != null) {
                                Producto(
                                    producto = CatalogoProducto(
                                        id = itemCatalogo.id,
                                        codigo = itemCatalogo.codigo,
                                        nombre = itemCatalogo.nombre,
                                        descripcion = itemCatalogo.descripcion,
                                        precio = detalle.precioUnitario
                                    ),
                                    cantidad = detalle.cantidad
                                )
                            } else null
                        }

                        Compra(
                            id = entidadCompra.id,
                            supermercado = entidadCompra.supermercado,
                            fecha = entidadCompra.fecha,
                            hora = entidadCompra.hora,
                            productos = productosMapeados,
                            imagenUri = null // NOTA: Si tu CompraEntity de Room llega a tener un campo imagenUri, mapealo acá como entidadCompra.imagenUri
                        )
                    }
                    _compras.value = listaComprasUI
                }
            }
        }

        viewModelScope.launch {
            repository.catalogo.collectLatest { entidades ->
                _catalogo.value = entidades.map { entidad ->
                    CatalogoProducto(
                        id = entidad.id,
                        codigo = entidad.codigo,
                        nombre = entidad.nombre,
                        descripcion = entidad.descripcion,
                        precio = entidad.precio
                    )
                }
            }
        }

        viewModelScope.launch {
            _userEmail.value = userPreferences.userEmail.first()
            val savedName = userPreferences.userName.first()
            _userName.value = if (savedName.isNotEmpty()) savedName else _userEmail.value.substringBefore("@")
        }
    }

    // =========================
    // CRUD COMPRAS PERSISTENTE
    // =========================
    suspend fun agregarCompra(compra: Compra) {
        withContext(Dispatchers.IO) {
            val entidad = CompraEntity(
                id = compra.id,
                supermercado = compra.supermercado,
                fecha = compra.fecha,
                hora = compra.hora
            )
            val idGenerado = repository.insertarCompra(entidad)
            val idFinal = if (compra.id == 0) idGenerado.toInt() else compra.id

            compra.productos.forEach { item ->
                val detalleEntidad = DetalleCompraEntity(
                    idCompra = idFinal,
                    idProducto = item.producto.id,
                    cantidad = item.cantidad,
                    precioUnitario = item.producto.precio
                )
                repository.insertarDetalle(detalleEntidad)
            }
        }
    }

    fun seleccionarCompra(compra: Compra) {
        _compraSeleccionada.value = compra
    }

    fun eliminarCompra(compra: Compra) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.eliminarDetallesPorCompraId(compra.id)
                repository.eliminarCompraPorId(compra.id)
            }
            if (_compraSeleccionada.value?.id == compra.id) {
                _compraSeleccionada.value = null
            }
        }
    }

    fun editarCompra(compraEditada: Compra) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val entidad = CompraEntity(
                    id = compraEditada.id,
                    supermercado = compraEditada.supermercado,
                    fecha = compraEditada.fecha,
                    hora = compraEditada.hora
                )
                repository.actualizarCompra(entidad)

                repository.eliminarDetallesPorCompraId(compraEditada.id)
                compraEditada.productos.forEach { item ->
                    val detalleEntidad = DetalleCompraEntity(
                        idCompra = compraEditada.id,
                        idProducto = item.producto.id,
                        cantidad = item.cantidad,
                        precioUnitario = item.producto.precio
                    )
                    repository.insertarDetalle(detalleEntidad)
                }
            }
            _compraSeleccionada.value = compraEditada
        }
    }

    fun eliminarProductoDeCompra(compraId: Int, producto: Producto) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.eliminarDetalleEspecifico(compraId, producto.producto.id)
            }
            val compraActual = _compras.value.find { it.id == compraId }
            if (compraActual != null && _compraSeleccionada.value?.id == compraId) {
                val nuevosProductos = compraActual.productos.filter { it.producto.id != producto.producto.id }
                _compraSeleccionada.value = compraActual.copy(productos = nuevosProductos)
            }
        }
    }

    // =========================
    // CATALOGO DINÁMICO PERSISTENTE
    // =========================
    fun agregarProductoAlCatalogo(codigo: String, nombre: String, descripcion: String, precio: Double): Boolean {
        val currentCatalogo = _catalogo.value
        val yaExiste = currentCatalogo.any { it.nombre.trim().equals(nombre.trim(), ignoreCase = true) }

        if (yaExiste) return false

        viewModelScope.launch {
            val nuevaEntidad = CatalogoEntity(
                codigo = codigo.trim(),
                nombre = nombre.trim(),
                descripcion = descripcion.trim(),
                precio = precio
            )
            repository.insertarProductoCatalogo(nuevaEntidad)
        }
        return true
    }

    fun eliminarProductoDelCatalogo(producto: CatalogoEntity) {
        viewModelScope.launch {
            repository.eliminarProducto(producto)
        }
    }

    fun actualizarProductoDelCatalogo(id: Int, nuevoCodigo: String, nuevoNombre: String, nuevaDescripcion: String, nuevoPrecio: Double) {
        viewModelScope.launch {
            val entidadActualizada = CatalogoEntity(
                id = id,
                codigo = nuevoCodigo.trim(),
                nombre = nuevoNombre.trim(),
                descripcion = nuevaDescripcion.trim(),
                precio = nuevoPrecio
            )
            repository.actualizarProducto(entidadActualizada)
        }
    }

    // =========================
    // USER DATA Y ESTADÍSTICAS
    // =========================
    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    fun guardarNombre(nuevoNombre: String) {
        viewModelScope.launch {
            userPreferences.saveUserName(nuevoNombre)
            _userName.value = nuevoNombre
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferences.logout()
            onLogoutComplete()
        }
    }

    fun obtenerGastoTotal(): Double = _compras.value.sumOf { it.total() }
    fun cantidadCompras(): Int = _compras.value.size

    fun gastoPorSupermercado(): Map<String, Double> {
        return _compras.value
            .groupBy { it.supermercado.trim().lowercase().replaceFirstChar { char -> char.uppercase() } }
            .mapValues { entry -> entry.value.sumOf { it.total() } }
    }

    fun productoMasComprado(): String {
        val todosProductos = _compras.value.flatMap { it.productos }
        if (todosProductos.isEmpty()) return ""
        return todosProductos
            .groupBy { it.producto.nombre }
            .maxByOrNull { entry -> entry.value.sumOf { item -> item.cantidad } }
            ?.key ?: ""
    }
}