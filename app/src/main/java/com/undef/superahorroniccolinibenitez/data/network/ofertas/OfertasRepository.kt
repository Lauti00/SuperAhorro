package com.undef.superahorroniccolinibenitez.data.network.ofertas

import com.undef.superahorroniccolinibenitez.data.network.RetrofitClient
import com.undef.superahorroniccolinibenitez.model.OfertaSupermercado

/*
Repository de ofertas.

Actúa como fuente de datos para las promociones.
El ViewModel no conoce Retrofit directamente:
le pide los datos al Repository.
*/
class OfertasRepository {

    private val apiService: OfertasApiService =
        RetrofitClient.retrofit.create(OfertasApiService::class.java)

    /*
    Consulta la API y transforma los DTOs en modelos de UI.
    */
    suspend fun obtenerOfertas(): List<OfertaSupermercado> {

        val response =
            apiService.obtenerOfertas(limit = 6)

        val supermercados =
            listOf(
                "Coto",
                "Carrefour",
                "Día",
                "ChangoMás",
                "Disco",
                "Jumbo"
            )

        return response.mapIndexed { index, producto ->

            val supermercado =
                supermercados[index % supermercados.size]

            OfertaSupermercado(
                supermercado = supermercado,
                producto = producto.title ?: "Producto en oferta",
                descripcion = producto.category
                    ?.replaceFirstChar { it.uppercase() }
                    ?: "Promoción especial",
                descuento = "${10 + (index * 5)}%",
                precio = producto.price ?: 0.0
            )
        }
    }
}