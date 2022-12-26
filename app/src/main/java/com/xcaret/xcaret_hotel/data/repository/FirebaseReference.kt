package com.xcaret.xcaret_hotel.data.repository

import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.view.config.Language

object FirebaseReference {
    private const val ROOT_CLASIFICATOR = "clasificator"
    private const val ROOT_CORE = "core"
    const val ROOT_LANG = "lang/"

    //region clasificators
    const val CATEGORY = "$ROOT_CLASIFICATOR/category"
    const val CONTACT = "$ROOT_CLASIFICATOR/contact"
    const val FILTER_MAP = "$ROOT_CLASIFICATOR/filters_map"
    const val HOUSE_ID = "$ROOT_CLASIFICATOR/house_id"
    const val LANGUAGE = "$ROOT_CLASIFICATOR/language"
    const val LEVEL_ROOM = "$ROOT_CLASIFICATOR/level_room"
    const val MAPCONFIG = "$ROOT_CLASIFICATOR/mapconfig"
    const val ROOM_AMENITY = "$ROOT_CLASIFICATOR/room_amenity"
    const val ROOM_LOCATION = "$ROOT_CLASIFICATOR/room_location"
    const val NEAR_PLACE = "$ROOT_CLASIFICATOR/near_place"
    const val AWARD = "$ROOT_CLASIFICATOR/awards"
    const val TITLE = "$ROOT_CLASIFICATOR/title"
    const val COUNTRY = "$ROOT_CLASIFICATOR/country"
    const val CURRENCY = "$ROOT_CLASIFICATOR/currency"
    const val AIRLINES = "$ROOT_CLASIFICATOR/airlines"
    const val PARAM_SETTING = "$ROOT_CLASIFICATOR/param_settings"
    const val PHONE_CODE = "$ROOT_CLASIFICATOR/phone_code"
    const val SCHEDULE_ACTIVITY = "$ROOT_CLASIFICATOR/schedule_activity"
    const val AIRLINE_TERMINAL = "$ROOT_CLASIFICATOR/airline_terminal"
    const val FAQ = "$ROOT_CLASIFICATOR/faq"
    const val DESTINATION = "$ROOT_CLASIFICATOR/destination"
    const val AFICLASS = "$ROOT_CLASIFICATOR/afi_class"
    const val DESTINATION_ACTIVITY = "$ROOT_CLASIFICATOR/destination_activity"
    //endregion

    //region core
    const val ACTIVITY = "$ROOT_CORE/activity"
    const val HOTEL = "$ROOT_CORE/hotel"
    const val PARK_TOUR = "$ROOT_CORE/park_tour"
    const val PLACE = "$ROOT_CORE/place"
    const val ROOM_TYPE = "$ROOT_CORE/room_type"
    const val RESTAURANT_DETAIL = "$ROOT_CORE/restaurant_detail"
    const val WEATHER = "$ROOT_CORE/weather"
    const val WEBCAM = "$ROOT_CORE/webcam"
    //endregion

    //region lang
    const val LABEL = "label"
    //endregion

    //region security
    const val FB_Security = "https://hotelxcaretmexico-security.firebaseio.com"
    const val VISITORS = "visitors"
    //endregion

    //region reservation
    const val RESERVATIONS = "reservations"
    //endregion
}