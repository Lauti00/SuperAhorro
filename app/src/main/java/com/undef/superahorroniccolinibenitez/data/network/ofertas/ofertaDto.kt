package com.undef.superahorroniccolinibenitez.data.network.ofertas

/*
DTO que representa cada producto recibido desde la API.

Usamos tipos nullable para evitar que la app falle
si algún campo llega vacío o no viene en el JSON.
*/
data class ProductoOfertaDto(
    val id: Int?,
    val title: String?,
    val price: Double?,
    val category: String?,
    val description: String?,
    val image: String?
)