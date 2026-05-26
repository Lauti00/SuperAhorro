package com.undef.superahorroniccolinibenitez.data.datastore.repository

import com.undef.superahorroniccolinibenitez.data.datastore.local.dao.SuperAhorroDao
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CatalogoEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.DetalleCompraEntity
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

    suspend fun eliminarCompraPorId(compraId: Int) {
        dao.eliminarCompraPorId(compraId)
    }

    // =========================
    // DETALLES
    // =========================

    fun obtenerDetallesPorCompra(compraId: Int): Flow<List<DetalleCompraEntity>> {
        return dao.obtenerDetallesPorCompra(compraId)
    }

    suspend fun obtenerTodosLosDetallesLista(): List<DetalleCompraEntity> {
        return dao.obtenerTodosLosDetallesLista()
    }

    suspend fun insertarDetalle(detalle: DetalleCompraEntity) {
        dao.insertarDetalle(detalle)
    }

    suspend fun eliminarDetallesPorCompraId(compraId: Int) {
        dao.eliminarDetallesPorCompraId(compraId)
    }

    suspend fun eliminarDetalleEspecifico(compraId: Int, productoId: Int) {
        dao.eliminarDetalleEspecifico(compraId, productoId)
    }

    // =========================
    // CATALOGO
    // =========================

    val catalogo: Flow<List<CatalogoEntity>> =
        dao.obtenerCatalogo()

    suspend fun insertarProductoCatalogo(producto: CatalogoEntity) {
        dao.insertarProductoCatalogo(producto)
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
}