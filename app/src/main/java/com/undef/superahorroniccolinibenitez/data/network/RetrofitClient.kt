package com.undef.superahorroniccolinibenitez.data.network

import okhttp3.Dns
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Inet4Address
import java.net.InetAddress
import java.util.concurrent.TimeUnit

/*
Cliente centralizado de Retrofit.

Retrofit se encarga de hacer las llamadas HTTP
y Gson convierte el JSON recibido en objetos Kotlin.
*/
object RetrofitClient {

    private const val BASE_URL =
        "https://fakestoreapi.com/"

    /*
    Cliente HTTP personalizado.

    Forzamos IPv4 para evitar errores de socket en algunos emuladores
    donde la resolución IPv6 puede fallar con EPERM.
    */
    private val okHttpClient =
        OkHttpClient.Builder()
            .dns(
                object : Dns {
                    override fun lookup(hostname: String): List<InetAddress> {
                        return Dns.SYSTEM.lookup(hostname)
                            .filterIsInstance<Inet4Address>()
                    }
                }
            )
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()

    val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}