package com.xcaret.xcaret_hotel.view.booking.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.BookingUseCase
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.domain.*
import kotlinx.coroutines.withContext

class PickupViewModel: ViewModel() {

    private val labelUseCase = LangLabelUseCase()
    private val bookingUseCase by lazy { BookingUseCase()}

    val arrivalActive = MutableLiveData(true)
    val arrivalInfo = MutableLiveData(PaymentPickup())
    val departureInfo = MutableLiveData(PaymentPickup())

    val currentInfo = MutableLiveData(PaymentPickup())
    val currentAirline = MutableLiveData<Airline?>()
    val currentAirlineTerminal = MutableLiveData<AirlineTerminal?>()
    val skipFormShuttle = MutableLiveData(false)
    val lblTerminal = MutableLiveData("")
    var previousText = MutableLiveData("")

    fun getTerminalLabel() = labelUseCase.findLabel(HotelXcaretApp.mContext.getString(R.string.rkey_lbl_flight_terminal))

    fun formIsValid(formData: PaymentPickup): Boolean {
        var result = true
        if(formData.airline == null)
            result = false
        if(formData.flightNumber == null)
            result = false
        if(formData.terminal == null)
            result = false
        if(formData.hour.isNullOrEmpty())
            result = false
        return result
    }

    fun getError(payment: PaymentPickup?): PaymentPickupError{
        val error = PaymentPickupError()
        if(payment?.airline == null){
            error.hasError = true
            error.airline = R.string.field_required
        }

        if(payment?.flightNumber.toString().isEmpty()){
            error.hasError = true
            error.flightNumber = R.string.field_required
        }

        if(payment?.hour.toString().isEmpty()){
            error.hasError = true
            error.hour = R.string.field_required
        }

        if(payment?.terminal.toString().isEmpty()){
            error.hasError = true
            error.terminal = R.string.field_required
        }
        return error
    }

    fun isCompleteInfo(): Boolean {
        return when {
            arrivalInfo.value == null -> false
            departureInfo.value == null -> false
            else -> formIsValid(arrivalInfo.value!!) && formIsValid(departureInfo.value!!)
        }
    }
    fun findNotTransportationCaption(): LiveData<LangLabel?> = labelUseCase.findLabel(
        HotelXcaretApp.mContext.getString(
            R.string.rkey_lbl_not_require_transportation_caption))

    suspend fun clearPreviousAAttemps(){
        bookingUseCase.deleteAttempts()
    }
}