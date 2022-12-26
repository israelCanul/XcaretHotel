package com.xcaret.xcaret_hotel.view.category.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Language

class WorkShopDetailViewModel: ViewModel() {

    private val activityUseCase  by lazy { ActivityUseCase() }
    private val placeUseCase by lazy { PlaceUseCase() }
    private val scheduleActivityUseCase by lazy { ScheduleActivityUseCase() }
    private val categoryUseCase by lazy { CategoryUseCase() }
    private val langCategoryUseCase by lazy {LangCategoryUseCase()}

    val activityUID = MutableLiveData("")
    val callFromMap = MutableLiveData(false)
    val activity = MutableLiveData<Activity>()
    val location = MutableLiveData<Place>()
    val category = MutableLiveData<Category>()
    val langCategory = MutableLiveData<LangCategory>()
    var daySelected = MutableLiveData<DayBooking>()
    var scheduleActivties = MutableLiveData<List<ScheduleActivity>>()
    var daysOfTheWeek= MutableLiveData<List<DayBooking>>()

    fun findByUID() = activityUseCase.findByUID(activityUID.value ?: "")
    fun location() = placeUseCase.findById(activity.value?.placeUID ?: "")
    fun activityWithSchedules() = scheduleActivityUseCase.activityWithSchedules(activityUID.value ?: "")
    fun findCategoryByUID() = categoryUseCase.findById(activity.value?.categoryUID ?: "")
    fun findLangCategoryByUID() =langCategoryUseCase.findLangCategory(category.value?.uid?:"", Language.getLangCode(HotelXcaretApp.mContext))


}