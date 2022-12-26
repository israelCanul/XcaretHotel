package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.data.entity.CognitoValidUserEntity
import io.reactivex.Single
import retrofit2.http.*

interface CognitoApi {

    @POST("validateuser")
    fun validateuser(@Body params: JsonObject): Single<CognitoValidUserEntity?>

    @GET("getuser")
    fun getUser(@Query("Username") email: String): Single<CognitoValidUserEntity?>

}