package com.xcaret.xcaret_hotel.view.config

import android.content.Context

object Session: BaseSharePref() {

    private const val APP_SESSION = "APP_SESSION"
    private const val UID = "UID"
    private const val IDTOKEN = "IDTOKEN"
    private const val DATE_REMEMBER_INCOMPLETE_PROFILE = "DATE_REMEMBER_INCOMPLETE_PROFILE"
    private const val SHOW_WELCOME_ALERT = "SHOW_WELCOME_ALERT"
    private const val IS_VISITOR = "IS_VISITOR"
    private const val IS_HOTEL_INFO_CLEANED ="IS_HOTEL_INFO_CLEANED"
    private const val COUNTRY_CODE ="COUNTRY_CODE"
    private const val ISO_PAYMENT_CODE ="ISO_PAYMENT_CODE"
    private const val PAYMENT_INFO ="ISO_PAYMENT_INFO"
    private const val PICKUP_ARRIVAL ="PICKUP_ARRIVAL"
    private const val PICKUP_DEPARTURE ="PICKUP_DEPARTURE"
    private const val LOGIN_TYPE ="LOGIN_TYPE"
    private const val DEVICE_UID = "DEVICE_UID"

    override fun getSettingName(): String = APP_SESSION

    fun setDeviceUID(value: String, context: Context) = setValue(UID, value, context)
    fun getDeviceUID(context: Context) = getSharedPreferences(context).getString(UID, "") ?: ""

    fun setUID(value: String, context: Context) = setValue(UID, value, context)
    fun getUID(context: Context) = getSharedPreferences(context).getString(UID, "")

    fun setToken(value: String, context: Context) = setValue(IDTOKEN, value, context)
    fun getToken(context: Context) = getSharedPreferences(context).getString(IDTOKEN, "")

    fun setRememberDate(value: String, context: Context) = setValue(DATE_REMEMBER_INCOMPLETE_PROFILE, value, context)
    fun getRememberDate(context: Context) = getSharedPreferences(context).getString(DATE_REMEMBER_INCOMPLETE_PROFILE, "") ?: ""

    fun setShowWelcomeAlert(value: Boolean, context: Context) = setValue(SHOW_WELCOME_ALERT, value, context, ValueType.BOOLEAN)
    fun isShowWelcomeAlert(context: Context) = getSharedPreferences(context).getBoolean(SHOW_WELCOME_ALERT, false)

    fun setVisitor(value: Boolean, context: Context) = setValue(IS_VISITOR, value, context, ValueType.BOOLEAN)
    fun isVisitor(context: Context) = getSharedPreferences(context).getBoolean(IS_VISITOR, false)

    fun setHotelInfoCleaned(value: Boolean,context: Context)= setValue(IS_HOTEL_INFO_CLEANED,value,context,ValueType.BOOLEAN)
    fun isHotelInfoCleaned(context: Context)=getSharedPreferences(context).getBoolean(
        IS_HOTEL_INFO_CLEANED,false)

    fun setCountryCode(value: String, context: Context) = setValue(COUNTRY_CODE, value, context)
    fun getCountryCode(context: Context) = getSharedPreferences(context).getString(COUNTRY_CODE, "") ?: ""

    fun setIsoPaymentCode(value: String, context: Context) = setValue(ISO_PAYMENT_CODE, value, context)
    fun getIsoPaymentCode(context: Context) = getSharedPreferences(context).getString(ISO_PAYMENT_CODE, "") ?: ""

    fun setLoginType(value: String, context: Context) = setValue(LOGIN_TYPE, value, context)
    fun getLoginType(context: Context) = getSharedPreferences(context).getString(LOGIN_TYPE, "NONE") ?: "NONE"

    fun setPaymentInfo(value: String, context: Context) = setValue(PAYMENT_INFO, value, context)
    fun getPaymentInfo(context: Context) = getSharedPreferences(context).getString(PAYMENT_INFO, "") ?: ""

    fun setPickUpArrival(value: String, context: Context) = setValue(PICKUP_ARRIVAL, value, context)
    fun getPickUpArrival(context: Context) = getSharedPreferences(context).getString(PICKUP_ARRIVAL, "") ?: ""
    fun setPickUpDeparture(value: String, context: Context) = setValue(PICKUP_DEPARTURE, value, context)
    fun getPickUpDeparture(context: Context) = getSharedPreferences(context).getString(PICKUP_DEPARTURE, "") ?: ""


    fun setCounterBankInfoRequest(value :Int, ctx :Context){
        var before = getCounterBankInfoRequest(ctx)

        setValue("BankInfoRequest",before+value,ctx,ValueType.INT)

    }
    fun getCounterBankInfoRequest(context: Context)= getSharedPreferences(context).getInt("BankInfoRequest",0)?:0

}