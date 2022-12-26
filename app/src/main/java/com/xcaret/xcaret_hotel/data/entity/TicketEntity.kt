package com.xcaret.xcaret_hotel.data.entity

data class TicketEntity(
    val Ventas: List<TicketSale>? = null
)

data class TicketSale(
    val idVenta: String? = null,
    val dsClaveVenta: String? = null,
    val feVenta: String? = null,
    val feAlta: String? = null,
    val dsCanalVenta: String = "",
    val productos: List<TicketProduct>? = null
)

data class TicketProduct(
    val cnPickup: Boolean? = null,
    val dsClave: String? = null,
    val dsProducto: String? = null,
    val dsClaveUnidadNegocio: String? = null,
    val familyProducto: String? = null
)