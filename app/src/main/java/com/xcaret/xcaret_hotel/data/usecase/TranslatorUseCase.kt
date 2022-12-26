package com.xcaret.xcaret_hotel.data.usecase

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.api.XwitchApiService
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.LogHX
import com.xcaret.xcaret_hotel.view.config.Settings
import retrofit2.Response

class TranslatorUseCase() {

    suspend fun requestLanguage(langCode: String) : Response<Void> {
        val language = JsonObject().apply {
            addProperty("langCode",langCode)
        }

        val headerObject = JsonObject().apply {
            addProperty("app", Settings.getParam(Constants.xkynetAPICode, HotelXcaretApp.mContext))
        }

        val jsonRequest = JsonObject().apply {
            add("header",headerObject)
            add("item",language)
        }
        val gson = GsonBuilder().setPrettyPrinting().create()
        val printJson = gson.toJson(jsonRequest)
        LogHX.i("requestLanguage req", printJson)

        return  XwitchApiService.getInstance().checkTranslation(params = jsonRequest)
    }

}