package com.xcaret.xcaret_hotel.view.afi.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.AwardUseCase
import com.xcaret.xcaret_hotel.data.usecase.ParkTourUseCase
import com.xcaret.xcaret_hotel.domain.Award
import com.xcaret.xcaret_hotel.domain.ParkTour
import com.xcaret.xcaret_hotel.view.config.Language

class ParkDetailViewModel: ViewModel(){

    private val parkTourUseCase = ParkTourUseCase()
    private val awardUseCase = AwardUseCase()

    val parkLiveData = MutableLiveData<ParkTour>()
    val parkUIDLiveData = MutableLiveData<String>()
    val switchValue = MutableLiveData<Boolean>(true)
    val switchContentValue = MutableLiveData<String?>()

    fun findById(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<ParkTour> = parkTourUseCase.findById(parkUIDLiveData.value ?: "", lang)
    fun findAwardByParkUID(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Award>> = awardUseCase.findByParkUID(parkUIDLiveData.value ?: "", lang)
}