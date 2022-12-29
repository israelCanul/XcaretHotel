package com.xcaret.xcaret_hotel.view.photopass.data.api

import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.view.photopass.data.config.Constants
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.xcaret.xcaret_hotel.HotelXcaretApp.Companion.mContext
import com.xcaret.xcaret_hotel.view.config.Settings

class PhotoApiService() {

    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(90, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(Settings.getParam(Constants.photo_api_url, mContext))
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)
        .build()
        .create(PhotosApi::class.java)

    fun login(params: JsonObject): Single<Login> {
        return api.login(params)
    }

    fun getCode(idToken: String, params: JsonObject): Single<Code> {
        return api.getCode("$PREFIX_BEARER $idToken", params)
    }

    fun getAlbum(idToken: String, params: JsonObject): Single<List<Album>> {
        return api.getAlbum("$PREFIX_BEARER $idToken", params)
    }

    fun getSelectedParks(idToken: String, params: JsonObject): Single<List<SelectedPark>> {
        return api.getSelectedParks("$PREFIX_BEARER $idToken", params)
    }

    fun getPhotos(idToken: String, params: JsonObject): Single<GetPhotos> {
        return api.getPhotos("$PREFIX_BEARER $idToken", params)
    }

    fun getPhotoStatus(idToken: String, params: JsonObject): Single<PhotoStatus> {
        return api.getPhotoStatus("$PREFIX_BEARER $idToken", params)
    }

    fun addVisitPark(idToken: String, params: JsonObject): Single<VisitPark> {
        return api.addVisitPark("$PREFIX_BEARER $idToken", params)
    }

    fun sendUrlCode(idToken: String, params: JsonObject): Single<UrlCode> {
        return api.sendUrlCode("$PREFIX_BEARER $idToken", params)
    }

    fun addMediaLog(idToken: String, params: JsonObject): Single<MediaLog> {
        return api.addMediaLog("$PREFIX_BEARER $idToken", params)
    }
    companion object{
        const val PREFIX_BEARER = "Bearer"
    }
}