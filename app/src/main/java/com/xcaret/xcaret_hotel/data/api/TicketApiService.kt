package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonArray
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.entity.TicketEntity
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Settings
import com.xcaret.xcaret_hotel.view.config.toBase64
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class TicketApiService {

    private val api = Retrofit.Builder()
        .baseUrl(Settings.getParam(Constants.api_tickets, HotelXcaretApp.mContext))
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(TicketApi::class.java)

    fun ventas(request: JsonArray): Single<TicketEntity?> {
        val user = Settings.getParam(Constants.user_tickets, HotelXcaretApp.mContext)
        val pass = Settings.getParam(Constants.password_tickets, HotelXcaretApp.mContext)
        val token = "$user:$pass".toBase64()
        return api.ventas(request, "Basic $token")
    }
}