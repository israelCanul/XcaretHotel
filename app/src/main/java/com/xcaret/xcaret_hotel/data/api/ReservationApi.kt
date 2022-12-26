package com.xcaret.xcaret_hotel.data.api

import com.xcaret.xcaret_hotel.data.entity.ReservationDetailResponse
import com.xcaret.xcaret_hotel.data.entity.ReservationsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ReservationApi {

    @GET("Reservations/by-referencecode-email/{language}/{currency}/{reservationNumber}/{email}/{details}/{balance}")
    fun byReferenceEmail(
        @Path(value = "language", encoded = true) language: String,
        @Path(value = "currency", encoded = true) currency: String,
        @Path(value = "reservationNumber", encoded = true) reservationNumber: String,
        @Path(value = "email", encoded = true) email: String,
        @Path(value = "details", encoded = true) details: Boolean,
        @Path(value = "balance", encoded = true) balance: Boolean
    ): Single<ReservationDetailResponse>

    @GET("Reservations/retrieve-by-email/{email}")
    fun retrieveByEmail(
        @Path(value = "email", encoded = true) email: String,
    ): Single<ReservationsResponse>
}