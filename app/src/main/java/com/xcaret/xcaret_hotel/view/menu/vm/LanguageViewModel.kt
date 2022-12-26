package com.xcaret.xcaret_hotel.view.menu.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.data.usecase.LanguageUseCase
import com.xcaret.xcaret_hotel.domain.LangLabel
import com.xcaret.xcaret_hotel.domain.Language

class LanguageViewModel: ViewModel(){

    private val languageUseCase = LanguageUseCase()
    private val labelUseCase = LangLabelUseCase()

    val currentPosition = MutableLiveData<Int>(-1)

    fun all(): LiveData<List<Language>> = languageUseCase.all()
    fun findTitle(): LiveData<LangLabel?> = labelUseCase.findLabel(HotelXcaretApp.mContext.getString(R.string.rkey_select_your_language))
}