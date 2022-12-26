package com.xcaret.xcaret_hotel.view.config

import android.content.Context
import android.util.Log
import android.util.Patterns
import com.salesforce.marketingcloud.MCLogListener
import com.salesforce.marketingcloud.MarketingCloudConfig
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.notifications.NotificationCustomizationOptions
import com.salesforce.marketingcloud.registration.c
import com.salesforce.marketingcloud.sfmcsdk.InitializationStatus
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdkInitializationStatus
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdkModuleConfig
import com.xcaret.xcaret_hotel.BuildConfig
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.domain.Hotel
import com.xcaret.xcaret_hotel.domain.User

object MarketingCloud {

    //private const val MC_APPLICATION_ID = "0d34d906-52bc-4536-a47c-a91a1045dc7f"
    //private const val MC_ACCESS_TOKEN = "xMfLWaY0ndK35snURsArUIl7"
    private const val MC_URL = "https://mch290xfgh1l3xhrgf8mvcxxtjjm.device.marketingcloudapis.com/"
    //private const val MC_APPLICATION_ID_DEV = "abe1442a-5010-4bc5-843b-6a2ae5b22a82"
    //private const val MC_ACCESS_TOKEN_DEV = "Z1UxhQC8RH0NHIaoDa3HqWeM"
    private const val FCM_SENDER_ID = "695818094309" //id remitente firebase
    private const val MID = "7329008"
    var isSDKInitialized = false


    fun init(context: Context, success: (res: Boolean) -> Unit){
        val applicationID = BuildConfig.MC_APPLICATION_ID
        val accesToken = BuildConfig.MC_ACCESS_TOKEN
        try {
            MarketingCloudSdk.setLogLevel(MCLogListener.VERBOSE)
            MarketingCloudSdk.setLogListener(MCLogListener.AndroidLogListener())

            if (!isSDKInitialized){
            SFMCSdk.configure(context , SFMCSdkModuleConfig.build {
                pushModuleConfig = MarketingCloudConfig.builder().apply {
                    setApplicationId(BuildConfig.MC_APPLICATION_ID)
                    setAccessToken(BuildConfig.MC_ACCESS_TOKEN)
                    setSenderId(FCM_SENDER_ID)
                    setMarketingCloudServerUrl(MC_URL)
                    setMid(MID)
                    setDelayRegistrationUntilContactKeyIsSet(true)
                    setNotificationCustomizationOptions(
                        NotificationCustomizationOptions.create(R.drawable.ic_notification)
                    )
                    // Other configuration options
                }.build(context)
            }) { initStatus ->
                when (initStatus.status) {
                    InitializationStatus.SUCCESS -> {
                        success(true)
                        isSDKInitialized = true
                    }
                    else -> {
                        isSDKInitialized = false
                        Log.e("MarketinCloudError", "error")
                    }
                }
            }
            }
        }catch (e: Exception){
            LogHX.e("MarketingCloudError", e.toString())
        }
    }

    /*
    * Para que el dispositivo se ligue a un contacto existente
    * */
    fun setManualContactKey(contactKey: String){
        try {
//            MarketingCloudSdk.requestSdk { sdk ->
//                val registrationManager = sdk.registrationManager
//                val currentKey = registrationManager.contactKey
//                LogHX.i("MarketingCloudSetKey", "$contactKey == $currentKey")
//                if (currentKey != contactKey) {
//                    registrationManager.edit().run {
//                        setContactKey(contactKey)
//                        commit()
//                    }
//                }
//            }

            SFMCSdk.requestSdk { sdk ->
                sdk.mp {
                    val currentContactKey = it.moduleIdentity.profileId
                    if (currentContactKey != contactKey) {
                        sdk.identity.setProfileId(contactKey)
                    }
                }
            }
        }catch (e:Exception){
            LogHX.e("MarketingCloudError", e.toString())
        }
    }

    fun registerDataToMKTCloud(user: User){
        try {

            SFMCSdk.requestSdk { sdk ->
                val attributesMap = mutableMapOf<String, String>()
                attributesMap[Constants.MktCloud_City] = user.city
                attributesMap[Constants.MktCloud_Country] = user.country_code
                attributesMap[Constants.MktCloud_ZipCode] = user.cp
                attributesMap[Constants.MktCloud_Email] = user.email
                //attributesMap[Constants.MktCloud_Device] = Utils.getDeviceName().toString()
                attributesMap[Constants.MktCloud_FirstName] = user.firstName
                attributesMap[Constants.MktCloud_FullName] = user.name
                attributesMap[Constants.MktCloud_Origin] = "App_Hoteles"
                attributesMap[Constants.MktCloud_PhoneNumber] = user.phone
                attributesMap[Constants.MktCloud_Title] = user.title_code
                attributesMap[Constants.MktCloud_State] = user.state_code
                attributesMap[Constants.MktCloud_LastName] = user.lastName
                //attributesMap[Constants.MktCloud_DeviceLanguage] = Language.getLangCode(HotelXcaretApp.mContext)
                attributesMap[Constants.MktCloud_Address] = user.address


                val attributesToClear = listOf(
                    Constants.MktCloud_City,
                    Constants.MktCloud_ZipCode,
                    Constants.MktCloud_Email,
                    Constants.MktCloud_Device,
                    Constants.MktCloud_FirstName,
                    Constants.MktCloud_FullName,
                    Constants.MktCloud_Origin,
                    Constants.MktCloud_PhoneNumber,
                    Constants.MktCloud_Title,
                    Constants.MktCloud_LastName,
                    Constants.MktCloud_Address,
                    Constants.MktCloud_State,
                    Constants.MktCloud_Country
                )

                // Set Attribute value
                sdk.identity.run {

                    //clearProfileAttributes(attributesToClear)
                    setProfileAttributes(attributesMap)

                }

            }
        }catch (e:Exception){
            LogHX.e("MarketingCloudError", e.toString())
        }
    }
}