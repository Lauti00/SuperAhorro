package com.undef.superahorroniccolinibenitez.model

/*
Modelo que usa la UI para mostrar ofertas.

No usamos directamente el DTO porque el DTO representa
la respuesta cruda de internet, mientras que este modelo
representa lo que necesita mostrar la app.
*/
data class OfertaSupermercado(
    val producto: String,
    val descripcion: String,
    val precio: Double,
    val url: String
)