package com.xcaret.xcaret_hotel.data.usecase


import com.google.gson.JsonArray
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.api.ReservationApiService
import com.xcaret.xcaret_hotel.data.api.TicketApiService
import com.xcaret.xcaret_hotel.data.entity.*
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.FirebaseReservationRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Reservation
import com.xcaret.xcaret_hotel.view.config.*
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.jetbrains.anko.doAsync
import java.util.*

class ReservationsUseCase: BaseUseCase<Reservation>() {

    private val dao = database.reservationDao()
    private val repository = FirebaseReservationRepository(Session.getUID(HotelXcaretApp.mContext) ?: "")
    private val reservationApi: ReservationApiService by lazy { ReservationApiService() }
    private val ticketApi: TicketApiService by lazy { TicketApiService() }

    override fun getDao(): BaseDao<Reservation> = dao
    override fun getRepository(): FirebaseDatabaseRepository<Reservation> = repository

    fun allReservations() = dao.allReservations()

    fun allReservationsTop() = dao.allReservationsTop20()
    fun allReservationsTopNoLive() = dao.allReservationsTop20NoLive()

    fun getReservationsInRange(from :Int? , to:Int?)= dao.getReservationsInRange(from,to)

    fun fetchReservationByEmail(email: String, result: (response: ReservationGenericResponse<List<ReservationDetailResponse>>) -> Unit){
        getReservationsNumber(email){
            if(it.success){
                val result = mutableListOf<ReservationItemResponse>()
                it.data?.Reservations?.forEach { itemReservation ->
                    itemReservation.Services?.let { itemService ->
                        if(itemService.isNotEmpty()){
                            if(ReservationProductType.from(itemService[0].Type ?: "") == ReservationProductType.hotel) {
                                result.add(itemReservation)
                            }
                        }
                    }
                }
                recursiveGetReservationDetail(0, result){ generic ->
                    result(generic)
                }
                /*erifyReservationsCodes(it.data?.Reservations ?: emptyList()){ correctList ->
                    LogHX.e("correctList", correctList.toString())
                    if(it.success){
                        recursiveGetReservationDetail(0, correctList.data ?: emptyList()){ generic ->
                            result(generic)
                        }
                    }else result(ReservationGenericResponse(correctList.success, correctList.errorCode))
                }*/
            }
            else result(ReservationGenericResponse(it.success, it.errorCode))
        }
    }

    fun updateReservationsInFirebase(toSave: List<ReservationDetailResponse>, response: (success: Boolean) -> Unit){
        doAsync {
            val map = mutableMapOf<String, Any>()
            toSave.forEach { reservation ->
                if(reservation.isHotelReservation()) {
                    reservation.SaleId?.let { sId ->
                        map[sId.toString()] = reservation
                    }
                }
            }
            getRepository().getReference().updateChildren(map)
                .addOnSuccessListener {
                    LogHX.d("updateReservationsInFirebase", "success")
                    response(true) }
                .addOnFailureListener {
                    LogHX.d("updateReservationsInFirebase", it.localizedMessage)
                    response(false)
                }
        }
    }
    fun updateReservationsInFirebaseCorutine(toSave: List<ReservationDetailResponse>){
        try {

            val map = mutableMapOf<String, Any>()
            toSave.forEach { reservation ->
                if (reservation.isHotelReservation()) {
                    reservation.SaleId?.let { sId ->
                        map[sId.toString()] = reservation
                    }
                }
            }
            getRepository().getReference().updateChildren(map)
                .addOnSuccessListener {
                    LogHX.d("updateReservationsInFirebase", "success")
                }
                .addOnFailureListener {
                    LogHX.d("updateReservationsInFirebase", it.localizedMessage)
                }
        }catch (exc:Exception){

        }
    }

