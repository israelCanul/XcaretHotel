package com.xcaret.xcaret_hotel.view.general.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.domain.LangLabel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DialogSnackViewModel :ViewModel() {

    private val labelUseCase = LangLabelUseCase()

    val drawable = MutableLiveData(0)
    val messageKey = MutableLiveData("")
    val confirmButtonKey = MutableLiveData("")
    val cancelButtonKey = MutableLiveData("")

     suspend fun findLabelAlt(key:String):LangLabel? {
        return withContext( Dispatchers.Default) {
           labelUseCase.findLabelOutLive(key)
        }
     }


}