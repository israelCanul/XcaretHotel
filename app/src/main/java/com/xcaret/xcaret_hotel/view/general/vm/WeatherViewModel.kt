package com.xcaret.xcaret_hotel.view.general.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.CategoryUseCase
import com.xcaret.xcaret_hotel.data.usecase.WeatherUseCase
import com.xcaret.xcaret_hotel.data.usecase.WebCamUseCase
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.Hotel
import com.xcaret.xcaret_hotel.domain.Weather
import com.xcaret.xcaret_hotel.domain.WebCam
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.DateUtil
import com.xcaret.xcaret_hotel.view.config.SettingsManager
import kotlinx.coroutines.flow.Flow

class WeatherViewModel: ViewModel(){

    private val weatherUseCase = WeatherUseCase()
    private val webCamUseCase = WebCamUseCase()
    private val categoryUseCase = CategoryUseCase()
    private val settingsManager = SettingsManager.getInstance(HotelXcaretApp.mContext)

    val currentWeather = MutableLiveData<Weather?>()
    val categoryLive = MutableLiveData<Category?>()
    val celsiusActive = MutableLiveData<Boolean>(true)
    val currentHotel = MutableLiveData<Hotel?>()

    val currentMetric: Flow<SettingsManager.TemperatureMetric> = settingsManager.currentMetic
    fun getWeatherByDay(day: String = DateUtil.getDateByFormat(DateUtil.DATE_FORMAT_WEATHER)): LiveData<Weather?> = weatherUseCase.getWeatherByDay(day)
    fun getWeathersLimitTo(limit: Int = 7): LiveData<List<Weather>> = weatherUseCase.getWeathersLimitTo(limit)
    fun getAllWebCamsByCategory(): LiveData<List<WebCam>> = webCamUseCase.getAllWebCamsByCategory(categoryLive.value?.uid ?: "")
    fun getAllWebCams(): LiveData<List<WebCam>> = webCamUseCase.getAllWebCams()
    fun findByCodeLiveWithoutLang(): LiveData<Category?> = categoryUseCase.findByCodeLiveWithoutLang(Constants.CATEGORY_VIDEO_LIVE)
    fun findByCodeLiveWithoutHotel(): LiveData<Category?> = categoryUseCase.findByCodeLiveWithoutHotel(Constants.CATEGORY_VIDEO_LIVE)
    suspend fun setCurrentMetric(metric: SettingsManager.TemperatureMetric) = settingsManager.setCurrentMetric(metric)

    fun formatTemp(temp: Double?): String{
        return temp?.toInt()?.toString() ?: ""
    }
}