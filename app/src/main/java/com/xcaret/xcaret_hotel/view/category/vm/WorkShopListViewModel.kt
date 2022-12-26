package com.xcaret.xcaret_hotel.view.category.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.ActivityUseCase
import com.xcaret.xcaret_hotel.data.usecase.CategoryUseCase
import com.xcaret.xcaret_hotel.data.usecase.PlaceUseCase
import com.xcaret.xcaret_hotel.domain.Activity
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.view.config.Language
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class WorkShopListViewModel: ViewModel() {
    private val categoryUseCase by lazy { CategoryUseCase() }
    private val activityUseCase by lazy { ActivityUseCase() }
    private val placeUseCase by lazy { PlaceUseCase() }

    val categoryUID = MutableLiveData("")
    val categorySelectedLiveData = MutableLiveData<Category?>()
    val loading = MutableLiveData(true)
    val categoryLiveData = MutableLiveData<Category?>()
    val workShopList = MutableLiveData<List<Activity>>()

    fun allByFilterGroup(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>> = categoryUseCase.allByFilterGroup(categoryUID.value ?: "", lang)
    fun allActivitiesByCategory() = activityUseCase.allByCategory(categorySelectedLiveData.value?.uid ?: "")
    fun findCategoryByUID() = categoryUseCase.findById(categoryUID.value ?: "")

    fun completeLocations(list: List<Activity>, ok: () -> Unit){
        doAsync {
            if (list.isEmpty()) uiThread { ok() }
            else {
                val placeUIDs = mutableListOf<String>()
                list.forEach {
                    if (!placeUIDs.contains(it.placeUID)) placeUIDs.add(it.placeUID ?: "")
                }
                if (placeUIDs.isEmpty()) uiThread { ok() }
                else {
                    val places = placeUseCase.findWithIn(placeUIDs)
                    list.forEach { activity ->
                        activity.location = places.find { it.uid == activity.placeUID}
                    }
                    uiThread { ok() }
                }
            }
        }
    }
}