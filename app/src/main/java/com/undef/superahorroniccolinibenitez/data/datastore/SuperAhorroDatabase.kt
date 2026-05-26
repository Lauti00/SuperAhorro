package com.undef.superahorroniccolinibenitez.data.datastore

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    companion object {
        @Volatile
        private var INSTANCE: SuperAhorroDatabase? = null

        //  Controla que toda la aplicación use un único archivo físico persistente (.db)
        fun getDatabase(context: Context): SuperAhorroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SuperAhorroDatabase::class.java,
                    "super_ahorro_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}