package com.undef.superahorroniccolinibenitez.model

data class Producto(
    val producto: CatalogoProducto,
    val cantidad: Int
) {
    fun subtotal(): Double = producto.precio * cantidad
}