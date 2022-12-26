package com.xcaret.xcaret_hotel.domain

import android.os.Build
import android.text.InputType
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InlineSuggestionsRequest
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.*
import com.xcaret.xcaret_hotel.BuildConfig
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.entity.PaymentBankEntity
import com.xcaret.xcaret_hotel.data.entity.PaymentBankInfoEntity
import com.xcaret.xcaret_hotel.data.entity.PaymentBanksEntity
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.Language
import org.json.JSONArray

data class Booking(
    var suiteQuotes: MutableList<SuiteQuotes> = mutableListOf(),
    var buyerData: BuyerData? = null,
    var arrival: PaymentPickup? = null,
    var departure: PaymentPickup? = null,
    var paymentCard: PaymentCard? = null,
    var reservation: ReservationBook? = null,
    var bookingAttempt:BookingAttemptInfo? = null
){
    fun toRequest(): JsonObject{
        val request = JsonObject()

        val header = JsonObject()
        header.addProperty("channel", Settings.getParam(Constants.CHANNEL_ID, HotelXcaretApp.mContext).toIntOrNull() ?: 0)
        header.addProperty("clientId", Settings.getParam(Constants.CLIENT_ID, HotelXcaretApp.mContext).toIntOrNull() ?: 0)
        header.addProperty("currency", Language.getCurrency(HotelXcaretApp.mContext))
        header.addProperty("language", Language.getLangCode(HotelXcaretApp.mContext))
        header.addProperty("country", Session.getIsoPaymentCode(HotelXcaretApp.mContext) ?: "")
        header.addProperty("browserCookie", "")
        header.addProperty("ip", Utils.ipAddressLocal)
        //header.addProperty("ip", "127.0.0.1")
        header.addProperty("deviceTransactionId", Session.getUID(HotelXcaretApp.mContext))
        header.addProperty("currencyId", Language.getCurrencyId(HotelXcaretApp.mContext))
        header.addProperty("mobile", Settings.getParam(Constants.mobile, HotelXcaretApp.mContext) == "1")
        header.addProperty("browser", "Dispositivo")
        header.addProperty("os", "Android")
        //New Fields appVersion, osVersion y deviceModel
        header.addProperty("appVersion", BuildConfig.VERSION_NAME?:"")
        header.addProperty("osVersion", Build.VERSION.RELEASE?:"")
        header.addProperty("deviceModel", Utils.getDeviceName()?:"")
        header.addProperty("app", "HOTEL")

        val services = JsonObject()
        val hotels = JsonArray()

        var total = 0.0
        suiteQuotes.forEach { sq ->
            val hotel = JsonObject()
            hotel.addProperty("promotionCode", sq.promoCode)
            hotel.addProperty("roomCode", sq.suiteCodeSelected.toUpperCase())
            hotel.addProperty("ratePlanCode", sq.ratePlanCode)
            hotel.addProperty("checkIn", sq.startDate)
            hotel.addProperty("checkOut", sq.endDate)

            val guest = JsonObject()
            guest.addProperty("adults", sq.adults)
            guest.addProperty("childs", sq.children)

            val guestArray = JsonArray()

            val ages = JsonArray()
            if(sq.children > 0) {
                if (sq.ageToList().isNotEmpty()) {
                    sq.ageToList().forEach { age ->
                        ages.add(age)
                    }
                }
            }
            guest.add("childsAges", ages)
            //guestArray.add(guest)
            hotel.add("Guest", guest)
            //hotel.add("Guest",guestArray)
            hotel.addProperty("comments", buyerData?.specialRequest ?: "")

            hotel.add("pickupFlightInfo", recoverPickUpInfo())
            hotel.addProperty("amount", sq.ratePlan?.amount ?: 0.0)
            hotel.addProperty("hotelCode", sq.hodelCode)

            hotel.add("traveler", getTravelerRequest(buyerData))
            hotels.add(hotel)
            total += sq.ratePlan?.amount ?: 0.0
        }
        services.add("hotels", hotels)

        val cards = JsonArray()
        val card = JsonObject()

        card.addProperty("cardNumber", paymentCard?.cardNumber?.encrypt())
        card.addProperty("cvv", paymentCard?.cvv?.encrypt())
        paymentCard?.expireDate?.let { ed ->
            val split = ed.split("/")
            val expireDate = JsonObject()
            expireDate.addProperty("month", split[0].encrypt())
            expireDate.addProperty("year", split[1].encrypt())
            card.add("expireDate", expireDate)
        }
        val buyer = getTravelerRequest(buyerData)
        paymentCard?.holderName?.let { name ->
            val split = name.split(" ")
            var firstName = ""
            var lastName = ""
            var secondsLastName = ""
            when {
                split.size == 1 -> {
                    firstName = name
                }
                split.size == 2 -> {
                    firstName = split[0]
                    lastName = split[1]
                }
                split.size == 3 -> {
                    firstName = split[0]
                    lastName = split[1]
                    secondsLastName = split[2]
                }
                split.size > 3 -> {
                    split.forEachIndexed { index, s ->
                        if(index < (split.size-2)) firstName += "$s "
                    }
                    lastName = split[split.size-2]
                    secondsLastName = split[split.size-1]
                }
            }
            if(firstName.trim().isEmpty()) firstName = buyerData?.firstName ?: ""
            if(lastName.trim().isEmpty()) lastName = buyerData?.lastName ?: ""
            buyer.addProperty("firstName", firstName)
            buyer.addProperty("lastName", lastName)
            buyer.addProperty("secondLastName", secondsLastName)
            buyer.addProperty("fullName", name)
        }

        card.add("buyer", buyer)
        card.addProperty("amount", total)
        card.addProperty("msi", paymentCard?.msi ?: 0)
        card.addProperty("transactionId", "")
        card.addProperty("status", 0)
        card.addProperty("paymentId", 0)

        paymentCard?.objBank?.let { bank ->
            val bankCharge = JsonObject()
            bankCharge.addProperty("id", bank.IdBank?.toIntOrNull() ?: 0)
            bankCharge.addProperty("name", bank.CardTypeName ?: "")
            card.add("bankCharge", bankCharge)
        }

        cards.add(card)

        val paymentCard = JsonObject()
        paymentCard.add("cards", cards)

        val device = JsonObject()
        device.addProperty("Name","Mobile")
        device.addProperty("Id",1)

        val additionalInformation = JsonObject()
        val payments = JsonArray()
        val paymentElement = JsonObject()
        paymentElement.addProperty("BankId",5)
        paymentElement.addProperty("PaymentMethodId",29)
        payments.add(paymentElement)
        additionalInformation.add("Payments",payments)


        request.addProperty("promotionCode", "")
        request.add("header", header)
        request.add("services", services)
        request.add("payments", paymentCard)
        request.add("traveler", getTravelerRequest(buyerData))
        request.addProperty("total", total)
        request.addProperty("discount", 0)
        request.add("Device",device)
        //TODO añadir endpoint para re-intento de pago
        //request.add("reservation","")
        //request.add("AdditionalInformation",additionalInformation)

//        reservation?.let { res ->
//            val resReq = JsonObject()
//            resReq.addProperty("dsSaleId", res.dsSaleId ?: "")
//            resReq.addProperty("salesId", res.salesId)
//            request.add("reservation", resReq)
//        }

        val gson = GsonBuilder().setPrettyPrinting().create()
        val printJson = gson.toJson(request)
        LogHX.i("booking req", printJson)
        return request
    }

    fun checkRequestAndAddAttemptIfNecessary(preRequest:JsonObject, bookingAttemptInfo: BookingAttemptInfo?):JsonObject{
        if (Constants.IS_ATTEMPT_ENABLED) {
            bookingAttemptInfo.let {
                val bodyAttempt = JsonObject()
                bodyAttempt.addProperty("dsSalesId", bookingAttemptInfo?.dsSalesId)
                if(!bookingAttemptInfo?.dsSaleIdInsure.isNullOrEmpty()){
                    bodyAttempt.addProperty("dsSaleIdInsure", bookingAttemptInfo?.dsSaleIdInsure)
                }
                if (bookingAttemptInfo?.saleIdInsure != 0){
                    bodyAttempt.addProperty("saleIdInsure", bookingAttemptInfo?.saleIdInsure)
                }
                bodyAttempt.addProperty("salesId", bookingAttemptInfo?.salesId)
                if (bodyAttempt.size() > 0) {
                    preRequest.add("reservation", bodyAttempt)
                }
            }
//            preRequest.addProperty("dsSalesId", bookingAttemptInfo?.dsSalesId)
//            preRequest.addProperty("salesId", bookingAttemptInfo?.salesId)
        }

        val gson = GsonBuilder().setPrettyPrinting().create()
        val printJson = gson.toJson(preRequest)
        Log.i("booking req with attempt", printJson)
        return preRequest
    }

    fun recoverPickUpInfo():JsonObject{
        val pickupJson = JsonObject()
        var pickUPArrival : PaymentPickup? = null
        var pickUPDeparture : PaymentPickup? = null
        try {
            val sArrival = Session.getPickUpArrival(HotelXcaretApp.mContext)
            val sDeparture = Session.getPickUpDeparture(HotelXcaretApp.mContext)

            if (sArrival.isNotEmpty()){
                pickUPArrival  = Gson().fromJson(sArrival, PaymentPickup::class.java)
            }
            if (sDeparture.isNotEmpty()){
                pickUPDeparture  = Gson().fromJson(sDeparture, PaymentPickup::class.java)
            }

            pickupJson.addProperty("checkInAirLine", pickUPArrival?.airline?.name ?: "")
            pickupJson.addProperty("checkInArrivalTime", pickUPArrival?.hour ?: "")
            pickupJson.addProperty("checkInFlightNumber", pickUPArrival?.flightNumber?.toString() ?: "")
            pickupJson.addProperty("checkInTerminal", pickUPArrival?.terminal?.number.toString() ?: "")
            pickupJson.addProperty("checkOutAirLine", pickUPDeparture?.airline?.name ?: "")
            pickupJson.addProperty("checkOutArrivalTime", pickUPDeparture?.hour ?: "")
            pickupJson.addProperty("checkOutFlightNumber", pickUPDeparture?.flightNumber?.toString() ?: "")
            pickupJson.addProperty("checkOutTerminal", pickUPDeparture?.terminal?.number.toString() ?: "")
        }catch (Exce:Exception){

        }
        return pickupJson
    }


    private fun getTravelerRequest(buyerData: BuyerData?): JsonObject{
        val traveler = JsonObject()
        traveler.addProperty("namePrefix", buyerData?.title ?: "")
        traveler.addProperty("firstName", buyerData?.firstName ?: "")
        traveler.addProperty("lastName", buyerData?.lastName ?: "")
        var secondLastName = ""
        buyerData?.lastName?.let {
            val split = it.split(" ")
            if(split.size > 2) split.forEachIndexed { index, s ->
                if(index > 0) secondLastName += "$s "
            }
        }
        traveler.addProperty("secondLastName", secondLastName.trim())
        traveler.addProperty("fullName", "${buyerData?.firstName ?: ""} ${buyerData?.lastName ?: ""}")
        traveler.addProperty("email", buyerData?.email ?: "")
        //traveler.addProperty("email", "test-accept@xcaret.com")

        val address = JsonObject()
        address.addProperty("street", buyerData?.address ?: "")
        address.addProperty("city", buyerData?.city ?: "")
        address.addProperty("countryId", buyerData?.objCountry?.id ?: 0)
        address.addProperty("stateId", buyerData?.objState?.id ?: 0)
        address.addProperty("postalCode", (buyerData?.cp ?: "0").toInt())
        traveler.add("address", address)

        val telephone = JsonObject()
        telephone.addProperty("telephoneClass", 1)
        telephone.addProperty("number", buyerData?.phone ?: "")
        traveler.add("telephone", telephone)

        traveler.addProperty("clientType", 0)

        return traveler
    }
}

