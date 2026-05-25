package com.undef.superahorroniccolinibenitez.data.datastore.local.mappers

import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CatalogoEntity
import com.undef.superahorroniccolinibenitez.model.CatalogoProducto

/*
CONVIERTE ENTITY -> MODEL
(Room hacia UI)
*/
fun CatalogoEntity.toModel(): CatalogoProducto {
    return CatalogoProducto(
        id = id,
        codigo = codigo,
        nombre = nombre,
        descripcion = descripcion,
        precio = precio
    )
}

/*
CONVIERTE MODEL -> ENTITY
(UI hacia Room)
*/
fun CatalogoProducto.toEntity(): CatalogoEntity {
    return CatalogoEntity(
        id = id,
        codigo = codigo,
        nombre = nombre,
        descripcion = descripcion,
        precio = precio
    )
}