package com.undef.superahorroniccolinibenitez.data.datastore.repository

import com.undef.superahorroniccolinibenitez.data.datastore.local.dao.SuperAhorroDao
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CatalogoEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.DetalleCompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.SupermercadoEntity
import com.undef.superahorroniccolinibenitez.data.network.productos.ProductosApiRepository
import kotlinx.coroutines.flow.Flow

class SuperAhorroRepository(
    private val dao: SuperAhorroDao,
    private val productosApiRepository: ProductosApiRepository) {

    // =========================
    // COMPRAS
    // =========================

    val todasLasCompras: Flow<List<CompraEntity>> =
        dao.obtenerTodasLasCompras()

    /*
    Ahora devuelve el ID generado por Room
    */
    suspend fun insertarCompra(compra: CompraEntity): Long {
        return dao.insertarCompra(compra)
    }

    suspend fun actualizarCompra(compra: CompraEntity) {
        dao.actualizarCompra(compra)
    }

    // =========================
    // DETALLES
    // =========================

    val todosLosDetalles: Flow<List<DetalleCompraEntity>> =
        dao.obtenerTodosLosDetalles()

    suspend fun insertarDetalle(detalle: DetalleCompraEntity) {
        dao.insertarDetalle(detalle)
    }

    suspend fun eliminarDetallesPorCompraId(compraId: Int) {
        dao.eliminarDetallesPorCompraId(compraId)
    }

    // =========================
    // CATALOGO
    // =========================

    val catalogo: Flow<List<CatalogoEntity>> =
        dao.obtenerCatalogo()

    suspend fun insertarProductoCatalogo(producto: CatalogoEntity) {
        dao.insertarProductoCatalogo(producto)
    }

    suspend fun contarProductosCatalogo(): Int {
        return dao.contarProductosCatalogo()
    }

    /*
    ACTUALIZAR PRODUCTO
    */
    suspend fun actualizarProducto(producto: CatalogoEntity) {
        dao.actualizarProducto(producto)
    }

    /*
    ELIMINAR PRODUCTO
    */
    suspend fun eliminarProducto(producto: CatalogoEntity) {
        dao.eliminarProducto(producto)
    }

    // =========================
    // SUPERMERCADOS
    // =========================

    val supermercados: Flow<List<SupermercadoEntity>> =
        dao.obtenerTodosSuperMercados()

    suspend fun insertarSupermercado(supermercado: SupermercadoEntity): Long {
        return dao.insertarSupermercado(supermercado)
    }

    suspend fun actualizarSupermercado(supermercado: SupermercadoEntity) {
        dao.actualizarSupermercado(supermercado)
    }

    suspend fun eliminarSupermercado(supermercado: SupermercadoEntity) {
        dao.eliminarSupermercado(supermercado)
    }

    suspend fun contarSupermercados(): Int {
        return dao.contarSupermercados()
    }

    /*
    Busca un producto por EAN:

    1. Busca primero en Room (caché local)
       → Si lo encuentra, lo devuelve sin tocar la red

    2. Si no está en Room, consulta la API
       → Si la API lo tiene, lo guarda en Room y lo devuelve
       → Si la API tampoco lo tiene, devuelve null

    La UI siempre recibe un CatalogoEntity — no sabe ni le importa
    si el dato vino de Room o de la API.
    */
    suspend fun buscarProductoPorEan(ean: String): CatalogoEntity? {
        // Paso 1: buscar en Room
        val enRoom = dao.buscarPorCodigo(ean)
        if (enRoom != null) return enRoom

        // Paso 2: no estaba en Room → consultar la API
        val enApi = productosApiRepository.buscarPorEan(ean)
        if (enApi is ProductosApiRepository.BusquedaResult.Encontrado) {

            // Guardar en Room lo que trajo la API
            // precio = 0.0 porque el precio lo completa el usuario
            dao.insertarProductoCatalogo(
                CatalogoEntity(
                    codigo      = ean,
                    nombre      = enApi.producto.nombre,
                    descripcion = enApi.producto.descripcion,
                    precio      = 0.0
                )
            )

            // Devolver desde Room para mantener la consistencia
            return dao.buscarPorCodigo(ean)
        }

        // Paso 3: ni Room ni API lo tienen
        return null
    }
}