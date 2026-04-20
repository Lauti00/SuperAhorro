package com.example.superahorro.model

data class Compra(
    val id: Int,
    val supermercado: String,
    val fecha: String,
    val total: Double
)

val listaComprasMock = listOf(
    Compra(1, "Mercadona", "15/10/2023", 45.50),
    Compra(2, "Carrefour", "12/10/2023", 120.00),
    Compra(3, "Lidl", "10/10/2023", 32.20),
    Compra(4, "Dia", "05/10/2023", 15.00),
    Compra(5, "Alcampo", "01/10/2023", 89.99)
)
