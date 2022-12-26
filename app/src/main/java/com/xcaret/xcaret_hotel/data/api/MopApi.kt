package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.data.entity.PaymentBankEntity
import com.xcaret.xcaret_hotel.data.entity.PaymentBankInfoEntity
import io.reactivex.Single
import retrofit2.http.*

interface MopApi {
    @GET("api/Payment/banks/{currency}/{channel}/{checkIn}/{checkOut}/{amount}")
    fun banks(
        @HeaderMap header: Map<String, String>,
        @Path(value = "currency", encoded = true) currency: String,
        @Path(value = "channel", encoded = true) channel: Int,
        @Path(value = "checkIn", encoded = true) checkIn: String,
        @Path(value = "checkOut", encoded = true) checkOut: String,
        @Path(value = "amount", encoded = true) amount: Double,
        @Query("iso3Country") iso3Country: String,
        @Query("iso3Country") iso2Country: String,
    ): Single<PaymentBankEntity>

    @GET("api/Payment/BinInfo")
    fun bankInfo(@HeaderMap header: Map<String, String>): Single<PaymentBankInfoEntity>

    @POST("BinSettings/info")
    fun bankInfoV2(@Body body:JsonObject): Single<PaymentBankInfoEntity>

}