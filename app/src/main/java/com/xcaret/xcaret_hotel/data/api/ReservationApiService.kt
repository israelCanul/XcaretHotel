package com.xcaret.xcaret_hotel.data.api

import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.entity.ReservationDetailResponse
import com.xcaret.xcaret_hotel.data.entity.ReservationItemResponse
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Settings
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ReservationApiService() {
    private val api = Retrofit.Builder()
        .baseUrl(Settings.getParam(Constants.api_itinerary_url, HotelXcaretApp.mContext)+'/')
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(ReservationApi::class.java)

    fun retrieveByEmail(email: String) = api.retrieveByEmail(email)

    fun byReferenceEmail(request: ReservationItemResponse): Single<ReservationDetailResponse> {
        return api.byReferenceEmail(
            language = request.Language,
            currency = request.Currency ?: "",
            email = request.Email ?: "",
            reservationNumber = request.ReservationNumber ?: "",
            details = request.Details,
            balance = request.Balance
        )
    }
}