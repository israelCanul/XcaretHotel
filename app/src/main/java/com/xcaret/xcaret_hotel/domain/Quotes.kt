package com.xcaret.xcaret_hotel.domain

import androidx.room.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.Language.getLangCode
import java.lang.Exception
import java.time.DayOfWeek
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

@Entity
data class DateQuotes(
    @PrimaryKey
    var hotelId: Int = 0,
    var dateArrival: String = "",
    var dateDeparture: String = ""
) {
    @Ignore
    fun displayDates(): String{
        try {
            return if (calculateNights() > 0) {
                val arrivalFormat = DateUtil.changeFormatDate(
                    dateArrival,
                    DateUtil.DATE_FORMAT_WEATHER,
                    DateUtil.QUOTES_FORMAT
                )
                val departureFormat = DateUtil.changeFormatDate(
                    dateDeparture,
                    DateUtil.DATE_FORMAT_WEATHER,
                    DateUtil.QUOTES_FORMAT
                )
                "$arrivalFormat - $departureFormat"
            } else ""
        }catch (e: Exception){
            return ""
        }
    }

    @Ignore
    fun getFormatDisplay(date: String?): String{
        return try{
            date?.let {
                return DateUtil.changeFormatDate(
                    it,
                    DateUtil.DATE_FORMAT_WEATHER,
                    DateUtil.QUOTES_FORMAT_LARGE
                )
            } ?: kotlin.run { "" }
        }
        catch (e: Exception){
            ""
        }
    }

    @Ignore
    fun calculateNights(): Int{
        return try{
            val arrivalCal = DateUtil.convertStringToDate(dateArrival, DateUtil.DATE_FORMAT_WEATHER)
            val departureCal = DateUtil.convertStringToDate(dateDeparture, DateUtil.DATE_FORMAT_WEATHER)
            val diff = departureCal.timeInMillis - arrivalCal.timeInMillis
            abs(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt())
        } catch (e: Exception){
            0
        }
    }
}

