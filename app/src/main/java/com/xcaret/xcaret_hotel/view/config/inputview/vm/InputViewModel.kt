package com.xcaret.xcaret_hotel.view.config.inputview.vm

import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.domain.LangLabel
import com.xcaret.xcaret_hotel.view.config.Language

class InputViewModel {
    private val langLabelUseCase = LangLabelUseCase()

    val key: MutableLiveData<String> = MutableLiveData("")
    val hasTextWatcher = MutableLiveData<TextWatcher?>()

    fun findLabel(lang: String = Language.getLangCode(HotelXcaretApp.mContext)):
            LiveData<LangLabel?> = langLabelUseCase.findLabel(key.value ?: "", lang)

    fun findLabelOutLive(lang: String = Language.getLangCode(HotelXcaretApp.mContext)):
            LangLabel? = langLabelUseCase.findLabelOutLive(key.value ?: "", lang)
}