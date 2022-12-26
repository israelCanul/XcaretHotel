package com.xcaret.xcaret_hotel.data.mapper

import com.xcaret.xcaret_hotel.data.entity.ReservationDetailResponse
import com.xcaret.xcaret_hotel.data.entity.ReservationServiceResponse
import com.xcaret.xcaret_hotel.data.entity.ServiceHotelResponse
import com.xcaret.xcaret_hotel.domain.Reservation

class ReservationMapper: FirebaseMapper<ReservationDetailResponse, Reservation>() {

    override fun map(from: ReservationDetailResponse?, key: String?): Reservation? {
        val detail = getDetailHotel(from?.Services?.Hotels)

        val reservation = if(detail.numPax == 0) null
        else Reservation(
            reservationNumber = from?.ReservationNumber ?: "",
            hotelCode = detail.hotelCode,
            saleDate = from?.SaleDate ?: "",
            amount = from?.Amount ?: 0.0f,
            guestName = from?.Traveler?.FullName ?: "",
            roomQuantity = detail.numRooms,
            guestQuantity = detail.numPax,
            checkIn = detail.checkIn,
            checkOut = detail.checkOut,
            status = from?.Status?.Reservation ?: "",
            statusPayment = from?.Status?.Payment ?: "",
            confirmationCode = getConfirmationCode(from?.Services),
            roomsDescription = detail.roomsDescription
        )

        from?.Services?.Hotels?.let { listHotels ->
            if(listHotels.isNotEmpty()){
                val first = listHotels[0]
                reservation?.confirmationCode = first.Confirmation?.ConfirmationCode ?: ""
            }
        }

        return reservation
    }

    fun getDetailHotel(hotels: List<ServiceHotelResponse>?): DetailHotel {
        val detail = DetailHotel()
        hotels?.let { h ->
            if(h.isNotEmpty()){
                h.forEach { mHotel ->
                    detail.roomsDescription += "|"+(mHotel.Room?.Name?:"--")
                    detail.numRooms += mHotel.Room?.Quantity ?: 0
                    mHotel.Room?.Pax?.let { pax ->
                        detail.numPax += (pax.Adults ?: 0) + (pax.Children ?: 0) +
                                (pax.Individual ?: 0) + (pax.Infant ?: 0)
                     }
                    detail.checkIn = mHotel.ChekIn ?: ""
                    detail.checkOut = mHotel.ChekOut ?: ""
                    detail.hotelCode = mHotel.HotelCode ?: ""
                }
            }
        }
        return detail
    }

    fun getConfirmationCode(resInfo:ReservationServiceResponse?):String{
        var code = "----"
        if (resInfo!=null){
            if(resInfo.Hotels?.size!=0){
                resInfo.Hotels?.forEach {
                    if(it.Confirmation!=null) {
                       if(it.Confirmation?.ConfirmationCode!!.isNotBlank()){
                           code = it.Confirmation?.ConfirmationCode!!
                       }
                    }
                }
            }
        }
        return code
    }

    data class DetailHotel(
        var numPax: Int = 0,
        var numRooms: Int = 0,
        var checkIn: String = "",
        var checkOut: String = "",
        var hotelCode: String = "",
        var roomsDescription:String = ""
    )

}