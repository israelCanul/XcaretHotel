package com.xcaret.xcaret_hotel.view.config

import android.content.Context

object Settings: BaseSharePref() {
    private const val APP_SETTINGS = "APP_SETTINGS"
    private const val DARK_THEME_ACTIVE = "DARK_THEME_ACTIVE"
    private const val HOTEL_SELECTED = "HOTEL_SELECTED"
    private const val HOTEL_UID_SELECTED = "HOTEL_UID_SELECTED"

    override fun getSettingName(): String = APP_SETTINGS

    fun activeDarkTheme(value: Boolean, context: Context) = setValue(DARK_THEME_ACTIVE, value, context, ValueType.BOOLEAN)
    fun isActiveDarkTheme(context: Context) = getSharedPreferences(context).getBoolean(DARK_THEME_ACTIVE, false)
    fun setHotelSelected(value: Int, context: Context) = setValue(HOTEL_SELECTED, value, context, ValueType.INT)
    fun getHotelSelected(context: Context) = getSharedPreferences(context).getInt(HOTEL_SELECTED, 0)
    fun setHotelUIDSelected(value: String, context: Context) = setValue(HOTEL_UID_SELECTED, value, context)
    fun getHotelUIDSelected(context: Context) = getSharedPreferences(context).getString(HOTEL_UID_SELECTED, "") ?: ""
    fun setParam(key: String, value: String, context: Context) = setValue(key, value, context)
    fun getParam(key: String, context: Context) = getSharedPreferences(context).getString(key, "") ?: ""
}