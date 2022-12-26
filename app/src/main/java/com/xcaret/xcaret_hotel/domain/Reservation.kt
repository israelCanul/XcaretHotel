package com.xcaret.xcaret_hotel.domain

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.room.*
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.entity.PaymentStatus
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.entity.ReservationStatus

@Entity
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    var idReservationNumber: Int = 0,
    var reservationNumber: String = "",
    var hotelCode: String = "",
    var saleDate: String = "",
    var guestName: String = "",
    var amount: Float = 0.0f,
    var guestQuantity: Int = 0,
    var roomQuantity: Int = 0,
    var pickUp: String = "",
    var checkIn: String = "",
    var checkOut: String = "",
    var status: String = "",
    var statusPayment: String = "",
    var confirmationCode: String = "--",
    var roomsDescription:String ="",
    @Ignore var expand: Boolean = false,
    @Ignore var policies: String = " ",
    @Embedded var hotel: Hotel? = null
): ListItemViewModel() {

    fun formatDate(date: String) =
        DateUtil.changeFormatDate(date, DateUtil.ORIGIN_FORMAT_RESERVATION, DateUtil.FORMAT_RESERVATION)

    fun formatAmount(): String {
        val currency = Language.getCurrency(HotelXcaretApp.mContext)
        return "${"".getSymbolCurrency() }${amount.formatCurrency()} $currency"
    }
    fun formatRooms():String{
        val suites = roomsDescription.split("|").filter { !it.isBlank() }
        var formatedStr = ""
        suites.forEachIndexed{index,name ->
            formatedStr += "$name"
            if (index != suites.size-1)formatedStr+= "\n"
        }
        return formatedStr
    }
    fun createRooms():String{
        val suites = roomsDescription.split("|").filter { !it.isBlank() }
        var formatedStr = ""
        suites.forEachIndexed{index,name ->
            formatedStr += "Suite ${index+1}:"
            if (index != suites.size-1)formatedStr+= "\n"
        }
        return formatedStr
    }

    fun thereAreRooms():Boolean{
        val suites = roomsDescription.split("|").filter { !it.isBlank() }
        return suites.isNotEmpty()
    }



    @Ignore
    fun getColorByStatus(): Int{
        if(statusPayment.toLowerCase() in arrayOf(PaymentStatus.declined.value, PaymentStatus.rejected.value))
            return R.color.colorTextReservationsStatus2
        else {
            return when(status.toLowerCase()){
                ReservationStatus.paid.value,
                ReservationStatus.courtesy.value,
                ReservationStatus.reservedAgency.value,
                ReservationStatus.invoiced.value,
                ReservationStatus.upgrade.value -> R.color.colorTextReservationsStatus1
                ReservationStatus.refound.value,
                ReservationStatus.inProccess.value,
                ReservationStatus.modify.value,
                ReservationStatus.chargeBack.value,
                ReservationStatus.reserved.value -> R.color.colorTextReservationsStatus3
                else -> R.color.colorTextReservationsStatus2
            }
        }
    }

    @Ignore
    fun getKeyLabelByStatus(): Int{
        if(statusPayment.toLowerCase() == PaymentStatus.approved.value
            && status.toLowerCase() == ReservationStatus.inProccess.value && confirmationCode.trim().isEmpty())
            return R.string.rkey_lbl_status_not_confirmed
        else if(statusPayment.toLowerCase() in arrayOf(PaymentStatus.declined.value, PaymentStatus.rejected.value))
            return R.string.rkey_lbl_status_pay_reject
        else {
            return when(status.toLowerCase()){
                ReservationStatus.paid.value -> R.string.rkey_lbl_status_paid
                ReservationStatus.courtesy.value -> R.string.rkey_lbl_status_courtesy
                ReservationStatus.reservedAgency.value -> R.string.rkey_lbl_status_reserved_agency
                ReservationStatus.invoiced.value -> R.string.rkey_lbl_status_invoiced
                ReservationStatus.upgrade.value -> R.string.rkey_lbl_status_upgrade
                ReservationStatus.refound.value -> R.string.rkey_lbl_status_refund
                ReservationStatus.modify.value -> R.string.rkey_lbl_status_modify
                ReservationStatus.chargeBack.value -> R.string.rkey_lbl_status_charge_back
                ReservationStatus.reserved.value -> R.string.rkey_lbl_status_reserved
                ReservationStatus.cancel.value -> R.string.rkey_lbl_status_cancel
                else -> R.string.rkey_lbl_status_in_proccess
            }
        }
    }
}