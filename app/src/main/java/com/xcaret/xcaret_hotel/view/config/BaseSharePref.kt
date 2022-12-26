package com.xcaret.xcaret_hotel.view.config

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.xcaret.xcaret_hotel.view.config.BaseSharePref.ValueType.*

abstract class BaseSharePref {
    abstract fun getSettingName(): String

    fun getSharedPreferences(context: Context): SharedPreferences {
//        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
//        return EncryptedSharedPreferences.create(
//            getSettingName(),
//            masterKeyAlias,
//            context,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
        return context.getSharedPreferences(getSettingName(), Context.MODE_PRIVATE)
    }

    fun setValue(key: String, value: Any, context: Context, type: ValueType = STRING){
        val editor = getSharedPreferences(context).edit()
        when(type){
            STRING -> { editor.putString(key, value.toString())}
            INT -> { editor.putInt(key, value as Int)}
            BOOLEAN -> { editor.putBoolean(key, value as Boolean)}
            FLOAT -> { editor.putFloat(key, value as Float)}
            else -> {}
        }
        editor.apply()
    }

    enum class ValueType {
        STRING,
        INT,
        BOOLEAN,
        FLOAT,
        LONG
    }
}