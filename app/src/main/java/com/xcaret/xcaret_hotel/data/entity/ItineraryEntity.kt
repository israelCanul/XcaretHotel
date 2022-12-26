package com.xcaret.xcaret_hotel.data.entity

data class ItineraryEntity(
    val Reservation: List<ItineraryReservationEntity>? = null,
    val Email: String? = ""
)

class ItineraryReservationEntity{}