package com.xcaret.xcaret_hotel.view.menu.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.AwardUseCase
import com.xcaret.xcaret_hotel.data.usecase.CategoryUseCase
import com.xcaret.xcaret_hotel.data.usecase.HotelUseCase
import com.xcaret.xcaret_hotel.data.usecase.PlaceUseCase
import com.xcaret.xcaret_hotel.domain.Award
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.Hotel
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Language

class AboutHotelViewModel: ViewModel(){

    private val categoryUseCase = CategoryUseCase()
    private val hotelUseCase = HotelUseCase()
    private val placeUseCase = PlaceUseCase()
    private val awardUseCase = AwardUseCase()

    val placeHotelLiveData = MutableLiveData<Place?>()
    val hotelLiveData = MutableLiveData<Hotel?>()
    val categoryHotelLiveData = MutableLiveData<Category?>()

    fun findCategoryByCode(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = categoryUseCase.findByCodeLiveOutHotel(Constants.CATEGORY_HOTEL, lang)
    fun findPlace(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Place?> = placeUseCase.findByCategoryAndHotel(categoryHotelLiveData.value?.uid ?: "", hotelLiveData.value?.uid ?: "", lang)
    fun findAwardByPlaceUID(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Award>> = awardUseCase.findByPlaceUID(placeHotelLiveData.value?.uid ?: "", lang)
}