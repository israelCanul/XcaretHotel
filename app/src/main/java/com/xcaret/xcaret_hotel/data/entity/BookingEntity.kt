package com.xcaret.xcaret_hotel.data.entity

data class BookingEntity(
    val salesId: Int? = null,
    val dsSalesId: String? = null,
    val dsSaleIdInsure: String? = null,
    val saleIdInsure: Int? = null,
    val services: BookingServiceEntity? = null,
    val payments: BookingCardPayment? = null
)

data class BookingServiceEntity(
    val hotels: List<BookingHotelEntity>? = null,
    //val activities: List<BookingActivityEntity>? = null
)

data class BookingHotelEntity(
    val roomId: String? = null,
    val rateKey: String? = null,
    val checkIn: String? = null,
    val checkOut: String? = null,
    val amount: Double? = null,
    val hotelCode: Int? = null,
    val status: Int? = null,
    val confirmationCode: String? = null
)

data class BookingActivityEntity(
    val id: Int? = null
)

data class BookingCardPayment(
    val cards: List<BookingCardEntity>? = null
)

data class BookingCardEntity(
    val transactionId: Int? = null,
    val status: Int? = null,
    val authorizationCode: String? = null,
    //val mechantOrderID: Any? = null,
    //val gateway: BookingCardGatewayEntity? = null,
    //val paymentMethod: BookingCardPayMethodEntity? = null,
    //val bankCharge: BookingCardBankChargeEntity? = null,
    //val bankReceptor: BookingCardBankReceptorEntity? = null,
    //val card: BookingCardDetailEntity? = null,
    val amount: Double? = null,
    val msi: Int? = null,
    val paymentId: Int? = null,
    val error: BookingCardError? = null,
    val comments: String? = null
)

data class BookingCardError(
    val id: Int? = null,
    val message: String? = null
)

data class BookingCardGatewayEntity(
    val id: Int? = null,
    val name: String? = null,
    val message: String? = null,
    val reference: String? = null,
    val cidGateway: String? = null,
    val gateWayFee: Int? = null,
    val gateWayFeePercent: Int? = null,
    val gateWayExtraFee: Int? = null
)

data class BookingCardPayMethodEntity(
    val id: Int? = null,
    val name: String? = null
)

data class BookingCardBankChargeEntity(
    val id: Int? = null,
    val name: String? = null
)

data class BookingCardBankReceptorEntity(
    val id: Int? = null,
    val name: String? = null
)

data class BookingCardDetailEntity(
    val cardType: BookingCardTypeEntity? = null,
    val number: String? = null,
    val maskNumber: String? = null,
    val holderName: String? = null,
    val clientCardId: Int? = null,
    val cvv: Int? = null,
    val expiration: String? = null
)

data class BookingCardTypeEntity(
    val id: Int? = null,
    val name: String? = null
)