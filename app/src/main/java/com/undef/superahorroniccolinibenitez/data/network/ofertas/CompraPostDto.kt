package com.undef.superahorroniccolinibenitez.data.network.ofertas

/*
DTO que representa el body del POST a FakeStore /products.

FakeStore espera los campos de un producto estándar.
Nosotros los usamos para representar una compra:
  - title     → nombre del supermercado
  - price     → total de la compra
  - category  → siempre "compra-superahorro" para identificarlo
  - description → fecha y hora concatenadas

La API devuelve el mismo objeto con un id asignado (ficticio,
no persiste en FakeStore, pero la respuesta es válida y 
demuestra la interacción POST real con el servidor).
*/
data class CompraPostDto(
    val title: String,
    val price: Double,
    val category: String,
    val description: String
)

/*
DTO de la respuesta que FakeStore devuelve tras el POST.
Devuelve el mismo objeto con el id generado por el servidor.
*/
data class CompraPostResponseDto(
    val id: Int,
    val title: String,
    val price: Double,
    val category: String,
    val description: String
)
