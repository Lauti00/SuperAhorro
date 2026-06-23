package com.undef.superahorroniccolinibenitez.data.network.ofertas

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/*
Servicio de Retrofit para consultar ofertas.

@GET  → obtiene precios de referencia desde /products
@POST → envía el registro de una compra a /products

Ambas funciones son suspend porque se ejecutan con corrutinas.
*/
interface OfertasApiService {

    @GET("products")
    suspend fun obtenerOfertas(
        @Query("limit") limit: Int = 6
    ): List<ProductoOfertaDto>

    /*
    Registra una compra en el servidor.

    FakeStore acepta POST en /products y devuelve el objeto
    creado con un id asignado. La persistencia real no ocurre
    en el servidor (FakeStore es un mock), pero la interacción
    HTTP es real y cumple el requisito de RNF de POST.
    */
    @POST("products")
    suspend fun registrarCompra(
        @Body compra: CompraPostDto
    ): CompraPostResponseDto
}