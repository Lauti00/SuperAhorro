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
    Consulta la API y transforma la respuesta en ofertas realistas para la app.

    La llamada de red sigue existiendo y permite demostrar Networking.
    Luego adaptamos esos datos a promociones de supermercado en español
    para que la experiencia sea coherente con SuperAhorro.
    */
    suspend fun obtenerOfertas(): List<OfertaSupermercado> {

        val response =
            apiService.obtenerOfertas(limit = 6)

        val ofertasLocales =
            listOf(
                OfertaSupermercado(
                    supermercado = "Coto",
                    producto = "Lácteos seleccionados",
                    descripcion = "Leche, yogures y quesos en promoción",
                    descuento = "20%",
                    precio = 1499.99,
                    url = "https://www.coto.com.ar/"
                ),
                OfertaSupermercado(
                    supermercado = "Carrefour",
                    producto = "Productos de limpieza",
                    descripcion = "Detergentes, lavandina y desinfectantes",
                    descuento = "2x1",
                    precio = 2299.99,
                    url = "https://www.carrefour.com.ar/"
                ),
                OfertaSupermercado(
                    supermercado = "Día",
                    producto = "Bebidas sin alcohol",
                    descripcion = "Gaseosas, aguas saborizadas y jugos",
                    descuento = "15%",
                    precio = 1099.99,
                    url = "https://diaonline.supermercadosdia.com.ar/"
                ),
                OfertaSupermercado(
                    supermercado = "ChangoMás",
                    producto = "Carnes y congelados",
                    descripcion = "Promoción semanal en cortes seleccionados",
                    descuento = "10%",
                    precio = 5499.99,
                    url = "https://www.masonline.com.ar/"
                ),
                OfertaSupermercado(
                    supermercado = "Disco",
                    producto = "Almacén básico",
                    descripcion = "Arroz, fideos, aceite y conservas",
                    descuento = "25%",
                    precio = 1899.99,
                    url = "https://www.disco.com.ar/"
                ),
                OfertaSupermercado(
                    supermercado = "Jumbo",
                    producto = "Frutas y verduras",
                    descripcion = "Productos frescos de temporada",
                    descuento = "30%",
                    precio = 999.99,
                    url = "https://www.jumbo.com.ar/"
                )
            )

        /*
        Usamos la cantidad recibida desde la API para limitar las ofertas.
        Si la API responde correctamente, mostramos promociones.
        */
        return ofertasLocales.take(response.size.coerceAtLeast(1))
    }
}