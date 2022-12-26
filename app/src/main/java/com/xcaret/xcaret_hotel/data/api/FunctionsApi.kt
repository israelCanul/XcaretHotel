package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface FunctionsApi {

    @POST("createCustomToken")
    fun createCustomToken(@Body params: JsonObject): Single<JsonObject>
}