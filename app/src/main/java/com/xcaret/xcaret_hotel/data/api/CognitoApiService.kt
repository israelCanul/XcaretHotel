package com.xcaret.xcaret_hotel.data.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.entity.CognitoValidUserEntity
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Settings
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CognitoApiService(){

    private val user = Settings.getParam(Constants.cog_user_sf_user,HotelXcaretApp.mContext)
    private val pass = Settings.getParam(Constants.cog_user_sf_password,HotelXcaretApp.mContext)
    private val client = OkHttpClient.Builder()
        .addInterceptor(interpectorCognito(
            user,pass))
        .build()
    val gson = GsonBuilder()
        .setLenient()
        .create()


//    private val api = Retrofit.Builder()
//        .baseUrl(Settings.getParam(Constants.base_url_cognito_auth, HotelXcaretApp.mContext))
//        .addConverterFactory(GsonConverterFactory.create())
//        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//        .build()
//        .create(CognitoApi::class.java)

    private val api = Retrofit.Builder()
        .baseUrl(Settings.getParam(Constants.base_url_cognito_auth, HotelXcaretApp.mContext))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(CognitoApi::class.java)

    fun validateUser(username: String, password: String): Single<CognitoValidUserEntity?> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Username", username)
        jsonObject.addProperty("Password", password)
        return api.validateuser(jsonObject)
    }

    fun getUser(username: String): Single<CognitoValidUserEntity?> {
        return api.getUser(username)
    }
}