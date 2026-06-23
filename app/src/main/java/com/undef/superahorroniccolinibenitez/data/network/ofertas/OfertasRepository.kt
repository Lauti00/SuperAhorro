package com.undef.superahorroniccolinibenitez.data.network.ofertas

import com.undef.superahorroniccolinibenitez.data.network.RetrofitClient
import com.undef.superahorroniccolinibenitez.model.OfertaSupermercado

/*
Repository de ofertas.

Actúa como fuente de datos para los precios de referencia.
El ViewModel no conoce Retrofit directamente:
le pide los datos al Repository.
*/
class OfertasRepository {

    private val apiService: OfertasApiService =
        RetrofitClient.retrofit.create(OfertasApiService::class.java)

    /*
    Traduce las categorías que devuelve FakeStore al español.
    FakeStore solo tiene 4 categorías posibles.
    */
    private fun traducirCategoria(category: String?): String {
        return when (category?.trim()?.lowercase()) {
            "electronics"      -> "Electrónica"
            "jewelery"         -> "Joyería"
            "men's clothing"   -> "Ropa masculina"
            "women's clothing" -> "Ropa femenina"
            else               -> category?.replaceFirstChar { it.uppercase() } ?: "General"
        }
    }

    /*
    Recorta el título si es demasiado largo para que no desborde la tarjeta.
    */
    private fun recortarTitulo(title: String?, maxLength: Int = 45): String {
        if (title == null) return "Sin nombre"
        return if (title.length <= maxLength) title
        else title.take(maxLength).trimEnd() + "…"
    }

    /*
    Consulta la API y mapea la respuesta directamente al modelo de la app.
    Cada campo viene de lo que FakeStore realmente devuelve.
    */
    suspend fun obtenerOfertas(): List<OfertaSupermercado> {
        val response = apiService.obtenerOfertas(limit = 6)

        return response.mapNotNull { dto ->
            val precio = dto.price ?: return@mapNotNull null

            OfertaSupermercado(
                producto    = recortarTitulo(dto.title),
                descripcion = traducirCategoria(dto.category),
                precio      = precio,
                url         = "https://fakestoreapi.com/products/${dto.id ?: ""}"
            )
        }
    }

    /*
    Registra una compra en el servidor remoto via POST.

    Recibe los datos de la compra ya guardada en Room y los
    envía a FakeStore /products. Devuelve el id que el servidor
    asignó al objeto creado (ficticio en FakeStore, pero la
    respuesta HTTP 200 con body confirma que el POST fue exitoso).

    Se llama desde HomeViewModel después del INSERT en Room,
    de forma fire-and-forget: si falla la red la compra ya
    quedó guardada localmente y no se pierde nada.
    */
    suspend fun registrarCompra(
        supermercado: String,
        total: Double,
        fecha: String,
        hora: String
    ): CompraPostResponseDto {

        val body = CompraPostDto(
            title       = supermercado,
            price       = total,
            category    = "compra-superahorro",
            description = "$fecha $hora"
        )

        return apiService.registrarCompra(body)
    }
}