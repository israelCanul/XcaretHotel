package com.xcaret.xcaret_hotel.data.entity

import com.google.firebase.database.PropertyName
import com.google.gson.Gson
import com.xcaret.xcaret_hotel.HotelXcaretApp

data class ReservationGenericResponse<T>(
    val success: Boolean = false,
    val errorCode: Int = 0,
    val data: T? = null
)

data class ReservationsResponse(
    val Email: String? = null,
    val Reservations: List<ReservationItemResponse>? = null
)

data class ReservationItemResponse(
    val Id: Int? = null,
    val ReservationNumber: String? = null,
    val SaleDate: String? = null,
    val Status: String? = null,
    var Language: String = com.xcaret.xcaret_hotel.view.config.Language.getLangCode(HotelXcaretApp.mContext),
    var Currency: String? = null,
    var Email: String? = null,
    var Details: Boolean = true,
    var Balance: Boolean = true,
    var Amount: Double? =null ,
    var Services: List<ReservationItemService>? = null
)

data class ReservationItemService(
    val Id: Int? = null,
    val Type: String? = null,
    val Total: Int? = null,
    val Description: String? = null
)

data class ReservationDetailResponse(
    @get:PropertyName("reservationNumber") @set:PropertyName("reservationNumber")
    var ReservationNumber: String? = null,

    @get:PropertyName("saleDate") @set:PropertyName("saleDate")
    var SaleDate: String? = null,

    @get:PropertyName("saleId") @set:PropertyName("saleId")
    var SaleId: Int? = null,

    @get:PropertyName("status") @set:PropertyName("status")
    var Status: ReservationStatusResponse? = null,

    @get:PropertyName("amount") @set:PropertyName("amount")
    var Amount: Float? = null,

    @get:PropertyName("discount")
    val Discount: Double? = null,

    @get:PropertyName("channel")
    val Channel: ReservationChannelResponse? = null,

    @get:PropertyName("notes")
    val Notes: Any? = null,

    @get:PropertyName("affiliate")
    val Affiliate: ReservationAffiliateResponse? = null,

    @get:PropertyName("traveler") @set:PropertyName("traveler")
    var Traveler: ReservationTravelerResponse? = null,

    @get:PropertyName("services") @set:PropertyName("services")
    var Services: ReservationServiceResponse? = null,

    @get:PropertyName("payments")
    val Payments: CardPaymentResponseItem? = null
){

    fun isHotelReservation(): Boolean{
        var isHotelReservation = false
        Services?.Hotels?.let {
            isHotelReservation = it.isNotEmpty()
        }
        return isHotelReservation
    }
}

data class ReservationStatusResponse(
    @get:PropertyName("reservation") @set:PropertyName("reservation")
    var Reservation: String? = null,

    @get:PropertyName("payment") @set:PropertyName("payment")
    var Payment: String? = null
)

data class ReservationChannelResponse(
    @get:PropertyName("id")
    val Id: Int? = null,

    @get:PropertyName("name")
    val Name: String? = null
)

data class ReservationAffiliateResponse(
    @get:PropertyName("id")
    val Id: Int? = null,

    @get:PropertyName("fiscalCode")
    val FiscalCode: String? = null,

    @get:PropertyName("comercialReason")
    val ComercialReason: String? = null,

    @get:PropertyName("businessReason")
    val BusinessReason: String? = null,

    @get:PropertyName("contac")
    val Contac: ReservationContactResponse? = null
)

data class ReservationContactResponse(
    @get:PropertyName("email")
    val Email: String? = null,

    @get:PropertyName("telephone")
    val Telephone: String? = null,

    @get:PropertyName("country")
    val Country: ContactCountryResponse? = null,
)

data class ContactCountryResponse(
    val State: ContactStateResponse? = null,
    val Id: Int? = null,
    val Name: String? = null,
    val Iso: String? = null
)

data class ContactStateResponse(
    val Id: Int? = null,
    val Name: String? = null,
    val ShortName: String? = null
)

data class ReservationTravelerResponse(
    val Id: Int? = null,
    val Name: String? = null,
    val LastName: String? = null,
    val SecondLastName: String? = null,

    @get:PropertyName("fullName") @set:PropertyName("fullName")
    var FullName: String? = null,
    val Contac: ContactCountryResponse? = null
)

data class ReservationServiceResponse(
    @get:PropertyName("hotels") @set:PropertyName("hotels")
    var Hotels: List<ServiceHotelResponse>? = null,
    val Activities: List<ServiceActivityResponse>? = null
)

data class ServiceHotelResponse(
    @get:PropertyName("hotelCode") @set:PropertyName("hotelCode")
    var HotelCode: String? = null,

    @get:PropertyName("hotelName") @set:PropertyName("hotelName")
    var HotelName: String? = null,

    @get:PropertyName("room") @set:PropertyName("room")
    var Room: HotelRoomResponse? = null,
    val RatePlan: HotelRatePlanResponse? = null,
    @get:PropertyName("confirmation") @set:PropertyName("confirmation")
    var Confirmation: HotelConfirmationResponse? = null,

    @get:PropertyName("amount")
    val Amount: Double? = null,

    @get:PropertyName("chekIn") @set:PropertyName("chekIn")
    var ChekIn: String? = null,

    @get:PropertyName("chekOut") @set:PropertyName("chekOut")
    var ChekOut: String? = null,

    @get:PropertyName("saleDate")
    val SaleDate: String? = null
)

data class HotelRoomResponse(
    @get:PropertyName("quantity") @set:PropertyName("quantity")
    var Quantity: Int? = null,

    @get:PropertyName("pax") @set:PropertyName("pax")
    var Pax: RoomPaxResponse? = null,

    @get:PropertyName("id")
    val Id: String? = null,

    @get:PropertyName("name")@set:PropertyName("name")
    var Name: String? = null
)

