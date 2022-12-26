package com.xcaret.xcaret_hotel.view.menu.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xcaret.xcaret_hotel.data.usecase.FaqUseCase
import com.xcaret.xcaret_hotel.domain.Faq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FaqDetailViewModel: ViewModel() {


    private val faqUseCase = FaqUseCase()

    val FaqList = MutableLiveData<List<Faq>>()
    var parentUid = MutableLiveData("")
    val FAQTitle = MutableLiveData("")

    fun getFaqs() {
        viewModelScope.launch {
            val faqs = withContext(Dispatchers.IO) {
                faqUseCase.getFaqs(parentUid.value!!)
            }

            if (faqs.isNotEmpty()){
                FaqList.value = faqs
            }
        }

    }

}