data class BuyerData(
    val title: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val address: String = "",
    val country: String = "",
    val state: String = "",
    val city: String = "",
    val cp: String = "",
    val phone: String = "",
    val specialRequest: String = "",
    val iamAdult: Boolean = true,
    val objCountry: Country? = null,
    val objState: State? = null
)

data class PaymentCard(
    var cardNumber: String = "",
    var cvv: String = "",
    var expireDate: String = "",
    var holderName: String = "",
    var msi: Int = 0,
    var transactionId: String = "",
    var objBank: PaymentBankInfoEntity? = null
){
    fun toJson(){
        try {
            val jsonString = Gson().toJson(this)
            Session.setPaymentInfo(jsonString,HotelXcaretApp.mContext)
        }catch (exc:Exception){

        }
    }
    fun clearInfoSaved(){
        try {
            Session.setPaymentInfo("",HotelXcaretApp.mContext)
        }catch (exc:Exception){

        }
    }
}

data class ReservationBook(
    val dsSaleId: String? = null,
    val salesId: Int = 0
)

data class BookingCreditCardInput(
    val inputType: Int = InputType.TYPE_CLASS_TEXT,
    val keyHint: String? = null,
    val defaultHint: String = "",
    val type: BookingCreditCardInputType = BookingCreditCardInputType.FULL_NAME,
    val maxLenght: Int = 0,
    var isValid: Boolean = false,
    var message: String? = null,
    var value: String = "",
    var actionId: Int = EditorInfo.IME_ACTION_DONE
): ListItemViewModel()

