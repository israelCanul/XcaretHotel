package com.xcaret.xcaret_hotel.view.category.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Language
import org.jetbrains.anko.doAsync

class RoomListViewModel: ViewModel(){
    private val roomTypeUseCase = RoomTypeUseCase()
    private val categoryUseCase = CategoryUseCase()
    private val amenityUseCase = RoomAmenityUseCase()
    private val suiteQuotesUseCase = SuiteQuotesUseCase()
    private val dateQuotesUseCase = DateQuotesUseCase()
    private val ratePlansUseCase = SuiteRatePlansUseCase()

    val hotelUIDLiveData = MutableLiveData<String>("")
    val loadingLiveData = MutableLiveData<Boolean>(true)
    val categoryLiveData = MutableLiveData<Category>()
    val categorySelectedLiveData = MutableLiveData<Category>()
    val percentageHeightFilter = 0.33f

    val dateLiveData = MutableLiveData<DateQuotes?>()
    val hasDateLivedata = MutableLiveData(false)
    val hotelIdLiveData = MutableLiveData<Int>()
    val suiteQuoteSelected = MutableLiveData<SuiteQuotes?>()
    val hotelLive = MutableLiveData<Hotel>()

    fun all(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<RoomType>> = roomTypeUseCase.all(hotelUIDLiveData.value ?: "", lang)
    fun findByCategory(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<RoomType>> = roomTypeUseCase.findByCategory(categorySelectedLiveData.value?.uid ?: "", lang)
    fun fetchCategoryForRooms(roomsType: List<RoomType>, result: (success: Boolean) -> Unit) = categoryUseCase.fetchForRooms(roomsType, result)
    fun fetchMainAmenitiesForRooms(roomsType: List<RoomType>, roomsCategory: Category?, result: (success: Boolean) -> Unit) = amenityUseCase.fetchMainAmenitiesForRooms(roomsType, roomsCategory, result)
    fun findCategoryByCode(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): Category? = categoryUseCase.findByCode(code, lang)
    fun findCategoryByCodeNoHotel(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): Category? = categoryUseCase.findByCodeNoHotel(code, lang)
    fun findCategoryByCodeLive(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = categoryUseCase.findByCodeLive(Constants.CATEGORY_ROOM_CODE, lang)
    fun allByFilterGroup(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>> = categoryUseCase.allByFilterGroup(categoryLiveData.value?.uid ?: "", lang)

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
}