package com.undef.superahorroniccolinibenitez.data.datastore.local.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detalles_compra")
data class DetalleCompraEntity(
    @PrimaryKey(autoGenerate = true)
    val idDetalle: Int = 0,
    val idCompra: Int,
    val idProducto: Int,
    val cantidad: Int,
    val precioUnitario: Double
)