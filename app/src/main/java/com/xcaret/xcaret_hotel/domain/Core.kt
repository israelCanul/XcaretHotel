package com.xcaret.xcaret_hotel.domain

import android.view.View
import androidx.room.*
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.xcaret.xcaret_hotel.data.entity.GalleryItemEntity
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticType
import com.xcaret.xcaret_hotel.view.config.analytics.logEventViewContentFacebook
import com.xcaret.xcaret_hotel.view.general.ui.MainActivity
import java.lang.Exception

@Entity(indices = [(Index("code", "hotelUID", "placeUID"))])
data class Activity(
    @PrimaryKey var uid: String = "",
    var capacity: String? = null,
    var categoryUID: String? = null,
    var code: String? = null,
    var duration: String? = null,
    var enabled: Boolean? = null,
    var hotelUID: String? = null,
    var icon: String? = null,
    var placeUID: String? = null,
    @Embedded var lang: LangActivity? = null,
    @Ignore var location: Place? = null
): ListItemViewModel()

@Entity(indices = [(Index("code"))])
data class Hotel(
    @PrimaryKey var uid: String = "",

    @ColumnInfo(name = "id_synxis")
    var idSynxis: String? = null,
    var name: String? = null,
    var code: String? = null,
    var logo: String? = null,
    var enabled: Boolean = false,

    @ColumnInfo(name = "booking_active")
    var bookingActive: Boolean = false,

    @ColumnInfo(name = "pax_default")
    var paxDefault: String = "",

    @ColumnInfo(name = "pax_rules")
    var paxRules: String = "",

    var baseColor: String? = null,
    var baseColorDark: String? = null,
    var order: Int = 0,

    @ColumnInfo(name="max_night")
    var maxNight:Int? =0,
    @ColumnInfo(name="min_night")
    var minNight:Int? = 0,
    @ColumnInfo(name="minimun_age_children")
    var minimumAgeChildren:Int? =0,

    @Embedded var lang: LangHotel? = null,
    @Ignore var hotelPlace: Place? = null,
    @Ignore var isSelected: Boolean = false
): ListItemViewModel() {
    @Ignore
    fun getDefaultPaxRule(): PaxRules{
        val rule = paxDefault.split(Constants.SEPARATOR_PAX_RULES)
        return if(rule.size >= 2)
            PaxRules(rule[0].trim().toIntOrNull() ?: 2, rule[1].trim().toIntOrNull() ?: 0)
        else PaxRules(2, 0)
    }

    @Ignore
    fun getListPaxRules(): List<PaxRules>?{
        val result = mutableListOf<PaxRules>()
        try{
            val rules = paxRules.trim().split(Constants.SEPARATOR_RULES)
            LogHX.i("Rule", paxRules)
            rules.forEach { rule ->
                val paxRule = rule.trim().split(Constants.SEPARATOR_PAX_RULES)
                if(paxRule.size >= 2){
                    // position 0 is adults
                    // position 1 is child
                    LogHX.i("paxRule", rule)
                    paxRule[0].trim().toIntOrNull()?.let { adult ->
                        paxRule[1].trim().toIntOrNull()?.let { child ->
                            result.add(PaxRules(adult, child))
                        }
                    }
                }
            }
            return result
        }
        catch (e: Exception){return null}
    }
}

@Entity(indices = [(Index("categoryUID", "code", "sivexCode"))])
data class ParkTour(
    @PrimaryKey var uid: String = "",
    var name: String? = null,
    var categoryUID: String? = null,
    var sivexCode: String? = null,
    var code: String? = null,
    var enabled: Boolean = false,
    var isPark: Boolean = false,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var needReservation: Boolean = false,
    var tags: String? = null,
    var order: Int = 0,
    var classUID:String? = null,
    @Embedded var lang: LangParkTour? = null
): ListItemViewModel()

@Entity(indices = [(Index("category_uid", "code", "hotel_uid", "type", "pparent_uid"))])
data class Place(
    @PrimaryKey var uid: String = "",

    @ColumnInfo(name = "category_uid")
    var categoryUID: String? = null,

    @ColumnInfo(name = "contact_category_uid")
    var contactCategoryUID: String? = null,

    var code: String? = null,
    var enabled: Boolean = false,
    var extra: String? = null,

    @ColumnInfo(name = "hotel_uid")
    var hotelUID: String? = null,
    var iconMap: String? = null,
    var colorMarkerMap: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var order: Int  = 0,
    var weather: Boolean = false,
    var level: String? = null,

    @ColumnInfo(name = "pparent_uid")
    var parentUID: String? = null,

    var type: String? = null,
    @Embedded var lang: LangPlace? = null,
    @Ignore var restaurantDetail: LangRestaurantDetail? = null,
    @Ignore var location: Place? = null,
    @Ignore var marker: Marker? = null,
    @Ignore var houseId: Int = 0,
    @Ignore var extraInfoBuilding: ExtraInfoBuilding? = null,
    @Ignore var isBuilding: Boolean = false,
    @Ignore var category: Category? = null,
    @Ignore var formatLocation: String = "",
    @Ignore var hotelCode: String = Constants.HOTEL_XCARTE_MEXICO
): ListItemViewModel(){
    fun showIcon():Boolean {
        var showIcon = false
        if(this.location!= null){ showIcon = true}
        if (this.location?.iconMap != null) showIcon = true
        return showIcon
    }
    fun getCorrectLocation():String {
        var correctLocation = "hxa_building"
        if(location?.iconMap.toString().isNotEmpty()){
            correctLocation = location?.iconMap.toString()
        }
        if (location?.iconMap.toString().isEmpty() && iconMap!= null){
            correctLocation = iconMap.toString()
        }

        return correctLocation
    }
}