enum class BookingCreditCardInputType(val value: Int){
    FULL_NAME(0),
    CARD_NUMBER(1),
    EXPIRY_DATE(2),
    CVV(3)
}

enum class BookingCardStatus(val value: Int){
    NONE(0),
    DECLINED(1),
    ACCEPTED(2),
    REJECTED(3),
    IN_PROGRESS(5),
    PAYMENT_PLAN(6);

    companion object {
        fun from(status: Int): BookingCardStatus {
            return when (status) {
                DECLINED.value -> DECLINED
                ACCEPTED.value -> ACCEPTED
                REJECTED.value -> REJECTED
                IN_PROGRESS.value -> IN_PROGRESS
                PAYMENT_PLAN.value -> PAYMENT_PLAN
                else -> NONE
            }
        }
    }
}

enum class BookingHotelStatus(val value: Int){
    CONFIRMED(1),
    CANCELLED(2),
    IN_PROGRESS(3),
    NO_CONFIRMED(4);

    companion object {
        fun from(status: Int): BookingHotelStatus {
            return when (status) {
                CONFIRMED.value -> CONFIRMED
                CANCELLED.value -> CANCELLED
                IN_PROGRESS.value -> IN_PROGRESS
                else -> NO_CONFIRMED
            }
        }
    }
}

@Entity
data class BookingAttemptInfo(
    @PrimaryKey(autoGenerate = true)
    var primary_key: Int = 0,
    var dsSalesId: String? = null,
    var dsSaleIdInsure: String? = null,
    var saleIdInsure: Int = 0,
    var salesId: Int? = null
): ListItemViewModel(){
    override fun toString(): String {
        return "dsSalesId: ${dsSalesId},dsSaleIdInsure: ${dsSaleIdInsure},salesId: ${salesId},saleIdInsure: $saleIdInsure"
    }
}