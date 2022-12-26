package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface XwitchAPI {

    @POST("api/lang/translateApp")
    suspend fun checkTranslation(@Body params: JsonObject): Response<Void>


//    @POST("api/path/getPath")
//    suspend fun getPath(@Body request:JsonObject):Response<PathResponseEntity?>

}