package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class FunctionsApiService {

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(FunctionsApi::class.java)

    fun createCustomToken(params: JsonObject): Single<JsonObject>{
        return api.createCustomToken(params)
    }

    companion object{
        const val BASE_URL = "https://us-central1-hotelxcaretmexico-b273b.cloudfunctions.net/"
    }
}