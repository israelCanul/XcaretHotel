package com.xcaret.xcaret_hotel.data.usecase

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.data.api.LiveCheckApiService
import com.xcaret.xcaret_hotel.data.api.QuotesApiService
import com.xcaret.xcaret_hotel.data.entity.LiveCheckResponseEntity
import com.xcaret.xcaret_hotel.data.entity.QuotesResponseEntity
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.data.room.dao.SuiteRatePlansDao
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.LogHX
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class SuiteRatePlansUseCase: BaseUseCase<SuiteRatePlans>() {

    private val TAG = "SuiteRatePlansUseCase"
    private val dao: SuiteRatePlansDao by lazy { database.suiteRatePlansDao() }
    private val quotesApi: QuotesApiService by lazy { QuotesApiService() }
    private val liveCheckApi: LiveCheckApiService by lazy { LiveCheckApiService() }

    override fun getDao(): BaseDao<SuiteRatePlans> = dao
    override fun getRepository(): FirebaseDatabaseRepository<SuiteRatePlans>? = null

    fun saveList(list: List<SuiteRatePlans>) = dao.insertAll(list)
    fun removeByRoomId(id: Long, hotelId: Int): Int = dao.removeByRoomId(id, hotelId)
    fun removeByHotel(hotelId: Int): Int = dao.removeByHotel(hotelId)
    fun findByRoomId(id: Long, hotelId: Int): LiveData<List<SuiteRatePlans>> = dao.findByRoomId(id, hotelId)
    fun findByRoomCode(code: String, hotelId: Int): LiveData<List<SuiteRatePlans>> = dao.findByRoomCode(code, hotelId)
    fun truncate() = dao.truncate()
    fun findByRoomIdRoomCodeAndHotel(roomId: Long, roomCode: String, hotelId: Int, rateCode: String): SuiteRatePlans? = dao.findByRoomIdRoomCodeAndHotel(roomId, roomCode, hotelId, rateCode)

    @SuppressLint("CheckResult")
    fun quotes(request: SuiteQuotes, result: (res: ResponseQuotes) -> Unit){
        try{
            quotesApi.quotes(request.toRequest())
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.computation())
                .subscribeWith(object: DisposableSingleObserver<QuotesResponseEntity>(){
                    override fun onSuccess(t: QuotesResponseEntity) {
                        LogHX.e("Quotes Success", t.toString())

                        result(ResponseQuotes(true, result = mapper(t, request)))
                    }

                    override fun onError(e: Throwable) {
                        LogHX.e("Quotes Error", e.localizedMessage.toString()?: "")
                        result(ResponseQuotes(false, 0))
                    }

                })
        }
        catch (e: Exception){
            LogHX.e(TAG, e.localizedMessage ?: "error quotes")
            result(ResponseQuotes(false, 0))
        }
    }

    @SuppressLint("CheckResult")
    fun liveCheck(suiteQuotes: SuiteQuotes, result: (res: ResponseLiveCheck) -> Unit){
        try{
            Log.e("LiveCheck Request", suiteQuotes.toRequestLiveCheck().toString())
            liveCheckApi.livecheck(suiteQuotes.toRequestLiveCheck())
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.computation())
                .subscribeWith(object: DisposableSingleObserver<LiveCheckResponseEntity>(){
                    override fun onSuccess(t: LiveCheckResponseEntity) {
                        Log.e("LiveCheck success", t.toString())
                        val existRoom = t.hotel?.rooms?.find { it.roomCode.equals(suiteQuotes.suiteCodeSelected, ignoreCase = true) } != null
                        result(
                            ResponseLiveCheck(
                                success = true,
                                errorCode = if(existRoom) StatusResponse.SUCCESS else StatusResponse.EMPTY,
                                errorSuite = null
                            )
                        )
                    }

                    override fun onError(e: Throwable) {
                        LogHX.e(TAG, e.localizedMessage ?: "error quotes")
                        result(
                            ResponseLiveCheck(
                            success = false,
                            errorCode = StatusResponse.SERVER
                        ))
                    }
                })
        }
        catch (e: java.lang.Exception){
            LogHX.e(TAG, e.localizedMessage ?: "error quotes")
            result(
                ResponseLiveCheck(
                    success = false,
                    errorCode = StatusResponse.SERVER
                ))
        }
    }

    private fun mapper(response: QuotesResponseEntity, request: SuiteQuotes): List<SuiteRatePlans> {
        val suiteRatePlans = mutableListOf<SuiteRatePlans>()
        try {
            if (response.hotels?.isNullOrEmpty() != true) {
                val responseHotel = response.hotels[0]
                if (responseHotel.rooms?.isNullOrEmpty() != true) {
                    val responseRooms = responseHotel.rooms
                    responseRooms.forEach { room ->
                        if (room.ratePlanes?.isNullOrEmpty() != true) {
                            room.ratePlanes.forEach { ratePlan ->
                                val suiteRate = SuiteRatePlans()
                                suiteRate.roomId = request.id
                                suiteRate.roomCode = room.roomCode ?: ""
                                suiteRate.roomName = room.name ?: ""
                                suiteRate.hotelCode = responseHotel.hotelCode ?: 0
                                suiteRate.hotelName = responseHotel.name ?: ""
                                suiteRate.currency = responseHotel.currency ?: ""
                                suiteRate.startDate = request.startDate
                                suiteRate.endDate = request.endDate
                                suiteRate.ratePlanCode = ratePlan.ratePlanCode ?: ""
                                suiteRate.ratePlanName = ratePlan.name ?: ""
                                suiteRate.ratePlanDescription = ratePlan.description
                                suiteRate.baseCurrency = ratePlan.baseCurrency ?: ""
                                suiteRate.guaranteeCode = ratePlan.guarantee?.code ?: ""
                                suiteRate.guaranteeDescription =
                                    ratePlan.guarantee?.description ?: ""
                                suiteRate.guaranteeChargeRequired =
                                    ratePlan.guarantee?.chargeRequired == true
                                suiteRate.guaranteeChargePercent =
                                    ratePlan.guarantee?.chargePercent ?: 0.0
                                suiteRate.mealsIncluded = ratePlan.mealsIncluded == true
                                suiteRate.amount = ratePlan.amount ?: 0.0
                                suiteRate.normalAmount = ratePlan.normalAmount ?: 0.0
                                suiteRate.averageAmount = ratePlan.averageAmount ?: 0.0

                                ratePlan.nights?.forEach { night ->
                                    suiteRate.priceForNights.add(
                                        QuotesRoomRatePlansNights(
                                            ratePlanCode = suiteRate.ratePlanCode,
                                            normalAmount = night.normalAmount ?: 0.0,
                                            amount = night.amount ?: 0.0,
                                            date = night.date ?: "",
                                            hotelId = responseHotel.hotelCode ?: 0,
                                            roomCode = room.roomCode ?: ""
                                        )
                                    )
                                }

                                if (ratePlan.policies?.isNullOrEmpty() != true) {
                                    val responsePolicies = ratePlan.policies
                                    suiteRate.policiesCode = responsePolicies[0].code ?: ""
                                    suiteRate.policiesDescription =
                                        responsePolicies[0].description ?: ""
                                    suiteRate.amount = responsePolicies[0].amount ?: 0.0
                                    suiteRate.policiesPenaltyDays =
                                        responsePolicies[0].penaltyDays ?: 0
                                    suiteRate.policiesDateToApply =
                                        responsePolicies[0].dateToApply ?: ""
                                }
                                suiteRatePlans.add(suiteRate)
                            }
                        }

                    }
                }
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
        return suiteRatePlans
    }
}