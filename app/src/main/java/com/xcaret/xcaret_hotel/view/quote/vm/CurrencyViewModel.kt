package com.xcaret.xcaret_hotel.view.quote.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.data.usecase.CurrencyUseCase
import com.xcaret.xcaret_hotel.domain.Currency

class CurrencyViewModel: ViewModel() {

    private val currencyUseCase = CurrencyUseCase()
    val data = MutableLiveData<List<Currency>>()

    fun all() = currencyUseCase.all()
}