package com.xcaret.xcaret_hotel.view.config

import android.util.Log
import com.xcaret.xcaret_hotel.BuildConfig

object LogHX {
    private val tag = "HXM-"
    private val isDebug: Boolean = BuildConfig.DEBUG

    fun e(clazz: String = tag, description: String?){
        if(isDebug) Log.e(clazz, description ?: "")
    }

    fun w(clazz: String = tag, description: String?){
        if(isDebug) Log.w(clazz, description ?: "")
    }

    fun i(clazz: String = tag, description: String?){
        if(isDebug) Log.i(clazz, description ?: "")
    }

    fun d(clazz: String = tag, description: String?){
        if(isDebug) Log.d("$tag$clazz", description ?: "")
    }

    fun debugMode( description: String?){
        if(isDebug) Log.d("$tag-Debug", description ?: "")
    }
}