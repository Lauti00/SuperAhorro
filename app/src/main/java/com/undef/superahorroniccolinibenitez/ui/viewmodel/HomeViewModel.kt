package com.undef.superahorroniccolinibenitez.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroniccolinibenitez.data.datastore.SuperAhorroDatabase
import com.undef.superahorroniccolinibenitez.data.datastore.UserPreferences
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CatalogoEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.DetalleCompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.repository.SuperAhorroRepository
import com.undef.superahorroniccolinibenitez.model.CatalogoProducto
import com.undef.superahorroniccolinibenitez.model.Compra
import com.undef.superahorroniccolinibenitez.model.Producto
import com.undef.superahorroniccolinibenitez.model.catalogoProductos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val database = SuperAhorroDatabase.getDatabase(application)
    private val repository = SuperAhorroRepository(database.superAhorroDao())

    // =========================
    // USER DATA Y ESTADÍSTICAS
    // =========================

    /*

    Estas variables DEBEN estar antes del init
    para evitar NullPointerException
    */
    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    // =========================
    // LISTA DE COMPRAS (ROOM)
    // =========================

    private val _compras = MutableStateFlow<List<Compra>>(emptyList())
    val compras: StateFlow<List<Compra>> = _compras.asStateFlow()

    private val _compraSeleccionada = MutableStateFlow<Compra?>(null)
    val compraSeleccionada: StateFlow<Compra?> = _compraSeleccionada.asStateFlow()

    // =========================
    // CATALOGO Modelos de UI y Entidades de Base de Datos
    // =========================

    private val _catalogo = MutableStateFlow<List<CatalogoProducto>>(emptyList())
    val catalogo: StateFlow<List<CatalogoProducto>> = _catalogo.asStateFlow()

    // Exponemos las entidades crudas para que NuevoProductoScreen no rompa
    private val _listaCatalogo = MutableStateFlow<List<CatalogoEntity>>(emptyList())
    val listaCatalogo: StateFlow<List<CatalogoEntity>> = _listaCatalogo.asStateFlow()

    init {

        /*
        CARGA INICIAL DEL CATÁLOGO EN ROOM

        Si la tabla está vacía, insertamos los productos base.
        Esto evita que al volver a iniciar sesión el catálogo quede vacío
        y las compras no puedan reconstruir correctamente sus productos.
        */
        inicializarCatalogoSiHaceFalta()

        /*
        CARGA DE COMPRAS, DETALLES Y CATÁLOGO DESDE ROOM

        Usamos combine para observar todo junto:
        - compras
        - detalles de compra
        - productos del catálogo

        Así la pantalla se reconstruye desde la base de datos real
        cada vez que se vuelve a iniciar sesión.
        */
        viewModelScope.launch {

            combine(
                repository.todasLasCompras,
                repository.todosLosDetalles,
                repository.catalogo
            ) { entidadesCompras, entidadesDetalles, entidadesCatalogo ->

                _listaCatalogo.value = entidadesCatalogo

                _catalogo.value =
                    entidadesCatalogo.map { entidad ->

                        CatalogoProducto(
                            id = entidad.id,
                            codigo = entidad.codigo,
                            nombre = entidad.nombre,
                            descripcion = entidad.descripcion,
                            precio = entidad.precio
                        )
                    }

                val mapaCatalogo =
                    entidadesCatalogo.associateBy { it.id }

                val mapaDetalles =
                    entidadesDetalles.groupBy { it.idCompra }

                entidadesCompras.map { entidadCompra ->

                    val detallesDeEstaCompra =
                        mapaDetalles[entidadCompra.id] ?: emptyList()

                    val productosMapeados =
                        detallesDeEstaCompra.mapNotNull { detalle ->

                            val itemCatalogo =
                                mapaCatalogo[detalle.idProducto]

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
                        imagenUri = entidadCompra.imagenUri
                    )
                }
            }.collectLatest { listaComprasUI ->

                _compras.value = listaComprasUI
            }
        }

        /*
        CARGA DE DATOS DEL USUARIO

        Ahora observamos DataStore en tiempo real.
        Así el Home, el Drawer y el Perfil se actualizan cuando cambia la sesión
        o cuando se modifica el nombre del perfil.
        */
        viewModelScope.launch {

            combine(
                userPreferences.userEmail,
                userPreferences.userName
            ) { email, name ->

                val nombreFinal =
                    if (name.isNotEmpty()) {
                        name
                    } else {
                        email.substringBefore("@")
                    }

                email to nombreFinal

            }.collectLatest { (email, nombreFinal) ->

                _userEmail.value = email

                _userName.value = nombreFinal
            }
        }
    }

    /*
    Inserta el catálogo inicial solamente si Room no tiene productos.
    */
    private fun inicializarCatalogoSiHaceFalta() {

        viewModelScope.launch(Dispatchers.IO) {

            val cantidadProductos =
                repository.contarProductosCatalogo()

            if (cantidadProductos == 0) {

                catalogoProductos.forEach { producto ->

                    repository.insertarProductoCatalogo(
                        CatalogoEntity(
                            codigo = producto.codigo,
                            nombre = producto.nombre,
                            descripcion = producto.descripcion,
                            precio = producto.precio
                        )
                    )
                }
            }
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
                hora = compra.hora,
                imagenUri = compra.imagenUri
            )

            val idGenerado =
                repository.insertarCompra(entidad)

            val idFinal =
                if (compra.id == 0) {
                    idGenerado.toInt()
                } else {
                    compra.id
                }

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
                    hora = compraEditada.hora,
                    imagenUri = compraEditada.imagenUri
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

    fun eliminarProductoDeCompra(
        compraId: Int,
        producto: Producto
    ) {

        viewModelScope.launch {

            withContext(Dispatchers.IO) {

                repository.eliminarDetalleEspecifico(
                    compraId,
                    producto.producto.id
                )
            }

            val compraActual =
                _compras.value.find { it.id == compraId }

            if (
                compraActual != null &&
                _compraSeleccionada.value?.id == compraId
            ) {

                val nuevosProductos =
                    compraActual.productos.filter {

                        it.producto.id != producto.producto.id
                    }

                _compraSeleccionada.value =
                    compraActual.copy(productos = nuevosProductos)
            }
        }
    }

    // =========================
    // CATALOGO DINÁMICO PERSISTENTE
    // =========================

    fun agregarProductoAlCatalogo(
        codigo: String,
        nombre: String,
        descripcion: String,
        precio: Double
    ): Boolean {

        val currentCatalogo = _catalogo.value

        val yaExiste =
            currentCatalogo.any {

                it.nombre.trim().equals(
                    nombre.trim(),
                    ignoreCase = true
                )
            }

        if (yaExiste) return false

        viewModelScope.launch {

            val nuevaEntidad = CatalogoEntity(
                codigo = codigo.trim(),
                nombre = nombre.trim(),
                descripcion = descripcion.trim(),
                precio = precio
            )

            repository.insertarProductoCatalogo(
                nuevaEntidad
            )
        }

        return true
    }

    fun eliminarProductoDelCatalogo(
        producto: CatalogoEntity
    ) {

        viewModelScope.launch {

            repository.eliminarProducto(producto)
        }
    }

    fun actualizarProductoDelCatalogo(
        id: Int,
        nuevoCodigo: String,
        nuevoNombre: String,
        nuevaDescripcion: String,
        nuevoPrecio: Double
    ) {

        viewModelScope.launch {

            val entidadActualizada = CatalogoEntity(
                id = id,
                codigo = nuevoCodigo.trim(),
                nombre = nuevoNombre.trim(),
                descripcion = nuevaDescripcion.trim(),
                precio = nuevoPrecio
            )

            repository.actualizarProducto(
                entidadActualizada
            )
        }
    }

    fun guardarNombre(nuevoNombre: String) {

        viewModelScope.launch {

            val nombreLimpio =
                nuevoNombre.trim()

            /*
            Guardamos el nombre activo de la sesión.
            */
            userPreferences.saveUserName(nombreLimpio)

            /*
            También actualizamos el nombre registrado.
            Si no hacemos esto, al cerrar sesión y volver a iniciar,
            LoginViewModel vuelve a cargar el nombre viejo.
            */
            userPreferences.updateRegisteredName(nombreLimpio)

            _userName.value = nombreLimpio
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {

        viewModelScope.launch {

            /*
            Cerramos sesión solamente en DataStore.
            NO borramos Room, para que las compras sigan persistidas.
            */
            userPreferences.logout()

            onLogoutComplete()
        }
    }

    fun obtenerGastoTotal(): Double {

        return _compras.value.sumOf { it.total() }
    }

    fun cantidadCompras(): Int {

        return _compras.value.size
    }

    fun gastoPorSupermercado(): Map<String, Double> {

        return _compras.value
            .groupBy {
                it.supermercado
                    .trim()
                    .lowercase()
                    .replaceFirstChar { char ->
                        char.uppercase()
                    }
            }
            .mapValues { entry ->
                entry.value.sumOf { it.total() }
            }
    }

    fun productoMasComprado(): String {

        val todosProductos =
            _compras.value.flatMap { it.productos }

        if (todosProductos.isEmpty()) return ""

        return todosProductos
            .groupBy { it.producto.nombre }
            .maxByOrNull { entry ->
                entry.value.sumOf { item ->
                    item.cantidad
                }
            }
            ?.key ?: ""
    }
}