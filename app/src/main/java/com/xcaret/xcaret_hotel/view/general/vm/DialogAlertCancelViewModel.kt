package com.xcaret.xcaret_hotel.view.general.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.domain.LangLabel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DialogAlertCancelViewModel : ViewModel() {


    private val labelUseCase = LangLabelUseCase()
    private val label = MutableLiveData<LangLabel>()

    val titleKey = MutableLiveData("")
    val dayBeforeCancelKey = MutableLiveData("")
    val h2Key = MutableLiveData("")
    val h4Key= MutableLiveData("")
    val callButtonKey = MutableLiveData("")
    val cancelButtonKey = MutableLiveData("")


    fun findLabel(key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            label.postValue(labelUseCase.findLabelOutLive(key))
        }

    }

    suspend fun findLabelAlt(key: String): LangLabel? {
        return withContext(Dispatchers.Default) {
            labelUseCase.findLabelOutLive(key)

        }
    }
}