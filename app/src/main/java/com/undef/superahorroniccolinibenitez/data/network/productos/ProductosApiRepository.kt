package com.undef.superahorroniccolinibenitez.data.network.productos

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
Repository para la API local de productos.

Usa una instancia de Retrofit separada de FakeStore
porque la base URL es diferente (servidor local).

10.0.2.2 es la IP especial que el emulador Android
usa para referirse al localhost de la máquina host.
*/
class ProductosApiRepository {

    private val apiService: ProductosApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.1.27:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductosApiService::class.java)
    }

    /*
    Resultado tipado del GET.
    Así el ViewModel no necesita conocer los códigos HTTP.
    */
    sealed class BusquedaResult {
        data class Encontrado(val producto: ProductoLocalDto) : BusquedaResult()
        object NoEncontrado : BusquedaResult()
        data class Error(val mensaje: String) : BusquedaResult()
    }

    /*
    Resultado tipado del POST.
    */
    sealed class GuardadoResult {
        data class Guardado(val producto: ProductoLocalDto) : GuardadoResult()
        object YaExiste : GuardadoResult()
        data class Error(val mensaje: String) : GuardadoResult()
    }

    /*
    Busca un producto por EAN en la API local.
    */
    suspend fun buscarPorEan(ean: String): BusquedaResult {
        return try {
            val response = apiService.buscarPorEan(ean)
            when {
                response.isSuccessful -> {
                    val producto = response.body()
                    if (producto != null) BusquedaResult.Encontrado(producto)
                    else BusquedaResult.Error("Respuesta vacía del servidor")
                }
                response.code() == 404 -> BusquedaResult.NoEncontrado
                else -> BusquedaResult.Error("Error del servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            BusquedaResult.Error("No se pudo conectar con la API: ${e.message}")
        }
    }

    /*
    Guarda un producto nuevo en la API local.
    Se llama solo cuando el usuario completó los campos a mano
    porque la API no tenía el producto.
    */
    suspend fun guardarProducto(
        ean: String,
        nombre: String,
        descripcion: String
    ): GuardadoResult {
        return try {
            val response = apiService.guardarProducto(
                ProductoLocalDto(
                    ean         = ean.trim(),
                    nombre      = nombre.trim(),
                    descripcion = descripcion.trim()
                )
            )
            when {
                response.isSuccessful -> {
                    val producto = response.body()
                    if (producto != null) GuardadoResult.Guardado(producto)
                    else GuardadoResult.Error("Respuesta vacía del servidor")
                }
                response.code() == 409 -> GuardadoResult.YaExiste
                else -> GuardadoResult.Error("Error del servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            GuardadoResult.Error("No se pudo conectar con la API: ${e.message}")
        }
    }
}
