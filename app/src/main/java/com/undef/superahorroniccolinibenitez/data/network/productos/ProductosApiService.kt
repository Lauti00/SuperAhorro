package com.undef.superahorroniccolinibenitez.data.network.productos

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/*
Interfaz Retrofit para la API local de productos.

Usamos Response<T> en el GET para poder distinguir
entre 200 (encontrado) y 404 (no encontrado) sin
que Retrofit lance una excepción en el 404.
*/
interface ProductosApiService {

    /*
    Busca un producto por código EAN.
    Devuelve 200 + producto si existe.
    Devuelve 404 si no existe.
    */
    @GET("productos/{ean}")
    suspend fun buscarPorEan(
        @Path("ean") ean: String
    ): Response<ProductoLocalDto>

    /*
    Guarda un producto nuevo en la API.
    Devuelve 201 + producto guardado.
    Devuelve 409 si el EAN ya existe.
    */
    @POST("productos")
    suspend fun guardarProducto(
        @Body producto: ProductoLocalDto
    ): Response<ProductoLocalDto>

    /*
    Actualiza nombre y descripcion de un producto existente.
    El precio no se manda — vive solo en Room.
    */
    @PUT("productos/{ean}")
    suspend fun actualizarProducto(
        @Path("ean") ean: String,
        @Body producto: ProductoLocalDto
    ): Response<ProductoLocalDto>
}
