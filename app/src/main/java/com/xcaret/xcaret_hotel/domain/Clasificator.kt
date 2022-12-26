package com.xcaret.xcaret_hotel.domain

import androidx.room.*
import com.google.firebase.database.IgnoreExtraProperties
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.ListItemViewModel
import com.xcaret.xcaret_hotel.view.config.normalize

@Entity(indices = [(Index("code", "enabled"))])
data class Airline(
    @PrimaryKey var uid: String = "",
    var name: String? = null,
    var code: String? = null,
    var enabled: Boolean = false,
    var order: Int = 0,
    @Ignore var is_selected: Boolean = false
): ListItemViewModel()

@Entity
data class AirlineTerminal(
    @PrimaryKey var uid: String = "",
    var number: String = "",
    var code: String = "",
    var order: Int = 0,
    var enabled: Boolean = false,
    @Ignore var isSelected: Boolean = false,
    @Ignore var value: String = ""
): ListItemViewModel()


@Entity(indices = [(Index("park_uid", "place_uid"))])
data class Award(
    @PrimaryKey var uid: String = "",

    @ColumnInfo(name = "park_uid")
    var parkUID: String? = null,

    @ColumnInfo(name = "place_uid")
    var placeUID: String? = null,
    var order: Int = 0,
    var icon: String? = null,
    var enabled: Boolean = false
): ListItemViewModel()

@Entity(indices = [(Index("hotel_uid", "code", "filter_grouper"))])
data class Category(
    @PrimaryKey var uid: String = "",

    @ColumnInfo(name = "hotel_uid")
    var hotelUID: String? = null,
    var code: String? = null,
    var icon: String? = null,
    var enabled: Boolean? =false,

    @ColumnInfo(name = "filter_grouper")
    var filterGrouper: String? = null,

    @ColumnInfo(name = "show_in_home")
    var showInHome: Boolean = false,
    var priority: Int = 0,
    @Embedded var lang: LangCategory? = null,
    @Ignore var selected: Boolean = false,
    @Ignore val topGuideLine: Float = 0.25f,
    @Ignore val bottomGuideLine: Float = 0.75f,
    @Ignore val topGuideLineSelected: Float = 0.15f,
    @Ignore val bottomGuideLineSelected: Float = 0.85f,
    @Ignore var colorBackground: Int = 0
): ListItemViewModel()

@Entity(indices = [(Index("hotel_uid", "code","category_uid"))])
data class Faq(
    @PrimaryKey var uid: String = "",

    @ColumnInfo(name = "hotel_uid")
    var hotelUID: String? = null,
    @ColumnInfo(name = "category_uid")
    var categoryUID: String? = null,
    var order: Int? = 0,
    var code: String? = null,
    var enabled: Int? = 0,

    @Embedded var lang: LangFaq? = null,
    @Ignore var showQuestion: Boolean = false

): ListItemViewModel()

@Entity(indices = [(Index("category_uid", "code", "hotel_uid"))])
data class Contact(
    @PrimaryKey var uid: String = "",

    @ColumnInfo(name = "category_uid")
    var categoryUID: String? = null,

    @ColumnInfo(name = "hotel_uid")
    var hotelUID: String? = null,
    var code: String? = null,
    var icon: String? = null,
    var type: String? = null,
    var value: String? = null,
    var order: Int = 0,
    var enabled: Boolean = false,
    @Embedded var lang: LangContact? = null
): ListItemViewModel()

@Entity
data class Country(
    @PrimaryKey(autoGenerate = true)
    var primary_key: Int = 0,

    var id: Int? = null,
    var continent: String? = null,

    @ColumnInfo(name = "iso")
    var iSO: String? = null,

    @ColumnInfo(name = "iso2")
    var iSO2: String? = null,
    var name: String? = null,
    var region: String? = null,
    @Ignore var name_normalized: String? = null,
    @Ignore var is_selected: Boolean = false,
    @Ignore var states: MutableList<State> = mutableListOf()
): ListItemViewModel()

@Entity
data class Currency(
    @PrimaryKey
    var id: Int = 0,
    var iso: String? = null,
    var name: String? = null,
    var symbol: String? = null,
    var enabled: Boolean = false,
    var icon: String? = null,
    var is_selected: Boolean = false,
    var miles: String? = null,
    val decimal: String? = null,
    val isoCountry :String? = null,
    val isoPayment:String? = null
): ListItemViewModel()

@Entity
data class State(
    @PrimaryKey
    var id: Int? = null,
    var countryId: Int? = null,
    var abbreviation: String? = null,
    var name: String? = null,
    @Ignore var name_normalized: String? = name?.normalize(),
    @Ignore var is_selected: Boolean = false
): ListItemViewModel()

@Entity(indices = [(Index("category_uid", "place_uid", "hotel_uid", "code"))])
data class FilterMap(
    @PrimaryKey var uid: String = "",

    @ColumnInfo(name = "category_uid")
    var categoryUID: String? = null,

    @ColumnInfo(name = "hotel_uid")
    var hotelUID: String? = null,

    @ColumnInfo(name = "place_uid")
    var parentUID: String? = null,
    var order: Int = 0,
    var type: String? = null,
    var code: String? = null,
    var icon: String? = null,
    var enabled: Boolean = false,
    @Embedded var lang: LangFilterMap? = null,
    @Ignore val childs: MutableList<FilterMap> = mutableListOf()
)

@Entity(indices = [(Index("building_uid"))])
data class HouseId(
    @PrimaryKey var uid: String = "",

    @ColumnInfo(name = "building_uid")
    var buildingUID: String? = null,
    var id: Int = 0
)

