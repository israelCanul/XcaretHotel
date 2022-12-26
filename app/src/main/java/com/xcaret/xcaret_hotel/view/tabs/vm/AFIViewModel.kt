package com.xcaret.xcaret_hotel.view.tabs.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.AFIClassUseCase
import com.xcaret.xcaret_hotel.data.usecase.CategoryUseCase
import com.xcaret.xcaret_hotel.data.usecase.ParkTourUseCase
import com.xcaret.xcaret_hotel.data.usecase.WebCamUseCase
import com.xcaret.xcaret_hotel.domain.AfiClass
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.ParkTour
import com.xcaret.xcaret_hotel.domain.WebCam
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Language

class AFIViewModel: ViewModel(){

    private val categoryUseCase = CategoryUseCase()
    private val parkTourUseCase = ParkTourUseCase()
    private val webCamUseCase = WebCamUseCase()
    private val afiClassesUseCase = AFIClassUseCase()

    val categoryLiveData = MutableLiveData<Category?>()
    val categorySelectedLiveData = MutableLiveData<Category?>()
    val videoLiveData = MutableLiveData<WebCam?>()
    var afiClassSelectedLiveData = MutableLiveData<AfiClass?>()
    var afiClassesLiveData = MutableLiveData<List<AfiClass?>>()
    val percentageHeightFilter = 0.3f

    fun findCategoryByCode(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = categoryUseCase.findByCodeLiveOutHotel(Constants.CATEGORY_AFI_CODE, lang)
    fun allByFilterGroup(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>> = categoryUseCase.allByFilterGroup(categoryLiveData.value?.uid ?: "", lang)
    fun findByCategory(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<ParkTour>> = parkTourUseCase.getByCategory(categorySelectedLiveData.value?.uid ?: "", lang)
    fun findByClass(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<ParkTour>> = parkTourUseCase.getByClassUID(afiClassSelectedLiveData.value?.uid ?: "", lang)
    fun findVideoByCode(): LiveData<WebCam?> = webCamUseCase.findByCode(Constants.VIDEO_AFI_CODE)
    fun findAFIClasses(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = afiClassesUseCase.findAllAFIClasses()
    fun findAFIClassByUID(classUID: String) = afiClassesUseCase.findAFIClassByUID(classUID)
}