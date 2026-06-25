package com.undef.superahorroniccolinibenitez.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroniccolinibenitez.data.datastore.SuperAhorroDatabase
import com.undef.superahorroniccolinibenitez.data.datastore.UserPreferences
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CatalogoEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.DetalleCompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.SupermercadoEntity
import com.undef.superahorroniccolinibenitez.data.datastore.repository.SuperAhorroRepository
import com.undef.superahorroniccolinibenitez.data.datastore.local.mappers.toModel
import com.undef.superahorroniccolinibenitez.data.datastore.local.mappers.toEntity
import com.undef.superahorroniccolinibenitez.data.network.ofertas.OfertasRepository
import android.util.Log
import com.undef.superahorroniccolinibenitez.model.CatalogoProducto
import com.undef.superahorroniccolinibenitez.model.Compra
import com.undef.superahorroniccolinibenitez.model.Producto
import com.undef.superahorroniccolinibenitez.model.Supermercado
import com.undef.superahorroniccolinibenitez.model.catalogoProductos
import com.undef.superahorroniccolinibenitez.model.supermercadosPrecargados
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

    /*
    Instancia del repository de red para el POST.
    El GET de ofertas sigue siendo responsabilidad de OfertasViewModel.
    */
    private val ofertasRepository = OfertasRepository()

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


    // =========================
    // CATALOGO Modelos de UI y Entidades de Base de Datos
    // =========================

    private val _catalogo = MutableStateFlow<List<CatalogoProducto>>(emptyList())
    val catalogo: StateFlow<List<CatalogoProducto>> = _catalogo.asStateFlow()

    // Exponemos las entidades crudas para que NuevoProductoScreen no rompa
    private val _listaCatalogo = MutableStateFlow<List<CatalogoEntity>>(emptyList())
    val listaCatalogo: StateFlow<List<CatalogoEntity>> = _listaCatalogo.asStateFlow()

    // =========================
    // SUPERMERCADOS
    // =========================

    private val _supermercados = MutableStateFlow<List<Supermercado>>(emptyList())
    val supermercados: StateFlow<List<Supermercado>> = _supermercados.asStateFlow()

    init {

        /*
        CARGA INICIAL DEL CATÁLOGO EN ROOM

        Si la tabla está vacía, insertamos los productos base.
        Esto evita que al volver a iniciar sesión el catálogo quede vacío
        y las compras no puedan reconstruir correctamente sus productos.
        */
        inicializarCatalogoSiHaceFalta()

        /*
        CARGA INICIAL DE SUPERMERCADOS EN ROOM

        Si la tabla está vacía, insertamos los supermercados base.
        */
        inicializarSupermercadosSiHaceFalta()

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

                _catalogo.value = entidadesCatalogo.map { it.toModel() }

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
                    name.ifEmpty {
                        email.substringBefore("@")
                    }

                email to nombreFinal

            }.collectLatest { (email, nombreFinal) ->

                _userEmail.value = email

                _userName.value = nombreFinal
            }
        }

        /*
        OBSERVAR SUPERMERCADOS

        Observamos los supermercados desde la BD en tiempo real.
        */
        viewModelScope.launch {
            repository.supermercados.collectLatest { entidades ->
                _supermercados.value = entidades.map { 
                    Supermercado(id = it.id, nombre = it.nombre) 
                }
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
                    repository.insertarProductoCatalogo(producto.toEntity())
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

        /*
        POST a FakeStore lanzado en viewModelScope para que sobreviva
        a la navegación. rememberCoroutineScope() se cancela cuando
        Compose destruye la pantalla al hacer popBackStack(), lo que
        mataba el POST antes de que OkHttp abriera la conexión.
        viewModelScope vive mientras el ViewModel exista.
        */
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("HomeViewModel", "Iniciando POST para compra de ${compra.supermercado}")
            try {
                val total = compra.productos.sumOf { it.subtotal() }
                val respuesta = ofertasRepository.registrarCompra(
                    supermercado = compra.supermercado,
                    total        = total,
                    fecha        = compra.fecha,
                    hora         = compra.hora
                )
                Log.i("HomeViewModel",
                    "POST exitoso — id servidor: ${respuesta.id}, " +
                    "supermercado: ${respuesta.title}, " +
                            $$"total: $$${respuesta.price}"
                )
            } catch (e: Exception) {
                Log.w("HomeViewModel",
                    "POST fallido (compra guardada en Room igualmente): ${e.message}")
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
            repository.insertarProductoCatalogo(
                CatalogoProducto(
                    id = 0,
                    codigo = codigo.trim(),
                    nombre = nombre.trim(),
                    descripcion = descripcion.trim(),
                    precio = precio
                ).toEntity()
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
            repository.actualizarProducto(
                CatalogoProducto(
                    id = id,
                    codigo = nuevoCodigo.trim(),
                    nombre = nuevoNombre.trim(),
                    descripcion = nuevaDescripcion.trim(),
                    precio = nuevoPrecio
                ).toEntity()
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

    // =========================
    // SUPERMERCADOS
    // =========================

    private fun inicializarSupermercadosSiHaceFalta() {
        viewModelScope.launch(Dispatchers.IO) {
            val cantidad = repository.contarSupermercados()
            
            if (cantidad == 0) {
                supermercadosPrecargados.forEach { s ->
                    repository.insertarSupermercado(
                        SupermercadoEntity(nombre = s.nombre)
                    )
                }
            }
        }
    }

    fun agregarSupermercado(nombre: String): Boolean {
        val currentSupermercados = _supermercados.value
        
        val yaExiste = currentSupermercados.any { 
            it.nombre.trim().equals(nombre.trim(), ignoreCase = true) 
        }
        
        if (yaExiste) return false
        
        viewModelScope.launch {
            val nuevaEntidad = SupermercadoEntity(nombre = nombre.trim())
            repository.insertarSupermercado(nuevaEntidad)
        }
        
        return true
    }

    fun eliminarSupermercado(supermercado: Supermercado) {
        viewModelScope.launch {
            repository.eliminarSupermercado(SupermercadoEntity(id = supermercado.id, nombre = supermercado.nombre))
        }
    }

    fun actualizarSupermercado(id: Int, nuevoNombre: String) {
        viewModelScope.launch {
            repository.actualizarSupermercado(
                SupermercadoEntity(id = id, nombre = nuevoNombre.trim())
            )
        }
    }

    fun seleccionarCompra(clickedCompra: com.undef.superahorroniccolinibenitez.model.Compra) {}
}
