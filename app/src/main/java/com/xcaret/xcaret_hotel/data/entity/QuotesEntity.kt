package com.xcaret.xcaret_hotel.data.entity

data class QuotesResponseEntity(
    val filters: QuotesFilterEntity? = null,
    val hotels: List<QuotesHotelEntity>? = null
)

data class QuotesFilterEntity(
    //val promotion: Any? = null,
    val mealsPlans: List<QuotesMealPlansEntity>? = null
)

data class QuotesMealPlansEntity(
    val id: String? = null,
    val name: String? = null
)

data class QuotesHotelEntity(
    val hotelCode: Int? = null,
    val name: String? = "",
    val chainCode: String? = null,
    val rateInfo: QuoteHotelRateInfoEntity? = null,
    val currency: String? = null,
    val totalRooms: Int? = null,
    val rooms: List<QuotesRoomEntity>? = null
)

data class QuoteHotelRateInfoEntity(
    val id: Int? = null,
    val description: String? = null
)

data class QuotesRoomEntity(
    val roomCode: String? = null,
    val name: String? = null,
    val shortDescription: String? = null,
    val description: String? = null,
    val ratePlanes: List<QuotesRoomRatePlansEntity>? = null,
    val image: String? = null,
    val roomImages: List<String>? = null,
    val quantity: Int? = null
)

data class QuotesRoomRatePlansEntity(
    val ratePlanCode: String? = null,
    val name: String? = null,
    val description: String? = null,
    val baseCurrency: String? = null,
    //val promotion: String? = null,
    val guarantee: QuotesRoomRatePlanGuaranteeEntity? = null,
    val mealsIncluded: Boolean? = null,
    val amount: Double? = null,
    val normalAmount: Double? = null,
    val averageAmount: Double? = null,
    val nights: List<QuotesRoomRatePlansNightsEntity>? = null,
    val policies: List<QuotesRoomRatePlansPolicyEntity>? = null
)

data class QuotesRoomRatePlanGuaranteeEntity(
    val code: String? = null,
    val description: String? = null,
    val chargeRequired: Boolean? = null,
    val chargePercent: Double? = null
)

data class QuotesRoomRatePlansNightsEntity(
    val date: String? = null,
    val amount: Double? = null,
    val normalAmount: Double? = null
)

data class QuotesRoomRatePlansPolicyEntity(
    val code: String? = null,
    val description: String? = null,
    val amount: Double? = null,
    val penaltyDays: Int? = null,
    val dateToApply: String? = null
)