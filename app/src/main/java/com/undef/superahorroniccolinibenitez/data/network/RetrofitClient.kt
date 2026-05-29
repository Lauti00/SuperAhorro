package com.undef.superahorroniccolinibenitez.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
Cliente centralizado de Retrofit.

Retrofit se encarga de hacer las llamadas HTTP
y Gson convierte el JSON recibido en objetos Kotlin.
*/
object RetrofitClient {

    private const val BASE_URL =
        "https://fakestoreapi.com/"

    val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}