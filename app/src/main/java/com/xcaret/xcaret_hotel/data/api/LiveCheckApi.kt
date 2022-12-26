package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.data.entity.LiveCheckResponseEntity
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface LiveCheckApi {
    @POST("Avail/livecheck")
    fun livecheck(@Body params: JsonObject): Single<LiveCheckResponseEntity>
}