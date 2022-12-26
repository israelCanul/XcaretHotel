package com.xcaret.xcaret_hotel.view.tabs.vm

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Language
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MapViewModel: ViewModel(){
    private val mapConfigUseCase = MapConfigUseCase()
    private val filterMapUseCase = FilterMapUseCase()
    private val placeUseCase = PlaceUseCase()
    private val categoryUseCase = CategoryUseCase()
    private val labelUseCase = LangLabelUseCase()
    private val hotelUseCase = HotelUseCase()
    private val userUseCase = UserUseCase()

    val filterSelectedLive = MutableLiveData<FilterMap?>()
    val mapConfigLive = MutableLiveData<MapConfig?>()
    val readyMap = MutableLiveData<Boolean>(false)
    val categoryLive = MutableLiveData<String>()
    val categoryListLive = MutableLiveData<List<String>>()
    val placesLive = MutableLiveData<List<Place>>()
    val hotelLive = MutableLiveData<Hotel>()
    val oldSelectedPos = MutableLiveData<Int>(-1)
    val currentPosSelected = MutableLiveData<Int>(0)
    val searchQueryLive = MutableLiveData("")
    val alertEmptyResultLive = MutableLiveData("")
    val isOpenSearchInput = MutableLiveData(false)
    val limitLatLngBoundsLive = MutableLiveData<LatLngBounds>()
    val myLiveLocation = MutableLiveData<LatLng>()
    val listCatHomesUID = MutableLiveData<List<String>>()
    val listCatRestUID = MutableLiveData<List<String>>()
    val listCatPoolUID = MutableLiveData<List<String>>()
    val placeUIDLive = MutableLiveData<String>()
    val auxMarkerList = MutableLiveData(mutableListOf<Marker>())
    val showMyLocation = MutableLiveData(true)
    val uidLiveData = MutableLiveData<String>()

    fun allHotel(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = hotelUseCase.all(lang)
    fun findConfig() = mapConfigUseCase.first(Constants.PLATFORM)
    fun findFiltersByHotel(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) =  filterMapUseCase.findByHotel(hotelLive.value?.uid ?: "", lang)
    fun findByListCategory(): LiveData<List<Place>> = placeUseCase.findInCategory(categoryListLive.value ?: listOf(), hotelUID = hotelLive.value?.uid ?: "")
    fun allByFilterGroup(): LiveData<List<Category>> = categoryUseCase.allByFilterGroup(categoryLive.value ?: "")
    fun allByHotel(type: String = Constants.PLACE, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>> = placeUseCase.allByHotel(hotelLive.value?.uid ?: "", type, lang)
    fun search() = placeUseCase.search(searchQueryLive.value ?: "", hotelLive.value?.uid ?: "", Language.getLangCode(HotelXcaretApp.mContext))
    fun hintSearch(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = findLabel(HotelXcaretApp.mContext.getString(R.string.rkey_map_search), lang)
    fun searchAlertEmptyResult(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = findLabel(HotelXcaretApp.mContext.getString(R.string.rkey_alert_empty_search), lang)
    fun findCategoryUIHouseByList(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = findByListCodes(Constants.LIST_CATEGORY_HOMES, lang)
    fun findCategoryUIRestByList(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = findByListCodes(Constants.LIST_CATEGORY_ONLY_REST, lang)
    fun findCategoryUIPoolByList(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = findByListCodes(Constants.LIST_CATEGORY_POOLS, lang)
    fun findWithIn(placeIDS: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<Place> = placeUseCase.findWithIn(placeIDS, lang, hotelLive.value?.uid ?: "")

    fun findLabel(key: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = labelUseCase.findLabel(key, lang)
    private fun findByListCodes(codes: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<String>> = categoryUseCase.findByListCodes(codes, hotelLive.value?.uid ?: "", lang)

    fun completeCategory(list: List<Place>, response: (success: Boolean) -> Unit){
        doAsync {
            val categoryIds = mutableListOf<String>()
            list.forEach {place ->
                place.categoryUID?.let { catUID ->
                    if(!categoryIds.contains(catUID) && catUID.trim().isNotEmpty())
                        categoryIds.add(catUID)
                }
            }
            val categories = categoryUseCase.findByListIds(categoryIds)
            list.forEach {place ->
                place.category = categories.firstOrNull { it.uid == place.categoryUID }
            }
            uiThread { response(true) }
        }
    }


    fun getSession() = userUseCase.getSession(uidLiveData.value ?: "")

}