package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.domain.Weather
import com.xcaret.xcaret_hotel.view.config.DateUtil

@Dao
abstract class WeatherDao: BaseDao<Weather>(Weather::class.simpleName ?: "") {

    @Query(value = """
        SELECT * FROM Weather w
        WHERE w.id = :day
    """)
    abstract fun getWeatherByDay(day: String = DateUtil.getDateByFormat(DateUtil.DATE_FORMAT_WEATHER)): LiveData<Weather?>

    @Query(value = """
        SELECT * FROM Weather w
        WHERE w.id > :day
        ORDER BY w.id ASC LIMIT :limit
    """)
    abstract fun getWeathersLimitTo(limit: Int = 7, day: String = DateUtil.getDateByFormat(DateUtil.DATE_FORMAT_WEATHER)): LiveData<List<Weather>>
}