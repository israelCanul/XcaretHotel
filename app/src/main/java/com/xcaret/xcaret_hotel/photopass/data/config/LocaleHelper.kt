package com.xcaret.xcaret_hotel.photopass.data.config

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import java.util.*

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    private const val SELECTED_COUNTRY = "Locale.Helper.Selected.Country"

    fun onAttach(context: Context): Context {
        val locale = load(context)
        return setLocale(context, locale)
    }

    fun getLocale(context: Context): Locale {
        return load(context)
    }

    fun setLocale(context: Context, locale: Locale): Context {
        persist(context, locale)
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) updateResources(context, locale)
        else updateResourcesLegacy(context, locale)
    }


    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(LocaleHelper::class.java.name, Context.MODE_PRIVATE)
    }

    private fun persist(context: Context, locale: Locale?) {
        if (locale == null) return
        getPreferences(context)
            .edit()
            .putString(SELECTED_LANGUAGE, locale.language)
            .putString(SELECTED_COUNTRY, locale.country)
            .apply()
    }

    private fun load(context: Context): Locale {
        val preferences = getPreferences(context)
        val len = if(Locale.getDefault().language != "en" && Locale.getDefault().language != "es") "en"
        else Locale.getDefault().language

        val cou = if(Locale.getDefault().country != "US" && Locale.getDefault().country != "MX") "US"
        else Locale.getDefault().country

        val language = preferences.getString(SELECTED_LANGUAGE, len)
        val country = preferences.getString(SELECTED_COUNTRY, cou)
        return Locale(language, country)
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