@Entity
data class SuiteQuotes(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sq_id")
    var id: Long = 0,
    var number: Int = 0,
    @ColumnInfo(name = "room_id")
    var id_room: Int = 0,
    @ColumnInfo(name = "is_selected")
    var isSelected: Boolean = false,
    var adults: Int = 0,
    var children: Int = 0,

    @ColumnInfo(name = "start_date")
    var startDate: String = "",

    @ColumnInfo(name = "end_date")
    var endDate: String = "",

    var channel: Int = 13,
    var currency: String = "MXN",
    var language: String = "ES",
    var country: String = "mx",

    @ColumnInfo(name = "hotel_code")
    var hodelCode: Int = 74213,

    @ColumnInfo(name = "ages")
    var ageString: String = "",

    var suiteCodeSelected: String = "",
    var suiteNameSelected: String = "",

    @ColumnInfo(name = "sq_rate_code")
    var ratePlanCode: String = "",

    @ColumnInfo(name = "sq_promo_code")
    var promoCode: String = "",

    @Ignore
    var age: MutableList<Int> = mutableListOf(),

    @Ignore
    var ratePlan: SuiteRatePlans? = null,

    @Ignore
    var height: Int = 0,
    @Ignore
    var visible: Boolean = true
): ListItemViewModel() {

    fun type(): TypeSuiteSelected{
        val type = when {
            number == 1000 -> TypeSuiteSelected.ADD
            isSelected -> TypeSuiteSelected.SELECTED
            else -> TypeSuiteSelected.UNSELECTED
        }
        return type
    }

    fun ageToList(): List<Int>{
        val edades = ageString.split(",")
        age.clear()
        edades.forEach { a ->
            a.toIntOrNull()?.let { age.add(it) }
        }
        return age
    }

    fun numberToString() = number.toString()
    fun adultsToString() = adults.toString()
    fun childrenToString() = children.toString()

    fun setAges(age: MutableList<Int>){
        this.age = age
        var aString = ""
        age.forEachIndexed { index, i ->
            aString += if(index == (age.size-1)) "$i"
            else "$i,"
        }
        this.ageString = aString
    }

    @Ignore
    fun calculateNights(): Int{
        return try{
            val arrivalCal = DateUtil.convertStringToDate(startDate, DateUtil.DATE_FORMAT_WEATHER)
            val departureCal = DateUtil.convertStringToDate(endDate, DateUtil.DATE_FORMAT_WEATHER)
            val diff = departureCal.timeInMillis - arrivalCal.timeInMillis
            abs(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt())
        } catch (e: Exception){
            0
        }
    }

    fun toRequest(): JsonObject{
        val request = JsonObject()

        val header = JsonObject()
        header.addProperty("Channel", channel)
        header.addProperty("Currency", currency)
        header.addProperty("Language", com.xcaret.xcaret_hotel.view.config.Language.getLangCode(HotelXcaretApp.mContext))
        header.addProperty("Country", country)
        request.add("Header", header)

        request.addProperty("promoCode", promoCode)

        val hCodes = JsonArray()
        hCodes.add(hodelCode)
        request.add("hotelCodes", hCodes)

        request.addProperty("CheckIn", startDate)
        request.addProperty("CheckOut", endDate)

        val paxesArray = JsonArray()
        val pax = JsonObject()
        pax.addProperty("id", "")
        pax.addProperty("adults", adults)
        pax.addProperty("children", children)

        val ageArray = JsonArray()
        val edades = ageString.split(",")
        edades.forEach { a ->
            a.toIntOrNull()?.let { ageArray.add(it) }
        }
        pax.add("ages", ageArray)

        paxesArray.add(pax)
        request.add("paxes", paxesArray)
        LogHX.i("Request quotes", request.toString())

        return request
    }

    fun toRequestLiveCheck(): JsonObject{
        val request = JsonObject()

        val header = JsonObject()
        header.addProperty("Channel", channel)
        header.addProperty("Currency", currency)
        header.addProperty("Language", language)
        header.addProperty("Country", country)
        request.add("Header", header)

        request.addProperty("PromoCode", promoCode)

        val hotel = JsonObject()
        hotel.addProperty("HotelCode", hodelCode)
        hotel.addProperty("RoomCode", suiteCodeSelected)
        hotel.addProperty("RatePlanCode", ratePlanCode)
        request.add("Hotel", hotel)

        request.addProperty("CheckIn", startDate)
        request.addProperty("CheckOut", endDate)

        val paxesArray = JsonArray()
        val pax = JsonObject()
        pax.addProperty("Adults", adults)
        pax.addProperty("Childs", children)

        val ageArray = JsonArray()
        val edades = ageString.split(",")
        edades.forEach { a ->
            a.toIntOrNull()?.let { ageArray.add(it) }
        }
        pax.add("ChildsAges", ageArray)

        paxesArray.add(pax)
        request.add("Paxes", paxesArray)

        LogHX.i("Request liveCheck", request.toString())
        return request
    }

}

