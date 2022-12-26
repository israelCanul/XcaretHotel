package com.xcaret.xcaret_hotel.view.config.analytics

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.facebook.appevents.AppEventsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.xcaret.xcaret_hotel.BuildConfig
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.general.ui.MainActivity
import java.math.BigDecimal
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.view.config.Session
import java.util.Currency


object XcaretAnalytic{

    fun logEvent(ctx: Context, type: AnalyticType, args: Bundle = Bundle.EMPTY){
        //if(ctx !is MainActivity || BuildConfig.DEBUG) return
        if(ctx !is MainActivity) return
        when(type){
            AnalyticType.DEFAULT -> default(ctx, args)
            AnalyticType.LOGIN, AnalyticType.SIGN_UP -> loginOrSignUp(ctx, type, args)
            AnalyticType.BEGIN_CHECKOUT, AnalyticType.PURCHASE -> beginCheckOutOrPurchase(ctx, type, args)
            AnalyticType.SEARCH_FACEBOOK -> searchFacebook(ctx, args)
            AnalyticType.VIEW_CONTENT_FACEBOOK -> viewcontenFacebook(ctx, args)
            AnalyticType.PURCHASE_FACEBOOK -> purchaseFacebook(ctx, args)
            AnalyticType.INITIATE_CHECKOUT_FACEBOOK -> initiatecheckoutFacebook(ctx, args)
            AnalyticType.SEARCH -> search(ctx, args)
            AnalyticType.ADD_TO_CART -> addToCart(ctx,type, args)
        }
    }

    private fun search(mainActivity: MainActivity, args: Bundle){
        mainActivity.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, args)
    }

    /*
    * ======== args ===========
    * FirebaseAnalytics.Param.ITEM_ID: String
    * FirebaseAnalytics.Param.ITEM_NAME: String
    * FirebaseAnalytics.Param.CONTENT_TYPE: String
    * */
    private fun default(mainActivity: MainActivity, args: Bundle){
        mainActivity.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, args)
    }

    /*
    * ======== args ===========
    * Provider: String
    * Status: Boolean
    * */
    private fun loginOrSignUp(mainActivity: MainActivity, type: AnalyticType, args: Bundle){
        val aType = if(type == AnalyticType.LOGIN) FirebaseAnalytics.Event.LOGIN
        else FirebaseAnalytics.Event.SIGN_UP
        mainActivity.firebaseAnalytics.logEvent(aType, args)
    }

    private fun beginCheckOutOrPurchase(mainActivity: MainActivity, type: AnalyticType, args: Bundle){
        val aType = if(type == AnalyticType.BEGIN_CHECKOUT) FirebaseAnalytics.Event.BEGIN_CHECKOUT
        else FirebaseAnalytics.Event.PURCHASE
        mainActivity.firebaseAnalytics.logEvent(aType, args)
    }

    private fun addToCart(mainActivity: MainActivity, type: AnalyticType, args: Bundle){
        val evtype = FirebaseAnalytics.Event.ADD_TO_CART
        mainActivity.firebaseAnalytics.logEvent(evtype, args)
    }


    //region Events Facebook

    private fun searchFacebook(mainActivity: MainActivity,args: Bundle){
        mainActivity.facebookAnalytics.
        logEvent(AppEventsConstants.EVENT_NAME_SEARCHED,args)
    }

    private fun viewcontenFacebook(mainActivity: MainActivity,args: Bundle){
        mainActivity.facebookAnalytics.
        logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT,args)
    }

    private fun initiatecheckoutFacebook(mainActivity: MainActivity,args: Bundle){
        mainActivity.facebookAnalytics.
        logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT,args)

    }

    private fun purchaseFacebook(mainActivity: MainActivity,args: Bundle){
        val amn = args.getDouble(AnalyticConstant.KEY_AMOUNT_FB,0.0)
        val bigDecimal = BigDecimal.valueOf( amn)
        val currency =  Currency.getInstance(args.getString(AnalyticConstant.KEY_CURRENCY_FB,"USD"));
        mainActivity.facebookAnalytics.
        logPurchase(bigDecimal,currency,args)
    }


    //endregion
}

fun Fragment.beginCheckOutOrPurchase(type: AnalyticType, data: Booking){
    data.suiteQuotes.forEach { sq ->
        val args = Bundle()
        args.putString(FirebaseAnalytics.Param.ITEM_NAME, sq.suiteNameSelected)
        args.putString(FirebaseAnalytics.Param.QUANTITY, sq.calculateNights().toString())
        args.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, sq.hodelCode.toString())
        args.putDouble(FirebaseAnalytics.Param.PRICE, sq.ratePlan?.amount ?: 0.0)
        args.putString(FirebaseAnalytics.Param.CURRENCY, sq.currency)
        XcaretAnalytic.logEvent(requireContext(), type, args)
    }
}

fun Fragment.logEvent(id: String, name: String, type: String){
    try {
        val args = Bundle()
        args.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        args.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        args.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type)
        XcaretAnalytic.logEvent(requireContext(), AnalyticType.DEFAULT, args)
    }catch (exc:Exception){

    }

}

