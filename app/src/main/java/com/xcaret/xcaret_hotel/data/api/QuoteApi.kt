package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.data.entity.QuotesResponseEntity
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface QuoteApi {
    @POST("Avail/quotes")
    fun quotes(@Body params: JsonObject): Single<QuotesResponseEntity>
}