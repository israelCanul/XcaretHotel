package com.xcaret.xcaret_hotel.view.afi.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.AwardUseCase
import com.xcaret.xcaret_hotel.data.usecase.DestinationActivityUseCase
import com.xcaret.xcaret_hotel.data.usecase.DestinationUseCase
import com.xcaret.xcaret_hotel.data.usecase.ParkTourUseCase
import com.xcaret.xcaret_hotel.domain.Award
import com.xcaret.xcaret_hotel.domain.Destination
import com.xcaret.xcaret_hotel.domain.DestinationActivity
import com.xcaret.xcaret_hotel.domain.ParkTour
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Language

class FerryDetailViewModel: ViewModel(){

    private val parkTourUseCase = ParkTourUseCase()
    private val destinationActivitiesUseCase = DestinationActivityUseCase()
    private val destinationUseCase = DestinationUseCase()

    val parkLiveData = MutableLiveData<ParkTour>()
    val parkUIDLiveData = MutableLiveData<String>()
    val switchValue = MutableLiveData<Boolean>(true)
    val switchContentValue = MutableLiveData<String?>()
    val currentDestinationLive = MutableLiveData<Destination?>()
    val destinationAttractionList = MutableLiveData<List<DestinationActivity>?>()
    val destinationBenefitsList = MutableLiveData<List<DestinationActivity>?>()
    val destinationServiceList = MutableLiveData<List<DestinationActivity>?>()
    val servicesBus = MutableLiveData<DestinationActivity?>()

    fun findById(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<ParkTour> = parkTourUseCase.findById(parkUIDLiveData.value ?: "", lang)
    fun findDestinations(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Destination>> = destinationUseCase.findDestinations(lang)
    fun findDestinationActivities(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<DestinationActivity>> = destinationActivitiesUseCase.findActivitiesByDestination(currentDestinationLive.value?.uid?:" ",lang)

    fun orderDestinantionsByType(activities: List<DestinationActivity>?) {
        try {
            activities.let { it ->
                val listATT =
                    it?.filter { it.type == Constants.AFICLASS_TYPE_ATT || it.type == Constants.AFICLASS_TYPE_SRV }?.sortedBy { it.order }
                val listBNF =
                    it?.filter { it.type == Constants.AFICLASS_TYPE_BNF }?.sortedBy { it.order }
                val listSRV =
                    it?.filter { it.type == Constants.AFICLASS_TYPE_SRV }?.sortedBy { it.order }
                listATT.let {
                    destinationAttractionList.postValue(listATT)
                }
                listBNF.let {
                    destinationBenefitsList.postValue(listBNF)
                }
//                listSRV.let {
//                    destinationServiceList.postValue(listSRV)
//                }
            }
        }catch (exc:Exception){

        }

    }

}