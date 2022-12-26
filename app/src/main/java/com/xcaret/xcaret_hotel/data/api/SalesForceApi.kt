package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.entity.SalesForceGetProfileEntity
import com.xcaret.xcaret_hotel.data.entity.SalesForcePaxProfileEntity
import com.xcaret.xcaret_hotel.view.config.Session
import io.reactivex.Single
import retrofit2.http.*

interface SalesForceApi {
    @GET("getpaxprofilerq")
    fun getpaxprofilerq(@Header("Authorization") idToken: String = Session.getToken(HotelXcaretApp.mContext) ?: "", @Query("Email") email: String): Single<SalesForceGetProfileEntity>

    @POST("paxprofilerq")
    fun paxprofilerq(@Header("Authorization") idToken: String = Session.getToken(HotelXcaretApp.mContext) ?: "", @Body params: JsonObject): Single<SalesForcePaxProfileEntity>

    @GET("getpaxprofilerq")
    fun getpaxprofilerqExternalId(@Header("Authorization") idToken: String = Session.getToken(HotelXcaretApp.mContext) ?: "", @Query("ExternalId") externalId: String): Single<SalesForceGetProfileEntity>
}