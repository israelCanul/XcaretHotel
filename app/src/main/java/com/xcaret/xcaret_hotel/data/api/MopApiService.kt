package com.xcaret.xcaret_hotel.data.api

import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.entity.PaymentBankEntity
import com.xcaret.xcaret_hotel.data.entity.PaymentBankInfoEntity
import com.xcaret.xcaret_hotel.domain.PaymenSecurity
import com.xcaret.xcaret_hotel.domain.Payment
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Settings
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MopApiService(baseUrl: String) {

    private val api = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(MopApi::class.java)

    private val apiBinInfo = Retrofit.Builder()
        //.baseUrl("https://xapis-preprod.xcaret.com/bindb-service/")
        .baseUrl(Settings.getParam(Constants.bin_info_url, HotelXcaretApp.mContext)+"/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(MopApi::class.java)

    fun banks(security: PaymenSecurity, request: Payment): Single<PaymentBankEntity>{
        val header = mutableMapOf(
            "Username" to security.Username,
            "Password" to security.Password,
            "Token" to security.Token
        )
        return api.banks(
            header,
            request.currency,
            request.channel,
            request.buyDate,
            request.visitDate,
            request.amount,
            request.iso3Country,
            request.iso2Country
        )
    }

    fun bankInfo(security: PaymenSecurity, bin: String): Single<PaymentBankInfoEntity> {
        val header = mutableMapOf(
            "Username" to security.Username,
            "Password" to security.Password,
            "Token" to security.Token,
            "bin" to bin
        )
        return api.bankInfo(header)
    }

    fun bankInfoV2(bin:Int) : Single<PaymentBankInfoEntity> {
        val request = JsonObject()
        request.addProperty("BinNumber",bin)
        return apiBinInfo.bankInfoV2(request)
    }
}