@Entity(indices = [(Index("category_uid", "code", "hotel_uid"))])
data class RoomType(
    @PrimaryKey var uid: String = "",
    var code: String? = null,

    @ColumnInfo(name = "code_synxis")
    var codeSynxis: String? = null,

    @ColumnInfo(name = "category_uid")
    var categoryUID: String? = null,

    @ColumnInfo(name = "hotel_uid")
    var hotelUID: String? = null,

    var enabled: Boolean = false,
    var order: Int = 0,

    var tour360: String? = null,

    @Ignore var galleryMap : Map<String,GalleryItemEntity>? = null,
    @Ignore var IdRoom : Int? = null,
    @Ignore var hasDate: Boolean = false,
    @Ignore var isSelected: Boolean = false,
    @Embedded var lang: LangRoomType? = null,
    @Ignore var category: Category? = null,
    @Ignore var mainAmenity: List<RoomAmenity>? = null,
    @Ignore var mainAmenityAdapter: GenericAdapter<RoomAmenity>? = null,
    @Ignore var ratePlan: MutableList<SuiteRatePlans> = mutableListOf(),
    @Ignore var isExpandable: Boolean = false,
    @Ignore var princeNigthsAdapter: GenericAdapter<QuotesRoomRatePlansNights>? = null
): ListItemViewModel() {

    fun visibleNotDisponible() = hasDate && !hasRatePlan()
    fun hasRatePlan() = ratePlan.isNotEmpty()
    fun isUniqueRatePlan() = ratePlan.size <= 1
    fun getRatePlan(cheap: Boolean = false): SuiteRatePlans? {
        var result: SuiteRatePlans? = null
        if(hasRatePlan()){
            if(isUniqueRatePlan()) return ratePlan[0]
            else{
                var cheapRatePlace = ratePlan[0]
                ratePlan.forEach { srp ->
                    if(cheap) {
                        if (srp.amount < cheapRatePlace.amount)
                            cheapRatePlace = srp
                    }else {
                        if (srp.amount > cheapRatePlace.amount)
                            cheapRatePlace = srp
                    }
                }
                result = cheapRatePlace
            }
        }
        return result
    }

    @Ignore
    fun partOfThePrice(decimal: Boolean): String{
        val rate = getRatePlan(true)
        var result = ""
        rate?.let {r ->
            result = r.averageAmount.formatCurrency()
            //$9,999.00
            var symbol  = "".getSymbolCurrency()
            if(symbol.equals(r.currency, true)) symbol = "$"
            if(!decimal) {
                if (result.length > 3)
                    result = "$symbol ${result.substring(0, result.length - 3)}"
            }else {
                if (result.length > 3)
                    result = "${result.substring(result.length - 3, result.length)} ${r.currency}"
            }
        }
        return result
    }

    @Ignore
    fun getAmenityView(): RoomAmenity?{
        return mainAmenity?.find { it.code.equals(Constants.AMENITY_VIEW_CODE, ignoreCase = true) }
    }

    @Ignore
    fun onClickMoreInfo(view: View){
        if(view.context is MainActivity) {
                val act = view.context as MainActivity
            act._viewModel.roomTypeLiveData.value = this
            act.showRoomTypeDialog()
            act.logEventViewContentFacebook(view.context,AnalyticType.VIEW_CONTENT_FACEBOOK,this)
        }
    }
}

@Entity
data class Gallery(
    @PrimaryKey var uid: String = "",
    @ColumnInfo(name = "parent_uid")
    var parentUID:String? = null,
    var isMain: Boolean? = null,
    var name: String? = null,
    var order: Int? = null,
    var enabled: Boolean? = false,
    @Ignore var isSelected: Boolean = false,
    var path: String? = null
): ListItemViewModel()
{
    fun realPath() = "{$path/$name}"
}

data class RoomTypeAndGallery(
    @Embedded val roomType: RoomType,
    @Relation(
        parentColumn = "uid",
        entityColumn = "parent_uid"
    )
    val playlists: List<Gallery>
):ListItemViewModel()

@Entity(indices = [(Index("category_uid", "code"))])
data class WebCam(
    @PrimaryKey var uid: String = "",

    @ColumnInfo(name = "category_uid")
    var categoryUID: String? = null,
    var name: String? = null,
    var image: String? = null,

    var url: String? = null,

    @ColumnInfo(name = "video_id")
    var videoId: String? = null,
    var order: Int = 0,
    var code: String? = null,
    var enabled: Boolean = false
): ListItemViewModel()