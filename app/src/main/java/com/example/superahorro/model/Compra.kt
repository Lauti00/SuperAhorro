package com.example.superahorro.model

data class Compra(
    val id: Int,
    val supermercado: String,
    val fecha: String,
    val productos: List<Producto> = emptyList(),
    val imagenUri: String? = null
) {
    fun total(): Double = productos.sumOf { it.subtotal() }
}
