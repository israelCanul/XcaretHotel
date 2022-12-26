package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.entity.SalesForceGetProfileEntity
import com.xcaret.xcaret_hotel.data.entity.SalesForcePaxProfileEntity
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Session
import com.xcaret.xcaret_hotel.view.config.Settings
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class SalesForceApiService {
    private val api = Retrofit.Builder()
        .baseUrl(Settings.getParam(Constants.sf_contacts, HotelXcaretApp.mContext))
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(SalesForceApi::class.java)

    fun getpaxprofilerq(idToken: String = Session.getToken(HotelXcaretApp.mContext) ?: "", email: String): Single<SalesForceGetProfileEntity> {
        return api.getpaxprofilerq(idToken, email)
    }

    fun paxprofilerq(idToken: String = Session.getToken(HotelXcaretApp.mContext) ?: "", request: JsonObject): Single<SalesForcePaxProfileEntity> {
        return api.paxprofilerq(idToken, request)
    }

    fun getpaxprofilerqExternalId(idToken: String = Session.getToken(HotelXcaretApp.mContext) ?: "", externalId: String): Single<SalesForceGetProfileEntity> {
        return api.getpaxprofilerqExternalId(idToken, externalId)
    }
}