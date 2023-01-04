package com.xcaret.xcaret_hotel.photopass.data.api

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PhotosApi {

    @POST("auth/Login")
    fun login(@Body params: JsonObject): Single<Login>

    @POST("media/GetCode")
    fun getCode(@Header("Authorization") idToken: String, @Body params: JsonObject): Single<Code>

    @POST("media/GetAlbum")
    fun getAlbum(@Header("Authorization") idToken: String, @Body params: JsonObject): Single<List<Album>>

    @POST("sale/GetSelectedParks")
    fun getSelectedParks(@Header("Authorization") idToken: String, @Body params: JsonObject): Single<List<SelectedPark>>

    @POST("media/GetPhotos")
    fun getPhotos(@Header("Authorization") idToken: String, @Body params: JsonObject): Single<GetPhotos>

    @POST("media/GetPhotoStatus") //Dont use
    fun getPhotoStatus(@Header("Authorization") idToken: String, @Body params: JsonObject): Single<PhotoStatus>

    @POST("sale/AddVisitPark")
    fun addVisitPark(@Header("Authorization") idToken: String, @Body params: JsonObject): Single<VisitPark>

    @POST("media/SendUrlCode")
    fun sendUrlCode(@Header("Authorization") idToken: String, @Body params: JsonObject): Single<UrlCode>

    @POST("sale/AddMediaLog")
    fun addMediaLog(@Header("Authorization") idToken: String, @Body params: JsonObject): Single<MediaLog>


}