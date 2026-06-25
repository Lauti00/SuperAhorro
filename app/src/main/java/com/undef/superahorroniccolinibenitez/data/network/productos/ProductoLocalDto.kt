package com.undef.superahorroniccolinibenitez.data.network.productos

/*
DTO que representa un producto en la API local.
Los tres campos coinciden exactamente con lo que
el servidor espera recibir y devuelve.
*/
data class ProductoLocalDto(
    val ean: String,
    val nombre: String,
    val descripcion: String
)
