package com.xcaret.xcaret_hotel.view.category.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.CategoryUseCase
import com.xcaret.xcaret_hotel.data.usecase.PlaceUseCase
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.LangRestaurantDetail
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.view.config.Language

class PoolDetailViewModel: ViewModel(){
    private val placeUseCase = PlaceUseCase()
    private val categoryUseCase = CategoryUseCase()

    val poolUID = MutableLiveData<String?>()
    val poolLiveData = MutableLiveData<Place?>()
    val categoryLiveData = MutableLiveData<Category?>()
    val callFromMap = MutableLiveData<Boolean>(false)

    fun findById(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Place?> = placeUseCase.findById(poolUID.value ?: "", lang)
    fun findCategoryById(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = categoryUseCase.findById(poolLiveData.value?.categoryUID ?: "", lang)
}