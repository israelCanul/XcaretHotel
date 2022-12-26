package com.xcaret.xcaret_hotel.view.category.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.CategoryUseCase
import com.xcaret.xcaret_hotel.data.usecase.LangRestaurantDetailUseCase
import com.xcaret.xcaret_hotel.data.usecase.PlaceUseCase
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.LangRestaurantDetail
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.domain.RoomType
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Language
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class RestaurantListViewModel: ViewModel(){

    private val categoryUseCase = CategoryUseCase()
    private val placeUseCase = PlaceUseCase()
    private val restaurantDetailUseCase = LangRestaurantDetailUseCase()

    val loadingLiveData = MutableLiveData<Boolean>(true)
    val categoryLiveData = MutableLiveData<Category>()
    val categorySelectedLiveData = MutableLiveData<Category>()
    val percentageHeightFilter = 0.33f

    fun findCategoryByCodeLive(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = categoryUseCase.findByCodeLive(Constants.CATEGORY_REST_CODE, lang)
    fun findByCategory(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>> = placeUseCase.findByCategory(categorySelectedLiveData.value?.uid ?: "", lang)
    fun allByFilterGroup(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>> = categoryUseCase.allByFilterGroup(categoryLiveData.value?.uid ?: "", lang)
    fun `in`(restIds: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<LangRestaurantDetail> = restaurantDetailUseCase.`in`(restIds, lang)
    fun findById(restId: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<LangRestaurantDetail?> = restaurantDetailUseCase.findById(restId, lang)
    fun findPlaceWithIn(placeIDS: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<Place> = placeUseCase.findWithIn(placeIDS, lang)

    fun completeInfo(list: List<Place>, result: (end: Boolean) -> Unit){
        doAsync {
            val restIds = mutableListOf<String>()
            val locationIDS = mutableListOf<String>()
            list.forEach {
                restIds.add(it.uid)
                it.parentUID?.let { parentUID ->
                    if(!locationIDS.contains(parentUID))
                        locationIDS.add(parentUID)
                }
            }
            val locationPlaces = findPlaceWithIn(locationIDS)
            val listDetail = `in`(restIds.toList())
            list.forEach {p ->
                p.location = locationPlaces.firstOrNull { it.uid == p.parentUID }
                p.restaurantDetail = listDetail.firstOrNull{ it.parent_uid == p.uid}
            }
            uiThread { result(true) }
        }
    }
}