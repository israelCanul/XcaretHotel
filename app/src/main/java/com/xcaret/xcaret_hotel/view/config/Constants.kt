package com.xcaret.xcaret_hotel.view.config

import io.card.payment.CreditCard

object Constants {




    //Pagination
    val QUERY_SIZE_PAGE = 20

    //hotels code
    const val HOTEL_XCARTE_MEXICO = "HXM"
    const val HOTEL_XCARET_MEXICO = "Hotel Xcaret Mexico"
    const val HOTEL_XCARET_ARTE = "Hotel Xcaret Arte"

    //categories code
    const val CATEGORY_HOTEL = "HOTEL"
    const val CATEGORY_ROOM_CODE = "ROOMS"
    const val CATEGORY_REST_CODE = "REST"
    const val CATEGORY_BUILDING_CODE = "BUILDING"
    const val CATEGORY_REST_BAR_SNACK = "REST_BAR_SNACK"
    const val CATEGORY_COFFE_SHOP = "COFFE_SHOP"
    const val CATEGORY_AFI_CODE = "AFI"
    const val CATEGORY_CALLUS_CODE = "CALL_CENTER_RESERVATION"
    const val CATEGORY_VIDEO_LIVE = "VIDEO_LIVE"
    const val CATEGORY_WORKSHOP = "TALLERES"
    const val CATEGORY_EVENTS = "EVENTOS"

    enum class DaysWeek(val value: Int) {
        LUNES(1),
        MARTES(2),
        MIERCOLES(3),
        JUEVES(4),
        VIERNES(5),
        SABADO(6),
        DOMINGO(7),
    }

    //list categories with detail view
    val LIST_CATEGORY_HOMES =
        listOf(
            "BUILDING_WATER",
            "BUILDING_GROUND",
            "BUILDING_WIND",
            "BUILDING_SPIRAL",
            "BUILDING_FIRE"
        )

    val LIST_CATEGORY_ONLY_REST =
        listOf(
            "REST",
            "REST_MEX",
            "REST_INTER"
        )

    val LIST_CATEGORY_REST_AND_BAR =
        listOf(
            "REST",
            "REST_MEX",
            "REST_INTER",
            "REST_BAR_SNACK"
        )

    val LIST_CATEGORY_POOLS =
        listOf(
            "POOL"
        )

    //amenities code
    const val AMENITY_ROOM_MAIN = "AMENITY_ROOM_MAIN"
    const val VIDEO_AFI_CODE = "video_afi"
    const val AMENITY_VIEW_CODE = "view"
    const val AMENITY_CAPACITY = "capacity"
    const val AMENITY_SIZE = "size"
    const val FAQ_CODE = "FAQ"

    //arguments key
    const val CAT_UID = "CatUID"
    const val HOTEL_UID = "HotelUID"
    const val REST_UID = "RestUID"
    const val POOL_UID = "PoolUID"
    const val ROOM_UID = "RoomUID"
    const val PARK_UID = "ParkUID"
    const val ACT_UID = "ActUID"
    const val LEGAL_TYPE = "LEGAL_TYPE"
    const val VIDEO_ID = "VideoId"
    const val PLACE_UID = "PlaceUID"
    const val CALLING_FROM_MAP = "CallingFromMap"
    const val REDIRECT_DESTINATION_ID = "RedirectDestinationId"
    const val DROP_DOWN_SELECTED = "DROP_DOWN_TYPE"
    const val DROP_DOWN_COUNTRY_ID = "DROP_DOWN_COUNTRY_ID"
    const val CLEAR_STACK = "CLEAR_STACK"
    const val IS_VISTOR = "IS_VISITOR"
    const val AIRLINE_CODE = "AIRLINE_CODE"
    const val AIRLINE_TERMINAL_CODE = "AIRLINE_TERMINAL_CODE"
    const val BEFORE_DESTINATION = "BEFORE_DESTINATION"
    const val CATEGORY_FAQ_ID = "CategoryFAQID"
    const val CATEGORY_FAQ_TITLE = "CategoryFAQTitle"

