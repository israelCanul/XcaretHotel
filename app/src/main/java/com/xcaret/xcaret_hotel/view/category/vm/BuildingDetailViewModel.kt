package com.xcaret.xcaret_hotel.view.category.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Language
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class BuildingDetailViewModel: ViewModel(){

    private val categoryUseCase = CategoryUseCase()
    private val placeUseCase = PlaceUseCase()
    private val roomTypeUseCase = RoomTypeUseCase()
    private val nearPlaceUseCase = NearPlaceUseCase()
    private val amenityUseCase = RoomAmenityUseCase()
    private val labelUseCase = LangLabelUseCase()
    private val suiteQuotesUseCase = SuiteQuotesUseCase()
    private val dateQuotesUseCase = DateQuotesUseCase()
    private val ratePlansUseCase = SuiteRatePlansUseCase()

    val percentageHeightFilter = 0.31f
    val welcomeLabelLive = MutableLiveData<LangLabel>()
    val categoryLiveData = MutableLiveData<Category?>()
    val categoryRoomLiveData = MutableLiveData<Category?>()
    val categoryRestLiveData = MutableLiveData<Category?>()
    val categoryBarLiveData = MutableLiveData<Category?>()
    val categorySelectedLiveData = MutableLiveData<Category>()
    val categoryUIDSelected = MutableLiveData<String>("")
    val placeLiveData = MutableLiveData<Place?>()
    val filterGroupRest = MutableLiveData<List<Category?>>()
    val hotelLive = MutableLiveData<Hotel>()

    val dateLiveData = MutableLiveData<DateQuotes?>()
    val hasDateLivedata = MutableLiveData(false)
    val hotelIdLiveData = MutableLiveData<Int>()
    val suiteQuoteSelected = MutableLiveData<SuiteQuotes?>()

    fun findCategoryBuilding(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = findCategoryByCodeLive(Constants.CATEGORY_BUILDING_CODE, lang)
    fun findCategoryRoom(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = findCategoryByCodeLive(Constants.CATEGORY_ROOM_CODE, lang)
    fun findCategoryRestaurant(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = findCategoryByCodeLive(Constants.CATEGORY_REST_CODE, lang)
    fun findCategoryBar(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = findCategoryByCodeLive(Constants.CATEGORY_REST_BAR_SNACK, lang)
    fun findByCategory(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>> = placeUseCase.findByCategory(categorySelectedLiveData.value?.uid ?: "", lang)
    fun allByFilterGroup(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>> = categoryUseCase.allByFilterGroup(categoryLiveData.value?.uid ?: "", lang)
    fun findByLocation(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<RoomType>> = roomTypeUseCase.findByLocation(placeLiveData.value?.uid ?: "", lang)
    fun findCategoryByFilterGroup(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>> = categoryUseCase.allByFilterGroup(categoryRestLiveData.value?.uid ?: "", lang)
    fun findNearPlace(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>> = nearPlaceUseCase.findNearPlace(placeLiveData.value?.uid ?: "", lang)
    fun allCategoryRoom(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>> = categoryUseCase.allByFilterGroup(categoryRoomLiveData.value?.uid ?: "", lang)

    private fun findCategoryByCodeLive(categoryCode: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = categoryUseCase.findByCodeLive(categoryCode, lang)
    fun fetchMainAmenitiesForRooms(roomsType: List<RoomType>, roomsCategory: Category?, result: (success: Boolean) -> Unit) = amenityUseCase.fetchMainAmenitiesForRooms(roomsType, roomsCategory, result)
    fun findCategoryByCode(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): Category? = categoryUseCase.findByCode(code, lang)
    fun findCategoryByCodeNoHotel(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): Category? = categoryUseCase.findByCodeNoHotel(code, lang)
    fun fetchCategoryForRooms(roomsType: List<RoomType>, result: (success: Boolean) -> Unit) = categoryUseCase.fetchForRooms(roomsType, result)

    fun findPlaceWithIn(placeIDS: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<Place> = placeUseCase.findWithIn(placeIDS, lang)

    fun findWelcomenLabel() = findLabelByKey(HotelXcaretApp.mContext.getString(R.string.rkey_welcome_to))
    private fun findLabelByKey(key: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = labelUseCase.findLabel(key, lang)

    //region quotes
    fun saveOrUpdateListSuites(list: List<SuiteQuotes>) = suiteQuotesUseCase.saveList(list)
    fun allDateByHotelId(): LiveData<DateQuotes?> = dateQuotesUseCase.allByHotelId(hotelIdLiveData.value ?: 0)
    fun allSuitesByHotelIdExceptAdd(): LiveData<List<SuiteQuotes>> = suiteQuotesUseCase.allByHotelIdExceptAdd(hotelIdLiveData.value ?: 0)
    fun findRatePlansByRoomHotelId(): LiveData<List<SuiteRatePlans>> = ratePlansUseCase.findByRoomId(suiteQuoteSelected.value?.id ?: 0, hotelIdLiveData.value ?: 0)
    //end quotes

    fun fetchCategoryAndMainAmenities(roomsType: List<RoomType>, result: (success: Boolean) -> Unit){
        doAsync {
            val category = findCategoryByCodeNoHotel(Constants.AMENITY_ROOM_MAIN)
            fetchCategoryForRooms(roomsType) {
                fetchMainAmenitiesForRooms(roomsType, category) {
                    result(it)
                }
            }
        }
    }

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
            list.forEach {p ->
                p.location = locationPlaces.firstOrNull { it.uid == p.parentUID }
            }
            uiThread { result(true) }
        }
    }
}