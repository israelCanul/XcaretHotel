package com.xcaret.xcaret_hotel.view.quote.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.LogHX
import com.xcaret.xcaret_hotel.view.config.Session
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.doAsync

class QuoteViewModel: ViewModel() {
    private val suiteQuotesUseCase = SuiteQuotesUseCase()
    private val dateQuotesUseCase = DateQuotesUseCase()
    private val roomTypeUseCase = RoomTypeUseCase()
    private val suiteRatePlansUseCase = SuiteRatePlansUseCase()
    private val categoryUseCase = CategoryUseCase()
    private val amenityUseCase = RoomAmenityUseCase()
    private val userUseCase = UserUseCase()
    private val currencyUseCase = CurrencyUseCase()


    val suiteQuoteSelected = MutableLiveData<SuiteQuotes?>()
    val dateQuotesLiveData = MutableLiveData<DateQuotes?>()
    val temporalDateQuotesLive = MutableLiveData<DateQuotes?>()
    val suiteQuotesLiveData = MutableLiveData<List<SuiteQuotes>>()
    val hotelIdSelected = MutableLiveData<Int>()
    val responseQuotesLive = MutableLiveData<ResponseQuotes?>()
    val activeLiveCheckStep = MutableLiveData(false)
    val totalLiveData = MutableLiveData("")
    val showBalanceGeneral = MutableLiveData(false)
    val bandNotUpdateRecycler = MutableLiveData(false) //Auxiliar, silo solo selecciona, entonces no actualizo todo el recycler
    val error = MutableLiveData(StatusResponse.NONE)
    val userLiveData = MutableLiveData<User>()
    val heightDefaultToolbar = MutableLiveData(0f)
    val posYDefaultBottomSheet = MutableLiveData(0f)
    val maxHeightTabs = MutableLiveData(0f)
    val minHeightTabs = MutableLiveData(0f)
    val heightHeaderQuotes = MutableLiveData(0f)
    val listPriceNights = MutableLiveData(mutableListOf<QuotesRoomRatePlansNights>())
    val currentHotel = MutableLiveData<Hotel?>()
    var isInfoCleaned = MutableLiveData<Boolean?>()
    var currency = MutableLiveData<Currency?>()
    var updateCurrency = MutableLiveData(false)
    var quotesStatus = MutableLiveData(-1)


    //first validate quotes
    val hasValidate = MutableLiveData(false)
    val listSuiteQuotesToUpdate = MutableLiveData<MutableList<SuiteQuotes>>(mutableListOf())

    fun getSession() = userUseCase.getSession(Session.getUID(HotelXcaretApp.mContext) ?: "")
    fun findDateByHotelId() = dateQuotesUseCase.allByHotelId(hotelIdSelected.value ?: 0)
    fun saveDate(dateQuotes: DateQuotes) = dateQuotesUseCase.save(dateQuotes)

    fun findSuiteByHotelId(): LiveData<List<SuiteQuotes>> = suiteQuotesUseCase.allByHotelId(hotelIdSelected.value ?: 0)

    fun saveSuiteQuotes(suiteQuote: List<SuiteQuotes>): List<Long> = suiteQuotesUseCase.saveList(suiteQuote)
    fun updateSingleSuiteQuote(suiteQuote: SuiteQuotes):Int = suiteQuotesUseCase.update(suiteQuote)
    fun deleteSuiteQuotes() = suiteQuotesUseCase.deleteByHotel(hotelIdSelected.value ?: 0)
    fun quotes(request: SuiteQuotes, result: (res: ResponseQuotes) -> Unit) = suiteRatePlansUseCase.quotes(request, result)
    fun liveCheck(suiteQuotes: SuiteQuotes, result: (res: ResponseLiveCheck) -> Unit) = suiteRatePlansUseCase.liveCheck(suiteQuotes, result = result)

    fun saveRatePlans(ratesPlans: List<SuiteRatePlans>) = suiteRatePlansUseCase.saveList(ratesPlans)
    fun deleteRatePlans(roomId: Long, hotelId: Int) = suiteRatePlansUseCase.removeByRoomId(roomId, hotelId)
    fun findByRoomId(): LiveData<List<SuiteRatePlans>> = suiteRatePlansUseCase.findByRoomId(suiteQuoteSelected.value?.id ?: 0, hotelIdSelected.value ?: 0)

