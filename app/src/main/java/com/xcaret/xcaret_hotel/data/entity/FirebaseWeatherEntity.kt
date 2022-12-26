package com.xcaret.xcaret_hotel.data.entity

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class WeatherEntity(
    val clouds: Int = 0,
    val dew_point: Double = 0.0,
    val dt: Long = 0,
    val feels_like: FeelsLikeEntity? = null,
    val feels_likef: FeelsLikeEntity? = null,
    val humidity: Int = 0,
    val pop: Int = 0,
    val pressure: Int = 0,
    val rain: Double = 0.0,
    val sunrise: Long = 0,
    val sunset: Long = 0,
    val temp: TempEntity? = null,
    val tempf: TempEntity? = null,
    val uvi: Double = 0.0,
    val weather: List<ForecastEntity> = listOf(),
    val wind_deg: Int = 0,
    val wind_speed: Double = 0.0
)

@IgnoreExtraProperties
data class FeelsLikeEntity(
    val day: Double = 0.0,
    val eve: Double = 0.0,
    val morn: Double = 0.0,
    val night: Double = 0.0
)

@IgnoreExtraProperties
data class TempEntity(
    val day: Double = 0.0,
    val eve: Double = 0.0,
    val max: Double = 0.0,
    val min: Double = 0.0,
    val morn: Double = 0.0,
    val night: Double = 0.0
)

@IgnoreExtraProperties
data class ForecastEntity(
    val description: String = "",
    val icon: String = "",
    val id: Int = 0,
    val main: String = ""
)