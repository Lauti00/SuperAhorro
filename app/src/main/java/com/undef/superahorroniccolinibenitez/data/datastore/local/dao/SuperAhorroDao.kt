package com.undef.superahorroniccolinibenitez.data.datastore.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CatalogoEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.DetalleCompraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SuperAhorroDao {

    // =========================
    // COMPRAS
    // =========================

    /*
    Ahora devuelve Long.
    Room retorna automáticamente el ID generado.
    */
    @Insert
    suspend fun insertarCompra(compra: CompraEntity): Long

    @Query("SELECT * FROM compras ORDER BY id DESC")
    fun obtenerTodasLasCompras(): Flow<List<CompraEntity>>

    // =========================
    // DETALLES
    // =========================

    @Insert
    suspend fun insertarDetalle(detalle: DetalleCompraEntity)

    @Query("SELECT * FROM detalles_compra WHERE idCompra = :compraId")
    fun obtenerDetallesPorCompra(compraId: Int): Flow<List<DetalleCompraEntity>>

    // =========================
    // CATÁLOGO
    // =========================

    @Insert
    suspend fun insertarProductoCatalogo(producto: CatalogoEntity)

    @Update
    suspend fun actualizarProducto(producto: CatalogoEntity)

    @Delete
    suspend fun eliminarProducto(producto: CatalogoEntity)

    @Query("SELECT * FROM catalogo_productos")
    fun obtenerCatalogo(): Flow<List<CatalogoEntity>>
}