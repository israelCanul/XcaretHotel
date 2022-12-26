package com.xcaret.xcaret_hotel.view.config

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import com.xcaret.xcaret_hotel.domain.Currency
import java.util.*

object Language {
    private const val APP_LANG = "APP_LANG"
    private const val LANG_CODE_DEFAULT = "en"
    private const val LANG_COUNTRY_CODE_DEFAULT = "US"
    private const val LANG_NAME_SF_DEFAULT = "Inglés"
    const val LANG_CURRENCY_ISO_DEFAULT = "USD"

    private const val LANG_CODE = "LANG_CODE"
    private const val LANG_COUNTRY_CODE = "LANG_COUNTRY_CODE"
    private const val LANG_NAME_SF = "LANG_NAME_SF"
    private const val LANG_CURRENCY_ISO = "LANG_CURRENCY_CODE"
    private const val LANG_CURRENCY_ID = "LANG_CURRENCY_ID"
    private const val LANG_CURRENCY_MILES = "LANG_CURRENCY_MILES"
    private const val LANG_CURRENCY_DECIMAL = "LANG_CURRENCY_DECIMAL"
    private const val LANG_CURRENCY_SYMBOL = "LANG_CURRENCY_SYMBOL"
    private const val LANG_DEVICE_AVAILABILITY = "LANG_DEVICE_AVAILABILITY"
    private const val LANG_CHANGE = "LANG_CHANGE"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(APP_LANG, Context.MODE_PRIVATE)
    }

    fun setLangCode(langCode: String?, context: Context){
        val editor = getSharedPreferences(context).edit()
        editor.putString(LANG_CODE, langCode?.trim()).apply()
    }

    fun getLangCode(context: Context): String{
        return getSharedPreferences(context).getString(LANG_CODE, LANG_CODE_DEFAULT) ?: LANG_CODE_DEFAULT
    }

    fun isDeviceLangAvailable(context: Context): Boolean{
    return getSharedPreferences(context).getBoolean(LANG_DEVICE_AVAILABILITY, false)
    }

    fun setDeviceLangAvailable(isDeviceLangAvailable: Boolean?, context: Context){
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(LANG_DEVICE_AVAILABILITY, isDeviceLangAvailable?:false).apply()
    }

    fun isLangChangeRequestedFromDialog(context: Context): Boolean{
        return getSharedPreferences(context).getBoolean(LANG_CHANGE, false)
    }

    fun setLangChangeRequestedFromDialog(isLangChangeRequested: Boolean?, context: Context){
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(LANG_CHANGE, isLangChangeRequested?:false).apply()
    }


    fun setLangNameSF(langCode: String?, context: Context){
        val editor = getSharedPreferences(context).edit()
        editor.putString(LANG_NAME_SF, langCode?.trim()).apply()
    }

    fun getLangNameSF(context: Context): String{
        return getSharedPreferences(context).getString(LANG_NAME_SF, LANG_NAME_SF_DEFAULT) ?: LANG_NAME_SF_DEFAULT
    }

    fun isNullOrEmpty(context: Context): Boolean {
        return getSharedPreferences(context).getString(LANG_CODE, "").isNullOrEmpty()
    }

    fun setCountryCode(countryCode: String?, context: Context){
        val editor = getSharedPreferences(context).edit()
        editor.putString(LANG_COUNTRY_CODE, countryCode?.trim()?.toUpperCase(Locale.getDefault())).apply()
    }

    fun getCountryCode(context: Context): String{
        return getSharedPreferences(context).getString(LANG_CODE, LANG_COUNTRY_CODE_DEFAULT) ?: LANG_COUNTRY_CODE_DEFAULT
    }

    fun setCurrency(currency: Currency, context: Context){
        setCurrency(currency.iso ?: LANG_CURRENCY_ISO_DEFAULT, context)
        setCurrencyId(currency.id, context)
        setCurrencyDecimal(currency.decimal, context)
        setCurrencyMiles(currency.miles, context)
        setCurrencySymbol(currency.symbol, context)
    }

    fun getCurrency(context: Context): String {
        return getSharedPreferences(context).getString(LANG_CURRENCY_ISO, "") ?: ""
    }

    fun setCurrency(currencyISO: String, context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(LANG_CURRENCY_ISO, currencyISO.trim()).apply()
    }

    fun getCurrencyId(context: Context): Int {
        return getSharedPreferences(context).getInt(LANG_CURRENCY_ID, 0)
    }

    fun setCurrencyId(currencyId: Int, context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(LANG_CURRENCY_ID, currencyId).apply()
    }

    fun setCurrencyDecimal(decimal: String?, context: Context){
        val editor = getSharedPreferences(context).edit()
        editor.putString(LANG_CURRENCY_DECIMAL, decimal ?: Constants.DECIMAL_DEFAULT).apply()
    }

    fun getCurrencyDecimal(context: Context): String{
        return getSharedPreferences(context).getString(LANG_CURRENCY_DECIMAL, Constants.DECIMAL_DEFAULT) ?: Constants.DECIMAL_DEFAULT
    }

    fun setCurrencyMiles(miles: String?, context: Context){
        val editor = getSharedPreferences(context).edit()
        editor.putString(LANG_CURRENCY_MILES, miles ?: Constants.MILES_DEFAULT).apply()
    }

    fun getCurrencyMiles(context: Context): String{
        return getSharedPreferences(context).getString(LANG_CURRENCY_MILES, Constants.MILES_DEFAULT) ?: Constants.MILES_DEFAULT
    }

    fun setCurrencySymbol(miles: String?, context: Context){
        val editor = getSharedPreferences(context).edit()
        editor.putString(LANG_CURRENCY_SYMBOL, miles ?: Constants.SYMBOL_DEFAULT).apply()
    }

    fun getCurrencySymbol(context: Context): String{
        return getSharedPreferences(context).getString(LANG_CURRENCY_SYMBOL, Constants.SYMBOL_DEFAULT) ?: Constants.SYMBOL_DEFAULT
    }

    fun getLocale(context: Context): Locale{
        return Locale(getLangCode(context), getCountryCode(context))
    }

    fun changeLanguage(langCode: String, countryCode: String, nameSF: String = LANG_NAME_SF_DEFAULT, context: Context, result: (success: Boolean) -> Unit){
        if(langCode.trim() == getLangCode(context)) result(false)
        else{
            setLangCode(langCode, context)
            setCountryCode(countryCode, context)
            setLangNameSF(nameSF, context)
            val locale = getLocale(context)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) updateResources(context, locale)
            else updateResourcesLegacy(context, locale)
            result(true)
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    @Suppress("DEPRECATION")
    @SuppressWarnings("deprecation")
    private fun updateResourcesLegacy(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        return context
    }
}