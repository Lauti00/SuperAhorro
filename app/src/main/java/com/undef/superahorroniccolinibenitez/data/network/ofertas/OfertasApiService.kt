package com.undef.superahorroniccolinibenitez.data.network.ofertas

import retrofit2.http.GET
import retrofit2.http.Query

/*
Servicio de Retrofit para consultar ofertas.

@GET indica el endpoint que se consume.
La función es suspend porque se ejecuta con corrutinas.
*/
interface OfertasApiService {

    @GET("products")
    suspend fun obtenerOfertas(
        @Query("limit") limit: Int = 6
    ): List<ProductoOfertaDto>
}