    const val TERMS_CONDITON = 0
    const val NOTICE_PRIVACY = 1

    //param settings key
    const val DAYS_REMEMBER_INCOMPLETE_PROFILE = "days_remember_incomplete_profile"
    const val CHANNEL_ID = "channel_id"
    const val CLIENT_ID = "client_id"
    const val PUBLIC_KEY = "public_key_rsa"

    //extras
    const val URL_PLAY_STORE = "https://play.google.com/store/apps/details?id="
    const val URL_MARKET = "market://details?id="
    const val PACKAGE_PARKS_APP = "com.xcaret.xcaret_parques"
    const val CONTACT_WRITE_US_CODE = "contact_general_email_hxm"
    const val PLATFORM = "ANDROID"
    const val AFI_RES = "afi_"

    //filter type
    const val ROOT = "root"
    const val BRANCH = "leaf"
    const val FILTER_MAP_REST_CODE = "restaurant"
    const val FILTER_MAP_ALL = "all"
    const val FILTER_MAP_SINLGE = "single"

    //place type
    const val PLACE = "place"
    const val SERVICE = "service"

    //house codes
    const val CASAS_AGUA = "BUILDING_WATER"
    const val CASA_TIERRA = "BUILDING_GROUND"
    const val CASA_VIENTO = "BUILDING_WIND"
    const val CASA_ESPIRAL = "BUILDING_SPIRAL"
    const val CASA_FUEGO = "BUILDING_FIRE"

    const val CASA_ARTISTAS = "BUILDING_ARTIST"
    const val CASA_DISENO = "BUILDING_DESIGN"
    const val CASA_PATRON = "BUILDING_PATRON"
    const val CASA_PAZ = "BUILDING_PEACE"
    const val CASA_PIRAMID = "BUILDING_PIRAMID"
    const val CASA_MUSICA = "BUILDING_MUSICIAN"

    const val MENU_HOME = "menu_home"
    const val MENU_MAP = "menu_map"
    const val MENU_AFI = "menu_afi"
    const val MENU_PROFILE = "menu_profile"

    const val PATH_PARK_TOUR = "park_tour"
    const val PATH_DESTINATION = "destination"
    const val PATH_DESTINATION_ACTIVITY = "destination_activity"

    //request permissions
    const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000
    const val SCAN_REQUEST_CODE = 1001
    const val REQUEST_UPDATE = 1002

    //conserve colors icon
    val listConserveColor = arrayOf(
        "medical_service"
    )

    //security remove
    const val USER_DEFAULT = "cognitouserapp@xcaret.com"
    const val PASS_DEFAULT = "DsV4iQ@yCYjKNZ!"

    //quotes
    const val ADULTS = "ADULTS"
    const val CHILDREN = "CHILDREN"
    const val AGE = "AGE"
    const val START_DATE = "START_DATE"
    const val END_DATE = "END_DATE"
    const val REQUIRED = "REQUIRED"
    const val SEPARATOR_RULES = "|"
    const val SEPARATOR_PAX_RULES = "/"
    const val MAX_AGE_FOR_CHILD = "max_age_for_child"
    const val SHOW_CALENDAR = "SHOW_CALENDAR"
    const val PRE_SELECT_ROOM = "PRE_SELECT_ROOM"
    const val MAX_YEARS_QUOTES = "max_years_quotes"

    //profile
    const val LANGUAGE_SF_DEFAULT = "Inglés"
    const val COMO_SE_ENTERO_DEFAULT = "Aplicación Hoteles"
    const val PROFILE_PATH = "profile/"
    const val PROFILE_EXT = ".jpeg"

