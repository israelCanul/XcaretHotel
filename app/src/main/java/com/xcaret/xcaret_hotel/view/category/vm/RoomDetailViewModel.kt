package com.xcaret.xcaret_hotel.view.category.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.doAsync

class RoomDetailViewModel: ViewModel(){
    private val roomTypeUseCase = RoomTypeUseCase()
    private val categoryUseCase = CategoryUseCase()
    private val roomLocation = RoomLocationUseCase()
    private val amenityUseCase = RoomAmenityUseCase()

    private val suiteQuotesUseCase = SuiteQuotesUseCase()
    private val dateQuotesUseCase = DateQuotesUseCase()
    private val ratePlansUseCase = SuiteRatePlansUseCase()
    private val galleryUseCase = GalleryUseCase()

    val percentageHeightFilter = 0.33f
    val roomUID = MutableLiveData<String>("")
    val roomLiveData = MutableLiveData<RoomType>()
    val roomCategoryLiveData = MutableLiveData<Category>()
    val filterCategoryAmenity = MutableLiveData<List<Category>>()
    val listAmenities = MutableLiveData<List<RoomAmenity>>()
    val listAmenitiesSelected = MutableLiveData<List<RoomAmenity>>()
    val filterCategoryAmenitySelected = MutableLiveData<Category>()

    val dateLiveData = MutableLiveData<DateQuotes?>()
    val hasDateLivedata = MutableLiveData(false)
    val hasRatePlansLiveData = MutableLiveData(false)
    val hotelIdLiveData = MutableLiveData<Int>()
    val ratePlanLiveDate = MutableLiveData<SuiteRatePlans?>()
    var suiteQuotesSelected = MutableLiveData<SuiteQuotes?>()
    val currentHotel = MutableLiveData<Hotel?>()
    val galleryList = MutableLiveData<List<Gallery>>()
    val galleryListToShow = MutableLiveData<List<Gallery>>()

    fun findById(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<RoomType?> = roomTypeUseCase.findById(roomUID.value ?: "", lang)
    fun getLocations(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>> = roomLocation.getLocations(roomUID.value ?: "", lang)
    fun findCategoryByCode(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): Category? = categoryUseCase.findByCodeOutHotel(code, lang)
    fun fetchMainAmenitiesForRoom(roomUID: String, roomsCategory: Category?, result: (amenities: List<RoomAmenity>) -> Unit) = amenityUseCase.fetchMainAmenitiesForRoom(roomUID, roomsCategory, result)
    fun findCategoryById(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = categoryUseCase.findById(roomLiveData.value?.categoryUID ?: "", lang)
    fun allByRoomUID(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<RoomAmenity>> = amenityUseCase.allByRoomUID(roomUID.value ?: "", lang)
    fun findByListIds(ids: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<Category> = categoryUseCase.findByListIds(ids, lang)

    fun findSelectedByHotelId() = suiteQuotesUseCase.findSelectedByHotelId(hotelIdLiveData.value ?: 0)
    fun allDateByHotelId(): LiveData<DateQuotes?> = dateQuotesUseCase.allByHotelId(hotelIdLiveData.value ?: 0)
    fun findRatePlansByRoomHotelId(): LiveData<List<SuiteRatePlans>> = ratePlansUseCase.findByRoomCode(roomLiveData.value?.codeSynxis ?: "", hotelIdLiveData.value ?: 0)

    fun fetchMainAmenitiesForRoom(result: (amenities: List<RoomAmenity>) -> Unit){
        doAsync {
            val category = findCategoryByCode(AMENITY_ROOM_MAIN)
            fetchMainAmenitiesForRoom(roomUID.value ?: "", category){
                result(it)
            }
        }
    }
    fun getImages(parentUID:String){
        viewModelScope.launch {
            val lst = withContext(Dispatchers.IO){
                galleryUseCase.getImagesFromGallery(parentUID)
            }
            if (lst.isNotEmpty()){
                galleryList.value = lst
            }
        }
    }

    companion object{
        const val AMENITY_ROOM_MAIN = "AMENITY_ROOM_MAIN";
    }
}