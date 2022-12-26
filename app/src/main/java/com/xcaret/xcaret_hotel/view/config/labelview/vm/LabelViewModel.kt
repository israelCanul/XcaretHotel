package com.xcaret.xcaret_hotel.view.config.labelview.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.domain.LangLabel
import com.xcaret.xcaret_hotel.view.config.Language

class LabelViewModel: ViewModel() {

    private val langLabelUseCase = LangLabelUseCase()

    val key: MutableLiveData<String> = MutableLiveData("")
    val concatText = MutableLiveData("")

    fun findLabel(lang: String = Language.getLangCode(HotelXcaretApp.mContext)):
            LiveData<LangLabel?> = langLabelUseCase.findLabel(key.value ?: "", lang)

    fun findLabelOutLive(lang: String = Language.getLangCode(HotelXcaretApp.mContext)):
            LangLabel? = langLabelUseCase.findLabelOutLive(key.value ?: "", lang)

}