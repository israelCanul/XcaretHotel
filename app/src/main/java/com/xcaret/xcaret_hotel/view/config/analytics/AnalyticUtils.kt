package com.xcaret.xcaret_hotel.view.config.analytics

object AnalyticConstant{
    const val KEY_PROVIDER = "provider"
    const val KEY_STATUS = "status"
    const val KEY_HOTEL_FB = "hotel"
    //////////////////KEYS FOR Fire Base //////////////////
    const val ID_WEATHERHOME ="id_weatherHome"
    const val ID_HOME_ABOUT ="id_home_about_xx"
    const val ID_WEBCAM_CODE ="id_webcam_code_xx"
    const val ID_NAV_MAP ="id_nav_map"
    const val ID_NAV_AFI ="id_nav_afi"
    const val ID_NAV_MENU ="id_nav_menu"
    const val ID_RESERVATION_CODE ="id_xx_reservation_code"
    const val ID_CALLFROM_CODE ="id_xx_callFrom_code"
    const val ID_VIDEO_CODE="id_video_code"
    const val ID_PARK_CODE ="id_park_code"
    const val ID_MENU_LANGUAGE ="id_menu_language"
    const val ID_MENU_CALLNOW ="id_menu_callNow"
    const val ID_MENU_EMAIL ="id_menu_email"
    const val ID_MENU_REVIEW ="id_menu_review"
    const val ID_MENU_ABOUT ="id_menu_about_xx"
    const val ID_MENU_APP_XCARET ="id_menu_appXcaret"
    const val ID_MENU_TERMS ="id_menu_terms"
    const val ID_MENU_NOTICE_PRIVACY ="id_menu_noticePrivacy"
    const val ID_MENU_THEME ="id_menu_theme"
    const val ID_MENU_PROFILE ="id_menu_profile"
    const val ID_MENU_LOGOUT ="id_menu_logout"
    const val ID_MENU_RESERVATIONS ="id_menu_reservations"
    const val ID_PROFILE_EDIT ="id_profiel_edit"
    const val ID_PROFILE_SAVE ="id_profile_save"

    const val ITEM_NAME_WEATHER_HOME ="weatherHome"
    const val ITEM_NAME_HOME_ABOUT="home_about_xx"
    const val ITEM_NAME_WEBCAM_CODE ="webcam_code_xx"
    const val ITEM_NAME_NAV_MAP ="nav_map"
    const val ITEM_NAME_NAV_AFI ="nav_afi"
    const val ITEM_NAME_NAV_MENU ="nav_menu"
    const val ITEM_NAME_RESERVATION_CODE ="xx_reservation_code"
    const val ITEM_NAME_CALL_FORM_CODE ="xx_callFrom_code"
    const val ITEM_NAME_VIDE_CODE ="vide_code"
    const val ITEM_NAME_PARK_CODE ="park_code"
    const val ITEM_NAME_MENU_LANGUAGE ="menu_language"
    const val ITEM_NAME_MENU_CALLNOW ="menu_callNow"
    const val ITEM_NAME_MENU_EMAIL ="menu_email"
    const val ITEM_NAME_MENU_REVIEW ="menu_review"
    const val ITEM_NAME_MENU_ABOUT ="menu_about_xx"
    const val ITEM_NAME_MENU_APP_XCARET ="menu_appXcaret"
    const val ITEM_NAME_MENU_TERMS ="menu_terms"
    const val ITEM_NAME_MENU_NOTICE_PRIVACY ="menu_noticePrivacy"
    const val ITEM_NAME_MENU_THEME ="menu_theme"
    const val ITEM_NAME_MENU_PROFILE ="menu_profile"
    const val ITEM_NAME_MENU_LOGOUT ="menu_logout"
    const val ITEM_NAME_MENU_RESERVATIONS ="menu_reservations"
    const val ITEM_NAME_PROFILE_EDIT ="profile_edit"
    const val ITEM_NAME_PROFILE_SAVE ="profile_save"

    const val CONTENT_TYPE_HOME ="home"
    const val CONTENT_TYPE_WEBCAM ="webcam"
    const val CONTENT_TYPE_NAVTABBAR ="navTabBar"
    const val CONTENT_TYPE_GASTRONOMY ="gastronomy"
    const val CONTENT_TYPE_CALLNOW ="callNow"
    const val CONTENT_TYPE_AFI ="afi"
    const val CONTENT_TYPE_AFIPARKS ="afiParks"
    const val CONTENT_TYPE_MENU ="menu"
    const val CONTENT_TYPE_PROFILE ="profile"


    //////////////////KEYS FOR FB //////////////////
    const val KEY_CHECKIN_DATE_FB = "fb_checkin_date"
    const val KEY_CHECKOUT_DATE_FB = "fb_checkout_date"
    const val KEY_CITY_FB = "fb_checkout_date"
    const val KEY_REGION_FB = "fb_region"
    const val KEY_COUNTRY_FB = "fb_country"
    const val KEY_NUM_ADULTS_FB = "fb_num_adults"
    const val KEY_NUM_CHILDREN_FB = "fb_num_children"
    const val KEY_ITEM_CATEGORY_FB = "fb_item_category"
    const val KEY_CURRENCY_FB = "fb_currency"
    const val KEY_QUANTITY_FB = "fb_quantity"
    const val KEY_SUITE_NAME_FB = "fb_suite_name"
    const val KEY_AMOUNT_FB = "fb_amount"




}

enum class AnalyticType {
    DEFAULT,
    BEGIN_CHECKOUT,
    PURCHASE,
    ADD_TO_CART,
    LOGIN,
    SIGN_UP,
    VIEW_CONTENT_FACEBOOK,
    SEARCH_FACEBOOK,
    INITIATE_CHECKOUT_FACEBOOK,
    PURCHASE_FACEBOOK,
    SEARCH
}