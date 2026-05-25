package com.undef.superahorroniccolinibenitez.data.datastore

import androidx.room.Database
import androidx.room.RoomDatabase
import com.undef.superahorroniccolinibenitez.data.datastore.local.dao.SuperAhorroDao
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CatalogoEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.DetalleCompraEntity

@Database(
    entities = [
        CompraEntity::class,
        DetalleCompraEntity::class,
        CatalogoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SuperAhorroDatabase : RoomDatabase() {

    abstract fun superAhorroDao(): SuperAhorroDao
}