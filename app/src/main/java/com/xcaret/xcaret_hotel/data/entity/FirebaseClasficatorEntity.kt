package com.xcaret.xcaret_hotel.data.entity

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class AirlinesEntity(
    val uid: String? = null,
    val name: String? = null,
    val code: String? = null,
    val enabled: Int = 0,
    val order: Int = 0
)

@IgnoreExtraProperties
data class AirlineTerminalEntity(
    val uid: String? = null,
    val code: String? = null,
    val number: String? = null,
    val order: Int? = null,
    val enabled: Int? = null
)

@IgnoreExtraProperties
data class AwardEntity(
    val parkUID: String? = null,
    val placeUID: String? = null,
    val order: Int = 0,
    val icon: String? = null,
    val enabled: Int = 0
)

@IgnoreExtraProperties
data class CategoryEntity(
    val uid: String? = null,
    val hotelUID: String? = null,
    val code: String? = null,
    val icon: String? = null,
    val showInHome: Int = 0,
    val priority: Int? = null,
    val filterGrouper: String? = null,
    val enabled: Int? = 0
)

@IgnoreExtraProperties
data class FaqEntity(
    val uid: String? = null,
    val hotelUID: String? = null,
    val code: String? = "",
    val enabled: Int? = 0,
    val order: Int? = 0,
    val categoryUID:String? = null
)

@IgnoreExtraProperties
data class ContactEntity(
    val categoryUID: String? = null,
    val hotelUID: String? = null,
    val code: String? = null,
    val icon: String? = null,
    val type: String? = null,
    val value: String? = null,
    val order: Int = 0,
    val enabled: Int = 0
)

@IgnoreExtraProperties
data class CountryEntity(
    val id: Int? = null,
    val continent: String? = null,
    val iSO: String? = null,
    val iSO2: String? = null,
    val name: String? = null,
    val region: String? = null,
    val states: ArrayList<StateEntity>? = null
)

@IgnoreExtraProperties
data class CurrencyEntity(
    val id: Int? = null,
    val iso: String? = null,
    val name: String? = null,
    val symbol: String? = null,
    val enabled: Int? = null,
    val icon: String? = null,
    val miles: String? = null,
    val decimal: String? = null,
    val isoCountry: String? = null,
    val isoPayment:String? = null

)

@IgnoreExtraProperties
data class StateEntity(
    val id: Int? = null,
    val abbreviation: String? = null,
    val name: String? = null
)

@IgnoreExtraProperties
data class FilterMapEntity(
    val categoryUID: String? = null,
    val hotelUID: String? = null,
    val parentUID: String? = null,
    val order: Int = 0,
    val type: String? = null,
    val code: String? = null,
    val icon: String? = null,
    val enabled: Int = 0
)

@IgnoreExtraProperties
data class HouseIdEntity(
    val buildingUID: String? = null,
    val id: Int = 0
)

@IgnoreExtraProperties
data class LanguageEntity(
    val uid: String? = null,
    val code: String? = null,
    val countryCode: String? = null,
    val name: String? = null,
    val nameSF: String? = null,
    val icon: String? = null,
    val enabled: Int = 0,
    val translating: Int = 0
)

@IgnoreExtraProperties
data class LevelRoomEntity(
    val buildingUID: String? = null,
    val houseId: Int = 0,
    val level: Int = 0,
    val number: String? = null,
    val isSpecial: Int = 0,
    val enabled: Int = 0
)

@IgnoreExtraProperties
data class MapConfigEntity(
    val bound1Lat: Double? = null,
    val bound1Lon: Double? = null,
    val bound2Lat: Double? = null,
    val bound2Lon: Double? = null,
    val defaultZoom: Double? = null,
    val hotelUID: String? = null,
    val imgOverlay: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val maxZoom: Double? = null,
    val minZoom: Double? = null,
    val overlay1Lat: Double? = null,
    val overlay1Lon: Double? = null,
    val overlay2Lat: Double? = null,
    val overlay2Lon: Double? = null,
    val platform: String? = null,
    val radiusLimit: Double? = null
)

@IgnoreExtraProperties
data class ParamSettingEntity(
    val code: String? = null,
    val value: String? = null
)

@IgnoreExtraProperties
data class PhoneCodeEntity(
    val iso: String? = null,
    val iso2: String? = null,
    val id: Int? = null,
    val name: String? = null,
    val code: String? = null
)

@IgnoreExtraProperties
data class NearPlaceEntity(
    val placeUID: String? = null,
    val nearPlaceUID: String? = null,
    val order: Int = 0
)

@IgnoreExtraProperties
data class RoomAmenityEntity(
    val categoryUID: String? = null,
    val code: String? = null,
    val icon: String? = null,
    val roomTypeUID: String? = null,
    val enabled: Int = 0,
    val order: Int? = null
)

@IgnoreExtraProperties
data class RoomLocationEntity(
    val placeUID: String? = null,
    val roomUID: String? = null,
    val order: Int? = 0
)

@IgnoreExtraProperties
data class ScheduleActivityEntity(
    val activityUID: String? = null,
    val day: String? = null,
    val enabled: Int? = 0,
    val hourEnd: String? = null,
    val hourStart: String? = null
)

@IgnoreExtraProperties
data class AFIClassEntity(
    val afiClassUID: String? = null,
    val code: String? = null,
    val enabled: Int? = 0,
    val icon: String? = null,
    val order: Int? = null
)

@IgnoreExtraProperties
data class DestinationEntity(
    val destinationUID: String? = null,
    val code: String? = null,
    val image: String? = null,
    val order: Int? = null,
    val status: Int? = 0
)

@IgnoreExtraProperties
data class DestinationActivityEntity(
    val destinationActivityUID: String? = null,
    val code: String? = null,
    val destinationUID: String? = null,
    val enabled: Int? = 0,
    val image: String? = null,
    val order: Int? = 0,
    val type: String? = null
)
