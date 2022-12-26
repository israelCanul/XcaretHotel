package com.xcaret.xcaret_hotel.data.api

import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Settings
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class XwitchApiService {
    companion object{
        private val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        private val client = OkHttpClient.Builder()
            //.addInterceptor(InterpectorXkynet())
            .addInterceptor(logging)
            .build()

        var xkynetApi: XwitchAPI? = null

        fun getInstance():XwitchAPI{
            if (xkynetApi == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(Settings.getParam(Constants.sf_booking_engine, HotelXcaretApp.mContext))
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                xkynetApi = retrofit.create(XwitchAPI::class.java)
            }
            return xkynetApi!!
        }
    }
}