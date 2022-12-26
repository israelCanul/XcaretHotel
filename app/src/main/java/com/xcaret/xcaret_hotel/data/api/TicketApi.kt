package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonArray
import com.xcaret.xcaret_hotel.data.entity.TicketEntity
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface TicketApi {

    @POST("xcaretapp/mobapp/ventas")
    fun ventas(@Body request: JsonArray, @Header("Authorization") basic: String): Single<TicketEntity?>
}