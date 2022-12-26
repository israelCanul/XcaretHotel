package com.xcaret.xcaret_hotel.data.mapper

import com.xcaret.xcaret_hotel.data.entity.ForecastEntity
import com.xcaret.xcaret_hotel.data.entity.WeatherEntity
import com.xcaret.xcaret_hotel.domain.*

class WeatherMapper: FirebaseMapper<WeatherEntity, Weather>() {
    override fun map(from: WeatherEntity?, key: String?): Weather {
        val forecast = mutableListOf<ForecastEntity>()
        from?.weather?.let {
            if(it.isNotEmpty()) forecast.add(it[0])
        }
        return Weather(
            id = key ?: "",
            clouds = from?.clouds ?: 0,
            dt = from?.dt ?: 0,
            feels_like = FeelsLike(
                feels_like_day = from?.feels_like?.day,
                feels_like_eve = from?.feels_like?.eve,
                feels_like_morn = from?.feels_like?.morn,
                feels_like_night = from?.feels_like?.night
            ),
            feels_likef = FeelsLikeF(
                feels_likef_day = from?.feels_likef?.day ?: 0.0,
                feels_likef_eve = from?.feels_likef?.eve ?: 0.0,
                feels_likef_morn = from?.feels_likef?.morn ?: 0.0,
                feels_likef_night = from?.feels_likef?.night ?: 0.0
            ),
            humidity = from?.humidity,
            pop = from?.pop,
            pressure = from?.pressure,
            rain = from?.rain,
            sunrise = from?.sunrise,
            sunset = from?.sunset,
            temp = Temp(
                temp_day = from?.temp?.day,
                temp_eve = from?.temp?.eve,
                temp_max = from?.temp?.max,
                temp_min = from?.temp?.min,
                temp_morn = from?.temp?.morn,
                temp_night = from?.temp?.night
            ),
            tempf = TempF(
                tempf_day = from?.tempf?.day ?: 0.0,
                tempf_eve = from?.tempf?.eve ?: 0.0,
                tempf_max = from?.tempf?.max ?: 0.0,
                tempf_min = from?.tempf?.min ?: 0.0,
                tempf_morn = from?.tempf?.morn ?: 0.0,
                tempf_night = from?.tempf?.night ?: 0.0
            ),
            uvi = from?.uvi,
            weather = if(forecast.isNotEmpty()) Forecast(
                forecast_description = forecast[0].description,
                forecast_icon = forecast[0].icon,
                forecast_id = forecast[0].id,
                forecast_main = forecast[0].main
            ) else null,
            wind_deg = from?.wind_deg,
            wind_speed = from?.wind_speed
        )
    }
}