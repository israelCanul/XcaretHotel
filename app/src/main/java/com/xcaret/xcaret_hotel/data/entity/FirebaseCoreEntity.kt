package com.xcaret.xcaret_hotel.data.entity

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ActivityEntity(
    val uid: String? = null,
    val capacity: String? = null,
    val categoryUID: String? = null,
    val code: String? = null,
    val duration: String? = null,
    val enabled: Int? = null,
    val hotelUID: String? = null,
    val icon: String? = null,
    val placeUID: String? = null
)

@IgnoreExtraProperties
data class HotelEntity(
    var uid: String? = null,
    val name: String? = null,
    val code: String? = null,
    val enabled: Int = 0,
    val idSynxis: String? = null,
    val logo: String? = null,
    var paxDefault: String? = null,
    var paxRules: String? = null,
    var bookingActive: Int? = null,
    var baseColor: String? = null,
    var baseColorDark: String? = null,
    var order: Int? = 0,
    val minimumAgeChildren:Int? = 0,
    val min_night:Int? = 0,
    val max_night:Int? = 0

)

@IgnoreExtraProperties
data class ParkTourEntity(
    val name: String? = null,
    val categoryUID: String? = null,
    val sivexCode: String? = null,
    val code: String? = null,
    val enabled: Int = 0,
    val isPark: Int = 0,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val needReservation: Int = 0,
    val tags: String? = null,
    val order: Int = 0,
    val classUID:String? = null
)

@IgnoreExtraProperties
data class PlaceEntity(
    val categoryUID: String? = null,
    val contactCategoryUID: String? = null,
    val code: String? = null,
    val enabled: Int = 0,
    val extra: String? = null,
    val hotelUID: String? = null,
    val iconMap: String? = null,
    val level: String? = null,
    val colorMarkerMap: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val parentUID: String? = null,
    val order: Int  = 0,
    val weather: Int = 0,
    val type: String? = null
)

@IgnoreExtraProperties
data class RoomTypeEntity(
    val code: String? = null,
    val codeSynxis: String? = null,
    val hotelUID: String? = null,
    val categoryUID: String? = null,
    val enabled: Int = 0,
    val order: Int? = null,
    val tour360: String? = null,
    val gallery :Map<String,GalleryItemEntity>? = null
)

data class GalleryItemEntity(
    val isMain: Int? = 0,
    val name: String? = null,
    val order: Int? = null,
    val path: String? = null,
    val enabled: Int? = 0
)

@IgnoreExtraProperties
data class WebCamEntity(
    val name: String? = null,
    val image: String? = null,
    val categoryUID: String? = null,
    val url: String? = null,
    val videoId: String? = null,
    val order: Int = 0,
    val code: String? = null,
    val enabled: Int = 0
)