@Entity(indices = [(Index("code"))])
data class Language(
    @PrimaryKey var uid: String = "",
    var code: String? = null,

    @ColumnInfo(name = "country_code")
    var countryCode: String? = null,
    var enabled: Boolean = false,
    var name: String? = null,

    @ColumnInfo(name = "name_sf")
    var nameSF: String = Constants.LANGUAGE_SF_DEFAULT,
    var icon: String? = null,
    var isTranslated: Boolean? = null,
    @Ignore var selected: Boolean = false
): ListItemViewModel()

@Entity
data class LevelRoom(
    @PrimaryKey var uid: String = "",

    @ColumnInfo(name = "building_uid")
    var buildingUID: String? = null,

    @ColumnInfo(name = "house_id")
    var houseId: Int = 0,
    var level: Int = 0,
    var number: String? = null,

    @ColumnInfo(name = "is_special")
    var isSpecial: Boolean = false,
    var enabled: Boolean = false
)

@Entity(indices = [(Index("hotel_uid", "platform"))])
data class MapConfig(
    @PrimaryKey var uid: String = "",

    @ColumnInfo(name = "bound1_lat")
    var bound1Lat: Double? = null,

    @ColumnInfo(name = "bound1_lon")
    var bound1Lon: Double? = null,

    @ColumnInfo(name = "bound2_lat")
    var bound2Lat: Double? = null,

    @ColumnInfo(name = "bound2_lon")
    var bound2Lon: Double? = null,

    @ColumnInfo(name = "default_zoom")
    var defaultZoom: Double? = null,

    @ColumnInfo(name = "hotel_uid")
    var hotelUID: String? = null,

    @ColumnInfo(name = "img_overlay")
    var imgOverlay: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,

    @ColumnInfo(name = "max_zoom")
    var maxZoom: Double? = null,

    @ColumnInfo(name = "min_zoom")
    var minZoom: Double? = null,

    @ColumnInfo(name = "overlay1_lat")
    var overlay1Lat: Double? = null,

    @ColumnInfo(name = "overlay1_lon")
    var overlay1Lon: Double? = null,

    @ColumnInfo(name = "overlay2_lat")
    var overlay2Lat: Double? = null,

    @ColumnInfo(name = "overlay2_lon")
    var overlay2Lon: Double? = null,
    var platform: String? = null,

    @ColumnInfo(name = "radius_limit")
    var radiusLimit: Double? = null
)

@Entity(indices = [(Index("n_place_uid", "n_near_place_uid"))])
data class NearPlace(
    @ColumnInfo(name = "n_uid")
    @PrimaryKey var uid: String = "",

    @ColumnInfo(name = "n_place_uid")
    var placeUID: String? = null,

    @ColumnInfo(name = "n_near_place_uid")
    var nearPlaceUID: String? = null,

    @ColumnInfo(name = "n_order")
    var order: Int = 0
)

@Entity(indices = [(Index("code"))])
data class ParamSetting(
    @PrimaryKey(autoGenerate = true)
    var primary_key: Int = 0,
    var code: String? = null,
    var value: String? = null
)

@Entity(indices = [(Index("iso", "iso2", "id"))])
data class PhoneCode(
    @PrimaryKey(autoGenerate = true)
    var primary_key: Int = 0,
    var iso: String? = null,
    var iso2: String? = null,
    var id: Int? = null,
    var name: String? = null,
    var code: String? = null
)

@Entity(indices = [(Index("category_uid", "room_type_uid"))])
data class RoomAmenity(
    @PrimaryKey var uid: String = "",

    @ColumnInfo(name = "category_uid")
    var categoryUID: String? = null,
    var code: String? = null,
    var icon: String? = null,

    @ColumnInfo(name = "room_type_uid")
    var roomTypeUID: String? = null,

    var enabled: Boolean = false,
    var order: Int = 0,
    @Embedded var lang: LangRoomAmenity? = null
): ListItemViewModel()

@Entity(indices = [(Index("place_uid", "room_uid"))])
data class RoomLocation(
    @ColumnInfo(name = "room_location_id")
    @PrimaryKey var uid: String = "",

    @ColumnInfo(name = "place_uid")
    var placeUID: String? = null,

    @ColumnInfo(name = "room_uid")
    var roomUID: String? = null,

    @ColumnInfo(name = "room_location_order")
    var order: Int? = 0
)

@Entity(indices = [(Index("activityUID", "day"))])
data class ScheduleActivity(
    @PrimaryKey var uid: String = "",
    var activityUID: String? = null,
    var day: String? = null,
    var enabled: Boolean? = false,
    var hourEnd: String? = null,
    var hourStart: String? = null
)

@Entity(indices = [(Index("uid"))])
data class AfiClass(
    @PrimaryKey var uid: String = "",
    var code: String? = null,
    var enabled: Boolean? = null,
    var icon: String? = null,
    var order: Int? = 0,
    @Ignore var selected: Boolean? = false,
    @Embedded var lang: LangAfiClass? = null
): ListItemViewModel()

@Entity(indices = [(Index("uid"))])
data class Destination(
    @PrimaryKey var uid: String = "",
    var code: String? = null,
    var image: String? = null,
    var order: Int? = null,
    var status: Int? = null,
    @Embedded var lang: LangDestination? = null
): ListItemViewModel()

@Entity(indices = [(Index("uid"))])
data class DestinationActivity(
    @PrimaryKey var uid: String = "",
    var code: String? = null,
    var destinationUID: String? = null,
    var enabled: Boolean? = false,
    var image: String? = null,
    var order: Int? = null,
    var type: String? = null,
    @Embedded var lang:LangDestinationActivity? = null


): ListItemViewModel(){
    fun isVisibleSchedule():Boolean?{
        return lang?.schedule?.isNotEmpty()
    }
}

enum class ContactType(val value: String){
    PHONE("phone"),
    EMAIL("email")
}