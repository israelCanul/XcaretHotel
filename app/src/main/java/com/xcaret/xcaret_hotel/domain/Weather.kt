package com.xcaret.xcaret_hotel.domain

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.xcaret.xcaret_hotel.view.config.ListItemViewModel

@Entity
data class Weather(
    @PrimaryKey var id: String = "",
    var place_uid: String? = null,
    var clouds: Int? = 0,
    var dew_point: Double? = 0.0,
    var dt: Long? = 0,
    @Embedded var feels_like: FeelsLike? = null,
    @Embedded var feels_likef: FeelsLikeF? = null,
    var humidity: Int? = 0,
    var pop: Int? = 0,
    var pressure: Int? = 0,
    var rain: Double? = 0.0,
    var sunrise: Long? = 0,
    var sunset: Long? = 0,
    @Embedded var temp: Temp? = null,
    @Embedded var tempf: TempF? = null,
    var uvi: Double? = 0.0,
    @Embedded var weather: Forecast? = null,
    var wind_deg: Int? = 0,
    var wind_speed: Double? = 0.0,
    @Ignore var colorBackground: Int? = null,
    @Ignore var isCelsiusActive: Boolean = true
): ListItemViewModel() {

    fun formatTemp(temp: Double?): String{
        return temp?.toInt()?.toString() ?: ""
    }

}

data class FeelsLike(
    var feels_like_day: Double? = 0.0,
    var feels_like_eve: Double? = 0.0,
    var feels_like_morn: Double? = 0.0,
    var feels_like_night: Double? = 0.0
)

data class FeelsLikeF(
    var feels_likef_day: Double? = 0.0,
    var feels_likef_eve: Double? = 0.0,
    var feels_likef_morn: Double? = 0.0,
    var feels_likef_night: Double? = 0.0
)

data class Temp(
    var temp_day: Double? = 0.0,
    var temp_eve: Double? = 0.0,
    var temp_max: Double? = 0.0,
    var temp_min: Double? = 0.0,
    var temp_morn: Double? = 0.0,
    var temp_night: Double? = 0.0
)

data class TempF(
    var tempf_day: Double? = 0.0,
    var tempf_eve: Double? = 0.0,
    var tempf_max: Double? = 0.0,
    var tempf_min: Double? = 0.0,
    var tempf_morn: Double? = 0.0,
    var tempf_night: Double? = 0.0
)

data class Forecast(
    var forecast_description: String? = "",
    var forecast_icon: String? = "",
    var forecast_id: Int? = 0,
    var forecast_main: String? = ""
)