    //param settings
    const val cog_user_sf_contact = "cog_user_sf_contact"
    const val cog_pass_sf_contact = "cog_pass_sf_contact"
    const val cog_user_sf_user = "cog_pass_sf_user"
    const val cog_user_sf_password = "cog_pass_sf_password"
    const val base_url_cognito_auth = "base_url_cognito_auth"
    const val api_mop_url = "api_mop_url"
    const val api_mop_user = "api_mop_user"
    const val api_mop_pass = "api_mop_pass"
    const val api_mop_token = "api_mop_token"
    const val sf_contacts = "sf_contacts"
    const val synxis_availabity = "synxis_availabity"
    const val api_itinerary_url = "api_itinerary_url"
    const val channel_id = "channel_id"
    const val client_id = "client_id"
    const val max_room_quotes = "max_room_quotes"
    const val sf_booking_engine = "sf_booking_engine"
    const val bin_info_url = "bin_info_url"
    const val show_declined_after = "show_declined_after"
    const val years_historical_reserves = "years_historical_reserves"
    const val api_tickets = "api_tickets"
    const val user_tickets = "user_tickets"
    const val password_tickets = "password_tickets"
    const val mobile = "mobile"

    const val api_itinerary_details = "api_itinerary_details"
    const val api_itinerary_balance = "api_itinerary_balance"

    //lottie anim
    const val LOADING = 59
    const val SUCCESS = 89
    const val ERROR = 120

    //Drawable
    const val DEFAULT_CARD_BACKGROUND = "bg"

    // Card Codes
    const val CREDIT_CARD = "CREDIT"
    const val DEBIT_CARD = "DEBIT"

    //numbers
    const val MILES_DEFAULT = ","
    const val DECIMAL_DEFAULT = "."
    const val SYMBOL_DEFAULT = "$"

    //Status Cognito
    const val CONFIRMED ="CONFIRMED"
    const val USER_DOESNT_EXIST ="El Usuario no existe"
    const val UNCONFIRMED ="UNCONFIRMED"

    const val ORIGINAL = "ÁáÉéÍíÓóÚúÑñÜü";
    const val REPLACEMENT = "AaEeIiOoUuNnUu";

    //MarketingCloudAttributes
    const val MktCloud_City = "City"
    const val MktCloud_Country = "Pais"
    const val MktCloud_ZipCode = "ZipCode"
    const val MktCloud_Device = "Device"
    const val MktCloud_Email = "Email"
    const val MktCloud_FirstName = "FirstName"
    const val MktCloud_DeviceLanguage = "Device Language"
    const val MktCloud_LastName = "LastName"
    const val MktCloud_FullName = "NombreCompleto"
    const val MktCloud_PhoneNumber = "PhoneNumber"
    const val MktCloud_State = "State"
    const val MktCloud_Title = "Title"
    const val MktCloud_Origin = "Origen"
    const val MktCloud_Address = "Address"

    //Server Codes
    const val BAD_REQUEST = 400
    const val NOT_FOUND = 404
    const val REQUEST_TIMEOUT = 408
    const val INTERNAL_SERVER_ERROR = 500
    const val SERVICE_UNAVAILABLE = 503
    const val GATEWAY_TIMEOUT = 504
    const val SERVICE_OFFLINE = 1
    const val CARD_NOT_FOUND = 2
    const val NOT_APPLIED_ERROR =0


    //AFI CLASS CODE
    const val AFICLASS_CODE_FERRY = "fer"
    //AFI TYPE
    const val AFICLASS_TYPE_SRV = "SRV"
    const val AFICLASS_TYPE_BNF = "BNF"
    const val AFICLASS_TYPE_ATT = "ATT"

    //SNACK TYPE
    const val SNACK_INFO = 0
    const val SNACK_ERROR = 1
    const val SNACK_SUCCES = 2
    const val SNACK_DEFAULT = 3
    //SNACK TYPE
    const val IS_ATTEMPT_ENABLED = true

    //Translator
    const val xkynetAPICode ="xkynet_api_app_code_hotel"






}