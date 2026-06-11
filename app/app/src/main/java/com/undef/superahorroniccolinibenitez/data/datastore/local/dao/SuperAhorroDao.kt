package com.undef.superahorroniccolinibenitez.data.datastore.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CatalogoEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.DetalleCompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.SupermercadoEntity
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

    @Update
    suspend fun actualizarCompra(compra: CompraEntity)

    @Query("DELETE FROM compras WHERE id = :compraId")
    suspend fun eliminarCompraPorId(compraId: Int)

    // =========================
    // DETALLES
    // =========================

    @Insert
    suspend fun insertarDetalle(detalle: DetalleCompraEntity)

    @Query("SELECT * FROM detalles_compra WHERE idCompra = :compraId")
    fun obtenerDetallesPorCompra(compraId: Int): Flow<List<DetalleCompraEntity>>

    /*
    Ahora también observamos todos los detalles como Flow.
    Esto permite que la UI se actualice automáticamente al volver a iniciar sesión
    o cuando se agregan productos a una compra.
    */
    @Query("SELECT * FROM detalles_compra")
    fun obtenerTodosLosDetalles(): Flow<List<DetalleCompraEntity>>

    @Query("SELECT * FROM detalles_compra")
    suspend fun obtenerTodosLosDetallesLista(): List<DetalleCompraEntity>

    @Query("DELETE FROM detalles_compra WHERE idCompra = :compraId")
    suspend fun eliminarDetallesPorCompraId(compraId: Int)

    @Query("DELETE FROM detalles_compra WHERE idCompra = :compraId AND idProducto = :productoId")
    suspend fun eliminarDetalleEspecifico(compraId: Int, productoId: Int)

    // =========================
    // CATÁLOGO
    // =========================

    /*
    Usamos IGNORE para poder cargar productos iniciales sin duplicarlos.
    */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarProductoCatalogo(producto: CatalogoEntity)

    @Update
    suspend fun actualizarProducto(producto: CatalogoEntity)

    @Delete
    suspend fun eliminarProducto(producto: CatalogoEntity)

    @Query("SELECT * FROM catalogo_productos ORDER BY id ASC")
    fun obtenerCatalogo(): Flow<List<CatalogoEntity>>

    @Query("SELECT COUNT(*) FROM catalogo_productos")
    suspend fun contarProductosCatalogo(): Int

    // =========================
    // SUPERMERCADOS
    // =========================

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarSupermercado(supermercado: SupermercadoEntity): Long

    @Query("SELECT * FROM supermercados ORDER BY nombre ASC")
    fun obtenerTodosSuperMercados(): Flow<List<SupermercadoEntity>>

    @Update
    suspend fun actualizarSupermercado(supermercado: SupermercadoEntity)

    @Delete
    suspend fun eliminarSupermercado(supermercado: SupermercadoEntity)

    @Query("SELECT COUNT(*) FROM supermercados")
    suspend fun contarSupermercados(): Int
}