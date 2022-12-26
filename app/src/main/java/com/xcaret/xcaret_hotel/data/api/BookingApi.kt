package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.data.entity.BookingEntity
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface BookingApi {

    @POST("api/Booking/DummyBooking")
    fun dummyBooking(@Body request: JsonObject): Single<BookingEntity>

    @POST("api/Booking/SetBooking")
    fun setBooking(@Body request: JsonObject): Single<BookingEntity>

    @POST("BookingService/Booking")
    fun booking(@Body request: JsonObject): Single<BookingEntity>
}