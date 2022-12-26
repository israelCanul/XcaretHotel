package com.xcaret.xcaret_hotel.view.menu.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.CategoryUseCase
import com.xcaret.xcaret_hotel.data.usecase.ContactUseCase
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.Contact
import com.xcaret.xcaret_hotel.domain.Hotel
import com.xcaret.xcaret_hotel.domain.LangLabel
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Language

class CallUsViewModel: ViewModel() {
    private val contactUseCase = ContactUseCase()
    private val categoryUseCase = CategoryUseCase()
    private val labelUseCase = LangLabelUseCase()

    val categoryCallUseLiveData = MutableLiveData<String>()
    val hotelLiveData = MutableLiveData<Hotel?>()
    val typeFilterLive = MutableLiveData<Int>()
    val valueFilterLive = MutableLiveData<String>()
    val currentPosition = MutableLiveData<Int>(-1)

    fun findCategoryByCode(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = categoryUseCase.findCategoryByCodeLive(valueFilterLive.value ?: "", lang)
    fun findContactsByCategoryAndHotel(lang: String = Language.getLangCode(
        HotelXcaretApp.mContext)): LiveData<List<Contact>> = contactUseCase.findByCategoryOutHotel(categoryCallUseLiveData.value ?: "", lang)
    fun findTitle(): LiveData<LangLabel?> = labelUseCase.findLabel(HotelXcaretApp.mContext.getString(
        R.string.rkey_call_us_from))
}