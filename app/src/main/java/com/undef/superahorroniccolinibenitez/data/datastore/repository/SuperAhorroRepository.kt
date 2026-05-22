package com.undef.superahorroniccolinibenitez.data.datastore.repository

import com.undef.superahorroniccolinibenitez.data.datastore.local.dao.SuperAhorroDao
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CatalogoEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.DetalleCompraEntity
import kotlinx.coroutines.flow.Flow

class SuperAhorroRepository(private val dao: SuperAhorroDao) {

    // Compras
    val todasLasCompras: Flow<List<CompraEntity>> = dao.obtenerTodasLasCompras()

    suspend fun insertarCompra(compra: CompraEntity) {
        dao.insertarCompra(compra)
    }

    // Detalles
    fun obtenerDetallesPorCompra(compraId: Int): Flow<List<DetalleCompraEntity>> {
        return dao.obtenerDetallesPorCompra(compraId)
    }

    suspend fun insertarDetalle(detalle: DetalleCompraEntity) {
        dao.insertarDetalle(detalle)
    }

    // Catálogo
    val catalogo: Flow<List<CatalogoEntity>> = dao.obtenerCatalogo()

    suspend fun insertarProductoCatalogo(producto: CatalogoEntity) {
        dao.insertarProductoCatalogo(producto)
    }
}