package com.xcaret.xcaret_hotel.view.config

import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R

object ToolbarUtils {
    val toolbars = mutableMapOf<String,ToolbarSettings>(
        FragmentTags.Splash.value to ToolbarSettings(showToolbar = false, showBottomNav = false),
        FragmentTags.Home.value to ToolbarSettings(showToolbar = false, showBottomNav = true),
        FragmentTags.Map.value to ToolbarSettings(showToolbar = false, showBottomNav = true),
        FragmentTags.AFI.value to ToolbarSettings(showToolbar = true, showBackBtn = false, title = HotelXcaretApp.mContext.getString(R.string.rkey_all_fun_inclusive), showBottomNav = true),
        FragmentTags.Menu.value to ToolbarSettings(showToolbar = true, showBackBtn = false, title = HotelXcaretApp.mContext.getString(R.string.rkey_menu_profile), bgResource = android.R.color.transparent, showBottomNav = true),
        FragmentTags.ParkDetail.value to ToolbarSettings(showToolbar = true, title = HotelXcaretApp.mContext.getString(R.string.rkey_all_fun_inclusive)),
        FragmentTags.FerryDetail.value to ToolbarSettings(showToolbar = true, title = HotelXcaretApp.mContext.getString(R.string.rkey_all_fun_inclusive)),
        FragmentTags.Weather.value to ToolbarSettings(showToolbar = true, title = HotelXcaretApp.mContext.getString(R.string.rkey_riviera_maya), backBtnColor = R.color.colorWeatherBack, textColor = R.color.colorWeatherPrimary),
        FragmentTags.BuldingDetail.value to ToolbarSettings(showToolbar = true, title = HotelXcaretApp.mContext.getString(R.string.rkey_casas_title)),
        FragmentTags.RoomList.value to ToolbarSettings(showToolbar = true, title = HotelXcaretApp.mContext.getString(R.string.rkey_room_title)),
        FragmentTags.RoomDetail.value to ToolbarSettings(showToolbar = true, title = HotelXcaretApp.mContext.getString(R.string.rkey_room_title)),
        FragmentTags.RestaurantList.value to ToolbarSettings(showToolbar = true, title = HotelXcaretApp.mContext.getString(R.string.rkey_restaurants_title)),
        FragmentTags.PoolDetailFragment.value to ToolbarSettings(showToolbar = true, title = HotelXcaretApp.mContext.getString(R.string.rkey_pools)),
        FragmentTags.RestaurantDetail.value to ToolbarSettings(showToolbar = true, title = HotelXcaretApp.mContext.getString(R.string.rkey_restaurants_title)),
        FragmentTags.AboutHotel.value to ToolbarSettings(showToolbar = true, title = HotelXcaretApp.mContext.getString(R.string.rkey_about)),
        FragmentTags.QuoteFragment.value to ToolbarSettings(false, title = HotelXcaretApp.mContext.getString(R.string.rkey_quotes_title), bgResource = R.color.colorBackground1),
        FragmentTags.MyReservations.value to ToolbarSettings(false, title = HotelXcaretApp.mContext.getString(R.string.rkey_quotes_title), bgResource = R.color.colorBackground1),
        FragmentTags.Login.value to ToolbarSettings(showToolbar = false, showBottomNav = false),
        FragmentTags.SignUp.value to ToolbarSettings(showToolbar = false, showBottomNav = false),
        FragmentTags.BuyerDataFragment.value to ToolbarSettings(showToolbar = false, showBottomNav = false),
        FragmentTags.PickupFragment.value to ToolbarSettings(showToolbar = false, showBottomNav = false),
        FragmentTags.PaymentFragment.value to ToolbarSettings(showToolbar = false, showBottomNav = false),
        FragmentTags.Profile.value to ToolbarSettings(showToolbar = false, showBottomNav = false),
        FragmentTags.WorkShopList.value to ToolbarSettings(showToolbar = true,title = HotelXcaretApp.mContext.getString(R.string.rkey_lbl_title_workshop), showBottomNav = false),
        FragmentTags.WorkShopDetail.value to ToolbarSettings(showToolbar = true, title = HotelXcaretApp.mContext.getString(R.string.rkey_lbl_title_workshop), showBottomNav = false),
        FragmentTags.Faq.value to ToolbarSettings(showToolbar = false, title = HotelXcaretApp.mContext.getString(R.string.rkey_frecuently_asked_question), showBottomNav = false),
        FragmentTags.FaqsDetail.value to ToolbarSettings(showToolbar = false, title = HotelXcaretApp.mContext.getString(R.string.rkey_about_my_reservation), showBottomNav = false),
        FragmentTags.EventList.value to ToolbarSettings(showToolbar = true, title = HotelXcaretApp.mContext.getString(R.string.rkey_lbl_title_event), showBottomNav = false),
        FragmentTags.EventDetail.value to ToolbarSettings(showToolbar = true, title = HotelXcaretApp.mContext.getString(R.string.rkey_lbl_detail_event), showBottomNav = false)
    )
}

class ToolbarSettings(
    val showToolbar: Boolean = false,
    val showBackBtn: Boolean = true,
    val bgResource: Int = R.color.colorPlecaTransparent,
    val backBtnColor: Int = R.color.colorBtnBack,
    val textColor: Int = R.color.colorToolbar,
    val title: String = "",
    var showBottomNav: Boolean = false
) {
    override fun toString(): String {
        return "toolbarSettings: {" +
                "showToolbar: $showToolbar " +
                "showBackBtn: $showBackBtn" +
                "showBottomNav: $showBottomNav" +
                "}"
    }
}