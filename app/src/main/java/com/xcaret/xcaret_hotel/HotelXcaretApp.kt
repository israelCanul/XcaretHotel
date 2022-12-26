package com.xcaret.xcaret_hotel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.xcaret.xcaret_hotel.data.repository.FirebaseReference
import com.xcaret.xcaret_hotel.view.config.LogHX
import com.xcaret.xcaret_hotel.view.config.MarketingCloud
import com.xcaret.xcaret_hotel.view.config.Session
import com.xcaret.xcaret_hotel.view.config.Settings
import java.util.*

class HotelXcaretApp: Application() {

    override fun onCreate() {
        super.onCreate()
        mContext = this
        if(Session.getDeviceUID(mContext).isNullOrEmpty())
            Session.setDeviceUID(UUID.randomUUID().toString(), mContext)
        setupFirebase()
        setupAmplify()
        setupTheme()
//        MarketingCloud.init(this) {
//            LogHX.i(HotelXcaretApp.javaClass.simpleName, "Result MKTCloud $it")
//        }
    }

    private fun setupFirebase(){
        FirebaseApp.initializeApp(mContext)
        FirebaseDatabase.getInstance(BuildConfig.DBCore).setPersistenceEnabled(true)
        FirebaseDatabase.getInstance(BuildConfig.DBReservations).setPersistenceEnabled(true)
        FirebaseDatabase.getInstance(FirebaseReference.FB_Security).setPersistenceEnabled(true)
    }

    private fun setupTheme(){
        val isNightMode = Settings.isActiveDarkTheme(this)
        LogHX.i("isNightMode", "$isNightMode")
        setDefaultNightMode(if(isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun setupAmplify(){
        try {

            val config = AmplifyConfiguration.builder(applicationContext)
                .devMenuEnabled(false)
                .build()
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(config, mContext)
            LogHX.i("MyAmplifyApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            LogHX.e("MyAmplifyApp",  error.localizedMessage ?: "")
        }
    }

    companion object{
        lateinit var mContext: Context
    }
}