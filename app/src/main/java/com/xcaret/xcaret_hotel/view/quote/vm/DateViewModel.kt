package com.xcaret.xcaret_hotel.view.quote.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.data.usecase.DateQuotesUseCase
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.domain.DateQuotes
import com.xcaret.xcaret_hotel.domain.Hotel
import java.time.YearMonth

class DateViewModel: ViewModel() {
    private val dateQuotesUseCase by lazy { DateQuotesUseCase() }
    val labelUseCase by lazy { LangLabelUseCase() }

    val dateLiveDate = MutableLiveData<DateQuotes?>()
    var hotelIdLiveData = MutableLiveData<Int?>()
    var arrivalDate = MutableLiveData<String?>()
    var departureDate = MutableLiveData<String?>()
    val currentYearMonth = MutableLiveData<YearMonth>()
    val reservationYearMonth = MutableLiveData<YearMonth>()
    val currentHotel = MutableLiveData<Hotel?>()
    val firstTime = MutableLiveData(false)


    fun allByHotelId(): LiveData<DateQuotes?> = dateQuotesUseCase.allByHotelId(hotelIdLiveData.value ?: 0)
}