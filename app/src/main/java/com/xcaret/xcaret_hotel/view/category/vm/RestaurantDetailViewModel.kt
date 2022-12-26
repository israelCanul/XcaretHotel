package com.xcaret.xcaret_hotel.view.category.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.CategoryUseCase
import com.xcaret.xcaret_hotel.data.usecase.LangRestaurantDetailUseCase
import com.xcaret.xcaret_hotel.data.usecase.PlaceUseCase
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.LangRestaurantDetail
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.view.config.Language

class RestaurantDetailViewModel: ViewModel(){

    private val placeUseCase = PlaceUseCase()
    private val categoryUseCase = CategoryUseCase()
    private val langRestaurantDetailUseCase = LangRestaurantDetailUseCase()

    val restUID = MutableLiveData<String?>()
    val restLiveData = MutableLiveData<Place?>()
    val locationLiveData = MutableLiveData<Place?>()
    val categoryLiveData = MutableLiveData<Category?>()
    val restDetailLiveData = MutableLiveData<LangRestaurantDetail?>()
    val callFromMap = MutableLiveData<Boolean>(false)

    fun findById(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Place?> = findPlace(restUID.value ?: "", lang)
    fun findLocation(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Place?> = findPlace(restLiveData.value?.parentUID ?: "", lang)
    fun findCategoryById(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = categoryUseCase.findById(restLiveData.value?.categoryUID ?: "", lang)
    fun findRestDetail(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<LangRestaurantDetail?> = langRestaurantDetailUseCase.findById(restLiveData.value?.uid ?: "", lang)

    private fun findPlace(uid: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Place?> = placeUseCase.findById(uid, lang)
}