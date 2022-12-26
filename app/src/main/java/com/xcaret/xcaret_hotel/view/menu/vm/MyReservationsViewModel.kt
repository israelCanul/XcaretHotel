package com.xcaret.xcaret_hotel.view.menu.vm

import android.util.Log
import androidx.lifecycle.*
import com.xcaret.xcaret_hotel.data.entity.*
import com.xcaret.xcaret_hotel.data.mapper.ReservationMapper
import com.xcaret.xcaret_hotel.data.usecase.HotelUseCase
import com.xcaret.xcaret_hotel.data.usecase.ReservationsUseCase
import com.xcaret.xcaret_hotel.data.usecase.UserUseCase
import com.xcaret.xcaret_hotel.domain.Hotel
import com.xcaret.xcaret_hotel.domain.Reservation
import com.xcaret.xcaret_hotel.domain.User
import com.xcaret.xcaret_hotel.view.config.Constants
import kotlinx.coroutines.*

class MyReservationsViewModel : ViewModel() {


    private val userUseCase = UserUseCase()
    var reservationUseCase = ReservationsUseCase()
    val hotelUserUseCase =HotelUseCase()

    val userLiveData = MutableLiveData<User>()
    val uidLiveData = MutableLiveData<String>()
    val statusView = MutableLiveData(ReservationAction.LOADING)
    var limitReached = MutableLiveData(false)
    var Reservation = MutableLiveData<List<Reservation>?>(null)
    val numberOfPages = MutableLiveData(0)
    var from = MutableLiveData(0)
    var to = MutableLiveData(20)
    var lastItem = MutableLiveData(0)
    var FirstItem = MutableLiveData(20)
    var shouldPaginate = MutableLiveData(false)
    var isLoadingPages = MutableLiveData(false)
    var lastItemInList = MutableLiveData(false)
    var showFaqs = MutableLiveData(false)
    var forceClose = MutableLiveData(false)
    var listReservationResponse = MutableLiveData<List<ReservationItemResponse>>()
    var listReservationDetail = MutableLiveData<List<ReservationDetailResponse>>()
    var listReservations = MutableLiveData<List<Reservation?>>()
    var listHotels = MutableLiveData<List<Hotel>>()
    var sizeCaptured = MutableLiveData(0)
    var searchMore = MutableLiveData(0)
    var doesDataFinished = MutableLiveData(false)
    var lastItemRegister = MutableLiveData("")

    fun getHotels() = hotelUserUseCase.allNoFilter()
    fun getSession() = userUseCase.getSession(uidLiveData.value ?: "")
    fun fetchReservationByEmail(
        email: String,
        result: (response: ReservationGenericResponse<List<ReservationDetailResponse>>) -> Unit
    ) =
        reservationUseCase.fetchReservationByEmail(email, result)


    fun getReservationsHotel(email: String) = reservationUseCase.getReservationsNumber(email) {
        if (it.success) {
            var result = mutableListOf<ReservationItemResponse>()
            it.data?.Reservations?.forEach { itemReservation ->
                itemReservation.Services?.let { itemService ->
                    if (itemService.isNotEmpty()) {
                        if (ReservationProductType.from(
                                itemService[0].Type ?: ""
                            ) == ReservationProductType.hotel
                        ) {
                            result.add(itemReservation)

                        }
                    }
                }
            }
            if(result.isNotEmpty()) {
                result = result.asReversed()
                listReservationResponse.postValue(result)
                reservationUseCase.recursiveGetReservationDetail(
                    0, result.take(8),
                    mutableListOf<ReservationDetailResponse>()
                ) { generic ->
                    if (generic.success) {
                        listReservationDetail.postValue(generic.data!!)
                    } else {
                        statusView.value = ReservationAction.ERROR_ENDPONT
                    }

                }
            }else{
                statusView.postValue( ReservationAction.ERROR_EMPTY)
            }


        }else{
            statusView.value = ReservationAction.ERROR_ENDPONT
        }
    }

    fun updateReservationsInFirebase(
        toSave: List<ReservationDetailResponse>,
        response: (success: Boolean) -> Unit
    ) =
        reservationUseCase.updateReservationsInFirebase(toSave, response)

    fun updateReservationAsync(toSave: List<ReservationDetailResponse>){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                reservationUseCase.updateReservationsInFirebaseCorutine(toSave)
            }
        }
    }

    fun allReservations() = reservationUseCase.allReservationsTop()

    fun getReservationsInRange() {
        viewModelScope.launch {
            if (Reservation.value == null) {
                val reservations = withContext(Dispatchers.IO) {
                    reservationUseCase.allReservationsTopNoLive()
                }
                Reservation.value = reservations

            } else {

                if (limitReached.value == false) {
                    val reservations = withContext(Dispatchers.IO) {
                        reservationUseCase.getReservationsInRange(from.value, to.value)
                    }
                    val oldReservations = Reservation.value!!.toMutableList()
                    if (reservations != null) {

                        oldReservations.addAll(reservations)
                        Reservation.value = oldReservations

                        limitReached.value = oldReservations.last().idReservationNumber == 1
                        Log.e("TAG", "Values ${oldReservations.last().idReservationNumber}")
                    }
                }
            }
        }
    }

    fun getTotalRegisters() {
        viewModelScope.launch {
            val totalRegister =
                withContext(Dispatchers.IO) { reservationUseCase.allTotalReservations() }
            numberOfPages.value = (totalRegister / Constants.QUERY_SIZE_PAGE).dec()
        }
    }

    fun mapDetailResponseToReservation(item:ReservationDetailResponse):Reservation?{
        val detail = ReservationMapper().getDetailHotel(item.Services?.Hotels)
        var policy = item.Services?.Hotels?.get(0)?.RatePlan?.Policies?.get(0)?.Description
        if (policy.isNullOrEmpty()) policy ="N/A"
        val reservation = if(detail.numPax == 0) {null}

        else Reservation(
            reservationNumber = item.ReservationNumber ?: "",
            hotelCode = detail.hotelCode,
            saleDate = item.SaleDate ?: "",
            amount = item.Amount ?: 0.0f,
            guestName = item.Traveler?.FullName ?: "",
            roomQuantity = detail.numRooms,
            guestQuantity = detail.numPax,
            checkIn = detail.checkIn,
            checkOut = detail.checkOut,
            status = item.Status?.Reservation ?: "",
            statusPayment = item.Status?.Payment ?: "",
            confirmationCode = ReservationMapper().getConfirmationCode(item.Services),
            roomsDescription = detail.roomsDescription,
            hotel = listHotels.value?.find {it.idSynxis == item.Services?.Hotels?.get(0)?.HotelCode},
            policies = policy ?: ""

        )

        item.Services?.Hotels?.let { listHotels ->
            if(listHotels.isNotEmpty()){
                val first = listHotels[0]
                reservation?.confirmationCode = first.Confirmation?.ConfirmationCode ?: ""
            }
        }

        return reservation
    }

    fun getReservations(items:List<ReservationDetailResponse>){
        updateReservationAsync(toSave = items)
        //val oldReservations = this.listReservations.value
        val listReservationsR = mutableListOf<Reservation?>()
//        if (this.listReservations.value != null)
//            if (this.listReservations.value!!.isNotEmpty())
//                listReservations.addAll(this.listReservations.value!!)
        items.forEach {
            listReservationsR.add(mapDetailResponseToReservation(it))
        }
        this.listReservations.postValue(listReservationsR)
    }


}