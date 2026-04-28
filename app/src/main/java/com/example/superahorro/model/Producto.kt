package com.example.superahorro.model
data class Producto(
   val producto: CatalogoProducto ,
    val cantidad : Int
)
{
    fun subtotal(): Double = producto.precio * cantidad
}