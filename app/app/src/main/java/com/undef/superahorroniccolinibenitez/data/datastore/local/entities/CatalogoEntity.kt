package com.undef.superahorroniccolinibenitez.data.datastore.local.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalogo_productos")
data class CatalogoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double
)