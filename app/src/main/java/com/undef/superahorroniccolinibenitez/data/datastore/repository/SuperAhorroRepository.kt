package com.undef.superahorroniccolinibenitez.data.datastore.repository

import com.undef.superahorroniccolinibenitez.data.datastore.local.dao.SuperAhorroDao
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CatalogoEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.DetalleCompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.SupermercadoEntity
import kotlinx.coroutines.flow.Flow

class SuperAhorroRepository(private val dao: SuperAhorroDao) {

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
}