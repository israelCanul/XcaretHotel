package com.xcaret.xcaret_hotel.view.config

import android.app.Activity.RESULT_OK
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.xcaret.xcaret_hotel.HotelXcaretApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsManager(context: Context) {

    //private val Context.dataStore = context.createDataStore(name = SETTINGS_PREF)
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_PREF)

    suspend fun setCurrentMetric(metric: TemperatureMetric){
        HotelXcaretApp.mContext.dataStore.edit { preference ->
            preference[CURRENT_METRIC] = metric.value
        }
    }

    val currentMetic: Flow<TemperatureMetric> = context.dataStore.data
        .map {preferences ->
            val value = preferences[CURRENT_METRIC] ?: 0
            TemperatureMetric.fromValue(value)
        }

    suspend fun setResultAppUpdate(result: Int){
        HotelXcaretApp.mContext.dataStore.edit { preference ->
            preference[RESULT_APP_UPDATE] = result
        }
    }

    val resultAppUpdate: Flow<Int> = context.dataStore.data
        .map {preferences ->
            preferences[RESULT_APP_UPDATE] ?: -100
        }

    suspend fun setUID(uid: String){
        HotelXcaretApp.mContext.dataStore.edit { preference ->
            preference[UID] = uid
        }
    }

    val getUID: Flow<String> = context.dataStore.data
        .map {preferences ->
            preferences[UID] ?: ""
        }

    companion object{
        const val SETTINGS_PREF = "settings_pref"
        val CURRENT_METRIC = intPreferencesKey("current_metric")
        val UID = stringPreferencesKey("uid")
        val RESULT_APP_UPDATE = intPreferencesKey("result_app_update")

        private var INSTANCE: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager{
            if(INSTANCE != null) return INSTANCE!!
            synchronized(this){
                val instance = SettingsManager(context)
                INSTANCE = instance
                return instance
            }
        }
    }

    enum class TemperatureMetric(val value: Int){
        Celsius(0),
        Fahrenheit(1);

        companion object {
            fun fromValue(value: Int) = TemperatureMetric.values().first { it.value == value }
        }
    }

    enum class UiMode {
        LIGHT, DARK
    }

}