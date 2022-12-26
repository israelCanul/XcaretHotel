package com.xcaret.xcaret_hotel.domain

import com.google.gson.Gson
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.view.config.Session
import com.xcaret.xcaret_hotel.view.config.navbottombar.util.spToPx

data class PaymenSecurity(
    val Username: String = "",
    val Password: String = "",
    val Token: String = ""
)

data class Payment(
    val currency: String = "",
    val channel: Int = 0,
    val buyDate: String = "",
    val visitDate: String = "",
    val amount: Double = 0.0,
    val iso3Country: String = "",
    val iso2Country: String = ""
)

data class PaymentPickup(
    var airline: Airline? = null,
    var flightNumber: Int? = null,
    var hour: String? = "",
    var terminal: AirlineTerminal? = null,
    var isDeparture: Boolean = false
){
    fun toJson(){
        try {
            val jsonString = Gson().toJson(this)
            if (isDeparture){
                Session.setPickUpDeparture(jsonString, HotelXcaretApp.mContext)
            }else{
                Session.setPickUpArrival(jsonString, HotelXcaretApp.mContext)
            }
        }catch (exc:Exception){

        }
    }


}

data class PaymentPickupError(
    var hasError: Boolean = false,
    var airline: Int = 0,
    var flightNumber: Int = 0,
    var hour: Int = 0,
    var terminal: Int = 0
)

data class PaymentGenericResponse<T>(
    val success: Boolean = true,
    var errorCode: Int = 0,
    val data: T? = null
)