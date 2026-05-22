package com.undef.superahorroniccolinibenitez.data.datastore.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CatalogoEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.DetalleCompraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SuperAhorroDao {

    // --- ÓRDENES PARA LAS COMPRAS ---
    @Insert
    suspend fun insertarCompra(compra: CompraEntity) // <-- Le sacamos el ": Long" de acá

    @Query("SELECT * FROM compras ORDER BY id DESC")
    fun obtenerTodasLasCompras(): Flow<List<CompraEntity>>

    // --- ÓRDENES PARA LOS DETALLES ---
    @Insert
    suspend fun insertarDetalle(detalle: DetalleCompraEntity)

    @Query("SELECT * FROM detalles_compra WHERE idCompra = :compraId")
    fun obtenerDetallesPorCompra(compraId: Int): Flow<List<DetalleCompraEntity>>

    // --- ÓRDENES PARA EL CATÁLOGO ---
    @Insert
    suspend fun insertarProductoCatalogo(producto: CatalogoEntity)

    @Query("SELECT * FROM catalogo_productos")
    fun obtenerCatalogo(): Flow<List<CatalogoEntity>>
}