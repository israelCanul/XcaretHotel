package com.xcaret.xcaret_hotel.view.tabs.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.DateUtil
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.Session

class HomeViewModel: ViewModel() {
    private val userUseCase = UserUseCase()
    private val hotelUseCase = HotelUseCase()
    private val categoryUseCase = CategoryUseCase()
    private val placeUseCase = PlaceUseCase()
    private val weatherUseCase = WeatherUseCase()
    private val labelUseCase = LangLabelUseCase()
    private val dateQuotesUseCase = DateQuotesUseCase()
    private val countryUseCase = CountryUseCase()

    val isLoginActive = MutableLiveData<Boolean>(false)
    val hotelsPlaceLiveData = MutableLiveData<List<Place>>()
    val hotelCategoryLiveData = MutableLiveData<Category?>()
    val sessionLiveData = MutableLiveData<User>()
    val hotelSynxisSelected = MutableLiveData(0)
    val hotelsLive = MutableLiveData<List<Hotel>>()
    val hotelLiveData = MutableLiveData<Hotel?>()
    var countryCode = MutableLiveData<String?>()
    var country = MutableLiveData<Country?>()



    fun checkSession(){
        initData()
    }

    private fun initData(){
        userUseCase.isSessionActive { status ->
            isLoginActive.postValue(status)
        }
    }

    fun signOut(){
        userUseCase.signOut {
            initData()
        }
    }

    fun all(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Hotel>> = hotelUseCase.all(lang)
    fun allByHotel(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>> = categoryUseCase.allByHotel(hotelLiveData.value?.uid ?: "", lang)
    fun findPlaceByCategory(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>> = placeUseCase.findHotelsPlace(hotelCategoryLiveData.value?.uid ?: "",  lang)
    fun getWeatherByDay(day: String = DateUtil.getDateByFormat(DateUtil.DATE_FORMAT_WEATHER)): LiveData<Weather?> = weatherUseCase.getWeatherByDay()
    fun getUser() = userUseCase.getUser()
    fun getUserLive(): LiveData<User?> = userUseCase.getUserLive()
    fun getLabelByCode(code: String) = labelUseCase.findLabelOutLive(code)
    fun findCategoryHotel(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = categoryUseCase.findByCodeLiveOutHotel(Constants.CATEGORY_HOTEL, lang)
    fun findDateByHotelId(IdHotel :Int?):LiveData<DateQuotes?> = dateQuotesUseCase.allByHotelId(IdHotel)
    fun findCountry(code:String) {
        val count = countryUseCase.findByIso2OutLiveData(code)
        try {
            Session.setCountryCode(count?.iSO!!.toLowerCase(),HotelXcaretApp.mContext)
        }catch (exc:Exception){

        }


    }



}