package com.xcaret.xcaret_hotel.view.general.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.SettingsManager
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainViewModel: ViewModel(){

    private val labelUseCase = LangLabelUseCase()
    private val categoryUseCase = CategoryUseCase()
    private val placeUseCase = PlaceUseCase()
    private val houseIdUseCase = HouseIdUseCase()
    private val levelRoomUseCase = LevelRoomUseCase()
    private val hotelUseCase = HotelUseCase()
    private val settingsManager = SettingsManager.getInstance(HotelXcaretApp.mContext)

    val navPosition = MutableLiveData<Int>(0)
    val navController = MutableLiveData<NavController>()
    val housesLiveData = MutableLiveData<List<Place>>()
    val categoryListIds = MutableLiveData<List<String>>()
    val categoryBuildingLive = MutableLiveData<List<Category>>()
    val categoryListBuildingUID = MutableLiveData(mutableListOf<String>())
    val categoryHotelLive = MutableLiveData<Category?>()
    val labelFloor = MutableLiveData<String>()
    val roomTypeLiveData = MutableLiveData<RoomType?>()
    val currentDestination = MutableLiveData<NavDestination>()
    val currentHotelLive = MutableLiveData<Hotel?>()
    val currentFragment = MutableLiveData(-1)
    val comeFromFragment = MutableLiveData("")

    val cardNumber = MutableLiveData("")
    val holderName = MutableLiveData("")
    val expiryDate = MutableLiveData("")
    val cvv = MutableLiveData("")

    val keyLabelLiveData = MutableLiveData<String>()
    val bookingData = MutableLiveData(Booking())
    val activeNextStep = MutableLiveData(false)
    val homeIsCache = MutableLiveData(false)
    var doesRequireReOrderSuites = MutableLiveData(false)

    fun getLabelByKey(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = labelUseCase.findLabel(keyLabelLiveData.value ?: "", lang)
    fun allByFilterGroup(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>> = categoryUseCase.allByListFilterGroup(categoryListBuildingUID.value ?: emptyList(), lang)
    fun findCategoryBulding(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>> = categoryUseCase.listByCodeLiveOutHotel(Constants.CATEGORY_BUILDING_CODE, lang)
    fun findCategoryHotel(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = categoryUseCase.findByCodeLiveOutHotel(Constants.CATEGORY_HOTEL, lang)
    fun findPlaceInCategory(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>> = placeUseCase.findInCategory(categoryListIds.value ?: arrayListOf(), lang)
    fun findLabelByFloor() = labelUseCase.findLabel(HotelXcaretApp.mContext.getString(R.string.rkey_level))
    suspend fun setResultAppUpdate(result: Int) = settingsManager.setResultAppUpdate(result)

    fun completeInfoByHouses(auxListeHouse: List<Place>?, response: (list: List<Place>) -> Unit){
        doAsync {
            val catSubCasa = categoryUseCase.findByCodeOutHotel("SUB_BUILDING")
            categoryBuildingLive.value?.forEach { category ->
                val building = placeUseCase.forceFindByCategoryOutHotel(catSubCasa?.uid ?: "", category.hotelUID ?: "")
                var buildingUID = mutableListOf<String>()
                building.forEach { buildingUID.add(it.uid) }
                val listHouseId = houseIdUseCase.all()
                val listExtraInfo = levelRoomUseCase.findExtraInfo(buildingUID)
                auxListeHouse?.filter { it.hotelUID == category.hotelUID }?.forEach { house ->
                    val houseId = listHouseId.firstOrNull { it.buildingUID == house.uid }
                    houseId?.let { hId -> house.houseId = hId.id }
                    house.extraInfoBuilding = listExtraInfo.firstOrNull { it.house_id == house.houseId }
                }
            }
            uiThread { response(auxListeHouse ?: listOf()) }
        }
    }
}