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

    // =========================
    // DETALLES
    // =========================

    fun obtenerDetallesPorCompra(compraId: Int): Flow<List<DetalleCompraEntity>> {
        return dao.obtenerDetallesPorCompra(compraId)
    }

    suspend fun insertarDetalle(detalle: DetalleCompraEntity) {
        dao.insertarDetalle(detalle)
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