fun Fragment.loginOrSignUp(type: AnalyticType, provider: ProviderType, status: Boolean){
    val args = Bundle()
    args.putString(AnalyticConstant.KEY_PROVIDER, provider.value)
    args.putBoolean(AnalyticConstant.KEY_STATUS, status)
    XcaretAnalytic.logEvent(requireContext(), type, args)
}

fun Fragment.Search(query: String){
    val args = Bundle()
    args.putString(FirebaseAnalytics.Param.SEARCH_TERM, query)
    XcaretAnalytic.logEvent(requireContext(), AnalyticType.SEARCH, args)
}

//region
fun Fragment.loggEventFacebook(type: AnalyticType) {
    val args = Bundle()
    args.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, AnalyticConstant.KEY_HOTEL_FB)
    args.putString(AnalyticConstant.KEY_CHECKIN_DATE_FB, AnalyticConstant.KEY_HOTEL_FB)
    args.putString(AnalyticConstant.KEY_CHECKOUT_DATE_FB, AnalyticConstant.KEY_HOTEL_FB)
    args.putString(AnalyticConstant.KEY_CITY_FB, AnalyticConstant.KEY_HOTEL_FB)
    args.putString(AnalyticConstant.KEY_REGION_FB, AnalyticConstant.KEY_HOTEL_FB)
    args.putString(AnalyticConstant.KEY_COUNTRY_FB, AnalyticConstant.KEY_HOTEL_FB)
    args.putString(AnalyticConstant.KEY_NUM_ADULTS_FB, AnalyticConstant.KEY_HOTEL_FB)
    args.putString(AnalyticConstant.KEY_NUM_CHILDREN_FB, AnalyticConstant.KEY_HOTEL_FB)
}

fun Fragment.addToCart(type:AnalyticType,data: Booking){
    data.suiteQuotes.forEach { sq ->
        val args = Bundle()
        args.putString(FirebaseAnalytics.Param.ITEM_NAME, sq.suiteNameSelected)
        args.putString(FirebaseAnalytics.Param.QUANTITY, sq.calculateNights().toString())
        args.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, sq.hodelCode.toString())
        args.putDouble(FirebaseAnalytics.Param.PRICE, sq.ratePlan?.amount ?: 0.0)
        args.putString(FirebaseAnalytics.Param.CURRENCY, sq.currency)
        XcaretAnalytic.logEvent(requireContext(), type, args)
    }
}

//region Facebook


fun Fragment.logInitCheckoutOrPurchaseFacebook(type: AnalyticType,data:Booking){
    val provName = Session.getLoginType(HotelXcaretApp.mContext)
    if (provName == ProviderType.Facebook.value) {
        data.suiteQuotes.forEach { sq ->
            val args = Bundle()
            args.putString(AnalyticConstant.KEY_SUITE_NAME_FB, sq.suiteNameSelected)
            args.putString(AnalyticConstant.KEY_QUANTITY_FB, sq.calculateNights().toString())
            args.putString(AnalyticConstant.KEY_HOTEL_FB, sq.hodelCode.toString())
            args.putDouble(AnalyticConstant.KEY_AMOUNT_FB, sq.ratePlan?.amount ?: 0.0)
            args.putString(AnalyticConstant.KEY_CURRENCY_FB, sq.currency)
            XcaretAnalytic.logEvent(requireContext(), type, args)
        }
    }

}

fun Fragment.SearchFacebook(type: AnalyticType,data:Booking){
    val provName = Session.getLoginType(HotelXcaretApp.mContext)
    if (provName == ProviderType.Facebook.value) {
        data.suiteQuotes.forEach { sq ->
            val args = Bundle()
            args.putString(FirebaseAnalytics.Param.ITEM_NAME, sq.suiteNameSelected)
            args.putString(FirebaseAnalytics.Param.QUANTITY, sq.calculateNights().toString())
            args.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, sq.hodelCode.toString())
            args.putDouble(FirebaseAnalytics.Param.PRICE, sq.ratePlan?.amount ?: 0.0)
            args.putString(FirebaseAnalytics.Param.CURRENCY, sq.currency)
            XcaretAnalytic.logEvent(requireContext(), type, args)
        }
    }

}



fun androidx.activity.ComponentActivity.logEventViewContentFacebook(ctx:Context,type: AnalyticType,data:RoomType){
    val provName = Session.getLoginType(HotelXcaretApp.mContext)
    if (provName == ProviderType.Facebook.value) {
        val args = Bundle()
        args.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, AnalyticConstant.KEY_HOTEL_FB)
        args.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, data.ratePlan[0].hotelCode.toString())
        args.putString(AnalyticConstant.KEY_CHECKOUT_DATE_FB, data.ratePlan[0].endDate)
        args.putString(AnalyticConstant.KEY_CHECKIN_DATE_FB, data.ratePlan[0].startDate)
        args.putString(AnalyticConstant.KEY_AMOUNT_FB,data.ratePlan[0].amount.toString())

        XcaretAnalytic.logEvent(ctx, type, args)
    }
}
//endregion