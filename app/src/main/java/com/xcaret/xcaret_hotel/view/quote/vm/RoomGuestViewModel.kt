package com.xcaret.xcaret_hotel.view.quote.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.data.usecase.SuiteQuotesUseCase
import com.xcaret.xcaret_hotel.domain.Hotel
import com.xcaret.xcaret_hotel.domain.SuiteQuotes

class RoomGuestViewModel: ViewModel() {
    private val suiteQuotesUseCase = SuiteQuotesUseCase()

    val totalSuite = MutableLiveData(0)
    val totalVisitors = MutableLiveData(0)
    val currentAdults = MutableLiveData(0)
    val currentChildren = MutableLiveData(0)
    val hotelIdLive = MutableLiveData(0)
    val currentTabSelected = MutableLiveData<SuiteQuotes?>()
    val sizeTabs = MutableLiveData(0)
    val maxTabs = MutableLiveData(0)
    val currentHotel = MutableLiveData<Hotel?>()

    fun allByHotelId(): LiveData<List<SuiteQuotes>> = suiteQuotesUseCase.allByHotelId(hotelIdLive.value ?: 0)
}