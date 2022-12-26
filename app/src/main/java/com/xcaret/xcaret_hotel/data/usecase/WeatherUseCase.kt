package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.FirebaseWeatherRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Weather
import com.xcaret.xcaret_hotel.view.config.DateUtil

class WeatherUseCase: BaseUseCase<Weather>{

    private lateinit var placeUID: String

    private val dao = database.weatherDao()
    private val repository: FirebaseWeatherRepository by lazy { FirebaseWeatherRepository(placeUID) }

    constructor()

    constructor(placeUID: String){
        this.placeUID = placeUID
    }

    override fun getDao(): BaseDao<Weather>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<Weather>? = repository

    fun getWeatherByDay(day: String = DateUtil.getDateByFormat(DateUtil.DATE_FORMAT_WEATHER)): LiveData<Weather?> = dao.getWeatherByDay()
    fun getWeathersLimitTo(limit: Int = 7): LiveData<List<Weather>> = dao.getWeathersLimitTo()
}