    private fun verifyReservationsCodes(list: List<ReservationItemResponse>, response: (result: ReservationGenericResponse<List<ReservationItemResponse>>) -> Unit){
        val result = mutableListOf<ReservationItemResponse>()
        if(list.isEmpty()) response(ReservationGenericResponse(success = true, 0, result))
        else {
            val request = JsonArray()
            list.forEach { item ->
                item.ReservationNumber?.let { request.add(it) }
            }
            ticketApi.ventas(request)
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.computation())
                .subscribeWith(object: DisposableSingleObserver<TicketEntity>(){
                    override fun onSuccess(value: TicketEntity) {
                        LogHX.i("getReservationsNumber", value.toString())
                        value?.Ventas?.forEach { tSale ->
                            if(tSale.productos?.isNotEmpty() == true){
                                val item = tSale.productos[0]
                                val familyProduct = item.familyProducto?.trim()?.toLowerCase() ?: ""
                                if(familyProduct.equals("hospedaje", ignoreCase = true)){
                                    list.find { it.ReservationNumber.equals(tSale.dsClaveVenta, ignoreCase = true) }?.let { aux ->
                                        result.add(aux)
                                    }
                                }
                            }else {
                                list.find { it.ReservationNumber.equals(tSale.dsClaveVenta, ignoreCase = true) }?.let { aux ->
                                    result.add(aux)
                                }
                            }
                        }
                        response(ReservationGenericResponse(success = true, 0, result))
                    }

                    override fun onError(e: Throwable) {
                        LogHX.e("verifyReservationsCodes", e?.localizedMessage ?: "error")
                        response(ReservationGenericResponse(success = false, 1))
                    }

                })
        }
    }

    fun recursiveGetReservationDetail(
        index: Int = 0,
        list: List<ReservationItemResponse>,
        out: MutableList<ReservationDetailResponse> = mutableListOf(),
        result: (response: ReservationGenericResponse<List<ReservationDetailResponse>>) -> Unit){

        if(list.isEmpty()) result(ReservationGenericResponse(true, 0))
        else {
            val item = list[index]
            LogHX.d("getDetail", item.toString())
            getReservationDetail(item){ resDetail ->
                if(resDetail.success) {
                    resDetail.data?.let { r ->
                        r.Status?.let { s ->
                            if((s.Payment?.equals(PaymentStatus.approved.value, ignoreCase = true) == true
                                        || s.Payment?.equals(PaymentStatus.inProccess.value, ignoreCase = true) == true
                                        || s.Payment?.equals(PaymentStatus.paymentPlan.value, ignoreCase = true) == true)
                            ){
                                r.Services?.Hotels?.let { listHotels ->
                                    if(listHotels.isNotEmpty()){
                                        val hotel = listHotels[0]
                                        hotel.ChekIn?.let { checkIn ->
                                            try {
                                                val historicYear = Settings.getParam(Constants.years_historical_reserves, HotelXcaretApp.mContext).toIntOrNull() ?: 0
                                                val yearRes = DateUtil.changeFormatDate(checkIn, DateUtil.ORIGIN_FORMAT_RESERVATION, DateUtil.YEAR).toIntOrNull() ?: 0
                                                val currentYear = (DateUtil.getDateByFormat(DateUtil.YEAR).toIntOrNull() ?: 0) - historicYear

                                                if(yearRes >= currentYear) out.add(r)
                                            }catch (e: Exception){}
                                        }
                                    }
                                } ?: kotlin.run { out.add(r) }
                            }else{
                                r.SaleDate?.let { sDate ->
                                    try {
                                        val sFormatDate = DateUtil.convertStringToDate(sDate, DateUtil.ORIGIN_FORMAT_RESERVATION)
                                        val aumentDays = Settings.getParam(Constants.show_declined_after, HotelXcaretApp.mContext).toIntOrNull() ?: 0
                                        val currentDate = DateUtil.convertStringToDate(DateUtil.getDateByFormat(
                                            DateUtil.DATE_FORMAT_WEATHER), DateUtil.DATE_FORMAT_WEATHER)
                                        if(aumentDays > 0) sFormatDate.add(Calendar.DATE, aumentDays)
                                        //if(sFormatDate.after(currentDate))
                                            out.add(r)
                                    }catch (e: Exception){}
                                }
                            }
                        }
                    }

                    //if(index == (list.size-1)) result(ReservationGenericResponse(true, 0, data = out))
                    //else recursiveGetReservationDetail(index+1, list, out, result)
                }
                if(index == (list.size-1)) {
                    result(ReservationGenericResponse(true, 0, data = out))
                }
                else recursiveGetReservationDetail(index+1, list, out, result)

            }
        }
    }




    fun getReservationsNumber(email: String, result: (response: ReservationGenericResponse<ReservationsResponse>) -> Unit){
        try{
            reservationApi.retrieveByEmail(email)
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.computation())
                .subscribeWith(object: DisposableSingleObserver<ReservationsResponse>(){
                    override fun onError(e: Throwable) {
                        LogHX.e("getReservationsNumber", e.localizedMessage)
                        result(ReservationGenericResponse(false, 1))
                    }

                    override fun onSuccess(t: ReservationsResponse) {
                        t.Reservations?.forEach {
                            it.Language = Language.getLangCode(HotelXcaretApp.mContext)
                            it.Currency = Language.getCurrency(HotelXcaretApp.mContext)
                            it.Email = email
                        }
                        LogHX.i("getReservationsNumber", t.toString())
                        result(ReservationGenericResponse(true, 0, t))
                    }
                })
        }
        catch (e: Exception){
            LogHX.e("getReservationsNumber", e.localizedMessage)
            result(ReservationGenericResponse(false, 1))
        }
    }

    fun getReservationDetail(request: ReservationItemResponse, result: (response: ReservationGenericResponse<ReservationDetailResponse>) -> Unit){
        try{
            reservationApi.byReferenceEmail(request)
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.computation())
                .subscribeWith(object: DisposableSingleObserver<ReservationDetailResponse>(){
                    override fun onError(e: Throwable) {
                        LogHX.e("getReservationDetail", e.localizedMessage)
                        result(ReservationGenericResponse(false, 1))
                    }

                    override fun onSuccess(t: ReservationDetailResponse) {
                        result(ReservationGenericResponse(true, 0, t))
                    }
                })
        }
        catch (e: Exception){
            LogHX.e("getReservationDetail", e.localizedMessage)
            result(ReservationGenericResponse(false, 1))
        }
    }


    suspend fun allTotalReservations():Int = dao.getCountReservations()
}