    private fun findRoomByListCodes(codes: List<String>) = roomTypeUseCase.findByListCodes(codes)

    fun getUser() = userUseCase.getUser()

    fun fetchMainAmenitiesForRooms(roomsType: List<RoomType>, roomsCategory: Category?, result: (success: Boolean) -> Unit) = amenityUseCase.fetchMainAmenitiesForRooms(roomsType, roomsCategory, result)
    fun findCategoryByCode(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): Category? = categoryUseCase.findByCodeOutHotel(code, lang)
    fun fetchCategoryForRooms(roomsType: List<RoomType>, result: (success: Boolean) -> Unit) = categoryUseCase.fetchForRooms(roomsType, result)

    fun addNights(list: List<QuotesRoomRatePlansNights>){
        if(list.isNotEmpty()){
            val first = list.first()
            removeNigths(first.roomCode ?: "", first.ratePlanCode, first.hotelId ?: 0)
        }
        list.forEach { item ->
            listPriceNights.value?.add(item)
        }
    }

    fun removeNigths(roomCode: String, ratePlanCode: String, hotelId: Int){
        val list = getNigths(roomCode, ratePlanCode, hotelId)
        list.forEach {
            listPriceNights.value?.remove(it)
        }
        /*if(list.isNotEmpty()){
            val first = list.first()
            getNigths(first.roomCode ?: "", first.ratePlanCode, first.hotelId ?: 0).forEach { item ->
                val index = listPriceNights.value?.indexOf(item) ?: -1
                if(index > 0 && index < listPriceNights.value?.size ?: 0)
                    listPriceNights.value?.removeAt(index)
            }
        }*/
    }

    fun getNigths(roomCode: String, ratePlanCode: String, hotelId: Int): List<QuotesRoomRatePlansNights>{
        return listPriceNights.value?.filter { it.hotelId == hotelId
                && it.roomCode.equals(roomCode, ignoreCase = true)
                && it.ratePlanCode.equals(ratePlanCode, ignoreCase = true) } ?: emptyList()
    }

    fun fetchCategoryAndMainAmenities(roomsType: List<RoomType>, result: (success: Boolean) -> Unit){
        doAsync {
            val category = findCategoryByCode(Constants.AMENITY_ROOM_MAIN)
            fetchCategoryForRooms(roomsType) {
                fetchMainAmenitiesForRooms(roomsType, category) {
                    result(it)
                }
            }
        }
    }

    fun completeRatePlanInfoToRooms(listRatePlans: List<SuiteRatePlans>, response: (List<RoomType>) -> Unit){
        doAsync {
            val roomsCodes = mutableListOf<String>()
            listRatePlans.forEach { rp ->
                //deleteRatePlans(rp.roomId, rp.hotelCode)
                if(!roomsCodes.contains(rp.roomCode)) roomsCodes.add(rp.roomCode)
            }
            val roomsType = findRoomByListCodes(roomsCodes)
            val ordersRoomType = mutableListOf<RoomType>()

            fetchCategoryAndMainAmenities(roomsType){
                roomsType.forEach { r ->
                    r.isSelected = suiteQuoteSelected.value?.suiteCodeSelected.equals(r.codeSynxis, ignoreCase = true)
                    r.ratePlan.addAll(listRatePlans.filter { it.roomCode.equals(r.codeSynxis, ignoreCase = true) })
                }

                //ordenar de mayor a menor
                listRatePlans.forEach { rp ->
                    roomsType.firstOrNull { it.codeSynxis.equals(rp.roomCode, ignoreCase = true) }?.let { rt ->
                        if(!ordersRoomType.contains(rt)) ordersRoomType.add(rt)
                    }
                }
                response(ordersRoomType)
            }
        }
    }

    fun findCurrency(iso:String) =currencyUseCase.finByIso(iso)

    fun hideTabs(visible :Boolean):List<SuiteQuotes>?{
        var list: List<SuiteQuotes>? = null
        if (!suiteQuotesLiveData.value.isNullOrEmpty()){
            list = suiteQuotesLiveData.value
            list?.forEach {
                it.visible = visible
            }

        }
        return list
    }


}