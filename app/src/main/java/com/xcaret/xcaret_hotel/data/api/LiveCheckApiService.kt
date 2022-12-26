package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.entity.LiveCheckResponseEntity
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Settings
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class LiveCheckApiService() {
    private val api = Retrofit.Builder()
        .baseUrl(Settings.getParam(Constants.synxis_availabity, HotelXcaretApp.mContext)+"/")
        //.baseUrl("https://xapis-preprod.xcaret.com/hotels/" )
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(LiveCheckApi::class.java)

    fun livecheck(request: JsonObject): Single<LiveCheckResponseEntity> = api.livecheck(request)
}