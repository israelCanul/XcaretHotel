package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Settings
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BookingApiService() {

    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(90, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()


    private val api = Retrofit.Builder()
        .baseUrl(Settings.getParam(Constants.sf_booking_engine, HotelXcaretApp.mContext))
        //.baseUrl("http://65838ec2aa8c.ngrok.io")
        //.baseUrl("http://api-book-engine-prueba.xperienciasxcaret.mx")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)
        .build()
        .create(BookingApi::class.java)

    fun dummyBooking(request: JsonObject) = api.setBooking(request)
}