@Entity
data class SuiteRatePlans(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "room_id")
    var roomId: Long = 0,

    @ColumnInfo(name = "rate_place_code")
    var ratePlanCode: String = "",

    @ColumnInfo(name = "rate_room_code")
    var roomCode: String = "",

    @ColumnInfo(name = "rate_room_name")
    var roomName: String = "",

    @ColumnInfo(name = "rate_plan_name")
    var ratePlanName: String? = null,

    @ColumnInfo(name = "rate_plan_description")
    var ratePlanDescription: String? = null,

    @ColumnInfo(name = "rate_hotel_code")
    var hotelCode: Int = 0,

    @ColumnInfo(name = "hotel_name")
    var hotelName: String = "",

    @ColumnInfo(name = "rp_currency")
    var currency: String = "",

    @ColumnInfo(name = "base_currency")
    var baseCurrency: String = "",
    //var promotion: Any? = null,

    @ColumnInfo(name = "rate_start_date")
    var startDate: String = "",

    @ColumnInfo(name = "rate_end_date")
    var endDate: String = "",

    @ColumnInfo(name = "guarantee_code")
    var guaranteeCode: String? = null,

    @ColumnInfo(name = "guarantee_description")
    var guaranteeDescription: String? = null,

    @ColumnInfo(name = "guarantee_charges_required")
    var guaranteeChargeRequired: Boolean? = null,

    @ColumnInfo(name = "guarantee_charge_percent")
    var guaranteeChargePercent: Double? = null,

    @ColumnInfo(name = "meals_included")
    var mealsIncluded: Boolean? = null,
    var amount: Double = 0.0,

    @ColumnInfo(name = "normal_amount")
    var normalAmount: Double = 0.0,

    @ColumnInfo(name = "average_amount")
    var averageAmount: Double = 0.0,

    @ColumnInfo(name = "policies_code")
    var policiesCode: String? = null,

    @ColumnInfo(name = "policies_description")
    var policiesDescription: String? = null,

    @ColumnInfo(name = "policies_amount")
    var policiesAmount: Double = 0.0,

    @ColumnInfo(name = "policies_penalty_days")
    var policiesPenaltyDays: Int = 0,

    @ColumnInfo(name = "policies_date_to_apply")
    var policiesDateToApply: String = "",

    @Ignore
    val priceForNights: MutableList<QuotesRoomRatePlansNights> = mutableListOf()
){
    fun averageFormat(): String {
        var symbol = "".getSymbolCurrency()
        if(symbol.equals(currency, true)) symbol = "$"
        val result = averageAmount.formatCurrency()
        return "$symbol $result"
    }
    fun amountFormat(): String {
        var symbol = "".getSymbolCurrency()
        if(symbol.equals(currency, true)) symbol = "$"
        return "$symbol ${amount.formatCurrency()}"
    }
}

data class QuotesRoomRatePlansNights(
    var id: Long = 0,
    var ratePlanCode: String,
    var date: String? = null,
    var amount: Double? = null,
    var normalAmount: Double? = null,
    var hotelId: Int? = null,
    var roomCode: String? = null
): ListItemViewModel() {

    fun formatDate(): String {
        if(date.isNullOrEmpty()) return ""
        return DateUtil.changeFormatDate(date!!, DateUtil.ORIGIN_FORMAT_RESERVATION, DateUtil.QUOTES_FORMAT)
    }

    fun formatAmount(): String{
        if((amount ?: -1.0) > 0f){
            val currency = com.xcaret.xcaret_hotel.view.config.Language.getCurrency(HotelXcaretApp.mContext)
            var symbol = "".getSymbolCurrency()
            if(symbol.equals(currency, ignoreCase = true))
                symbol = "$"
            return "$symbol ${amount!!.formatCurrency()} ${currency.toUpperCase()}"
        }else return ""
    }
}

class ResponseQuotes(
    val success: Boolean = false,
    val errorCode: Int = -1, //0 -> error server
    val result: List<SuiteRatePlans> = emptyList()
)

class ResponseLiveCheck(
    val success: Boolean = false,
    val errorCode: StatusResponse = StatusResponse.NONE,
    val errorSuite: SuiteQuotes? = null
)

class YearBooking(
    var year: Int = 0,
    var isSelected: Boolean = false
): ListItemViewModel() {
    override fun toString() = year.toString()
}

class MonthBooking(
    var year: Int = 0,
    var monthValue: Int = 0,
    var name: String = ""
): ListItemViewModel() {
    fun capitalizeMonth() = name.capitalize()
}

data class DayBooking(
    var dayName:String ="",
    var selected:Boolean = false,
    var dayValue:Int = 0):ListItemViewModel()


class PaxRules(
    val adults: Int = 0,
    val children: Int = 0
)

enum class TypeSuiteSelected {
    UNSELECTED,
    SELECTED,
    ADD
}