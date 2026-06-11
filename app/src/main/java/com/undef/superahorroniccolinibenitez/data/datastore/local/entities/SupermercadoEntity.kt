package com.undef.superahorroniccolinibenitez.data.datastore.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "supermercados")
data class SupermercadoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String
)