data class RoomPaxResponse(
    @get:PropertyName("adults") @set:PropertyName("adults")
    var Adults: Int? = null,

    @get:PropertyName("children") @set:PropertyName("children")
    var Children: Int? = null,

    @get:PropertyName("infant") @set:PropertyName("infant")
    var Infant: Int? = null,

    @get:PropertyName("individual") @set:PropertyName("individual")
    var Individual: Int? = null
)

data class HotelRatePlanResponse(
    val Id: String? = null,
    val Name: String? = null,
    var Policies: List<HotelPoliciesResponse>? = null,

)
data class HotelPoliciesResponse(
    val Code: String? = null,
    val Description: String? = null,
    val Amount: Double? = 0.0,
    val DaysToApply: Int? = 0,
    val DateToApply: String? = null
)

data class HotelConfirmationResponse(
    @get:PropertyName("confirmationCode") @set:PropertyName("confirmationCode")
    var ConfirmationCode: String? = null
)

data class ServiceActivityResponse(
    val DetailId: Int? = null,
    val ProductKey: String? = null,
    val ProductId: Int? = null,
    val ProductCode: String? = null,
    val ProductName: String? = null,
    val Status: String? = null,
    val DetailReferenceId: Int? = null,
    val UnitBussines: ActivityUnitBussinesResponse? = null,
    val Geography: ActivityGeographyResponse? = null,
    val Location: ActivityLocationResponse? = null,
    val VisitDate: String? = null,
    val SaleDate: String? = null,
    val Guests: List<ActivityGuestResponse>? = null,
    val NormalAmount: Double? = null,
    val Amount: Double? = null,
    val AmountDiscount: Double? = null,
    val ExchageRate: Double? = null,
    val DiscountPercent: Double? = null,
    val ProviderReference: String? = null,
    val IvaPercent: Double? = null,
    val IvaAmount: Double? = null,
    val Pax: ActivityPaxResponse? = null,
    val Movement: Int? = null,
    val AllotementReserved: Int? = null,
    val Transport: Boolean? = null,
    val IsPackage: Boolean? = null,
    val RelatedId: Int? = null,
    val Rates: ActivityRateResponse? = null,
    val RateKey: String? = null,
    val SyncSaturn: Boolean? = null
)

data class ActivityUnitBussinesResponse(
    val Id: Int? = null,
    val Name: String? = null
)

data class ActivityGeographyResponse(
    val Id: Int? = null
)

data class ActivityLocationResponse(
    val Id: Int? = null,
    val Name: String? = null
)

data class ActivityGuestResponse(
    val Name: String? = null,
    val LastName: String? = null,
    val FullName: String? = null
)

data class ActivityPaxResponse(
    val Adults: Int? = null,
    val Children: Int? = null,
    val Infant: Int? = null,
    val Individual: Int? = null
)

data class ActivityRateResponse(
    val Individual: RateIndividualResponse? = null,
    val Time: String? = null
)

data class RateIndividualResponse(
    val Id: Int? = null,
    val NormalAmount: Double? = null,
    val Amount: Double? = null,
    val Discount: Double? = null,
    val PercentDiscount: Double? = null
)

data class CardPaymentResponseItem(
    val CardPayments: List<CardPaymentsResponse>? = null
)

data class CardPaymentsResponse(
    val Id: Int? = null,
    val Status: String? = null,
    val MovementId: Int? = null,
    val paymentDate: String? = null,
    val PaymentHour: String? = null,
    val Amount: Double? = null,
    val PaymentType: Int? = null,
    val Authorization: String? = null,
    val Gateway: PaymentGatewayResponse? = null,
    val BankCharge: PaymentBankChargeResponse? = null,
    val BankReceptor: PaymentBankReceptorResponse? = null,
    val Card: PaymentCardResponse? = null
)

data class PaymentGatewayResponse(
    val Id: Int? = null,
    val Name: String? = null,
    val Reference: String? = null
)

data class PaymentBankChargeResponse(
    val Id: Int? = null,
    val Name: String? = null
)

data class PaymentBankReceptorResponse(
    val Id: Int? = null,
    val Name: String? = null
)

data class PaymentCardResponse(
    val CardType: PaymentCardTypeResponse? = null,
    val MaskNumber: String? = null,
    val HolderName: String? = null
)

data class PaymentCardTypeResponse(
    val Id: Int? = null,
    val Name: String? = null
)

enum class ReservationAction(val value: Int){
    SUCCESS(0),
    ERROR_EMPTY(1),
    ERROR_ENDPONT(2),
    LOADING(3),
    ERRO_NO_OFFLINE_NO_DB_STORED(4)
}

enum class PaymentStatus(val value: String){
    declined("declined"),
    approved("approved"),
    rejected("rejected"),
    inProccess("rnProccess"),
    paymentPlan("paymentPlan")
}

enum class ReservationStatus(val value: String){
    cancel("cancel"),
    refound("refound"),
    upgrade("upgrade"),
    inProccess("inprocess"),
    modify("modify"),
    paid("paid"),
    expired("expired"),
    courtesy("courtesy"),
    reserved("reserved"),
    chargeBack("chargeBack"),
    penalty("penalty"),
    invoiced("invoiced"),
    reservedAgency("reservedAgency")
}

enum class ReservationProductType(val value: String) {
    none("None"),
    activity("Activity"),
    addon("Addon"),
    paquete("Package"),
    hotel("Hotel"),
    insurance("Insurance");

    companion object{
        fun from(strProducType: String): ReservationProductType {
            return when(strProducType){
                activity.value -> activity
                addon.value -> addon
                paquete.value -> paquete
                hotel.value -> hotel
                insurance.value -> insurance
                else -> none
            }
        }
    }
}




