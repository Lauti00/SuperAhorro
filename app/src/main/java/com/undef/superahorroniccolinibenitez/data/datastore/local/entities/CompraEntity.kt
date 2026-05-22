package com.undef.superahorroniccolinibenitez.data.datastore.local.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compras")
data class CompraEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val supermercado: String,
    val fecha: String,
    val hora: String,
    val imagenUri: String? = null
)