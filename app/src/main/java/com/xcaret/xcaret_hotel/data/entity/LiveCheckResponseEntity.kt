package com.xcaret.xcaret_hotel.data.entity

data class LiveCheckResponseEntity(
    val hotel: LiveCheckHotelEntity? = null
)

data class LiveCheckHotelEntity(
    val hotelCode: String? = null,
    val name: String? = null,
    val chainCode: String? = null,
    val rateInfo: LiveCheckRateInfoEntity? = null,
    val currency: String? = null,
    val totalRooms: Int? = null,
    val rooms: List<LiveCheckRoomsEntity>? = null
)

data class LiveCheckRateInfoEntity(
    val id: Int? = null,
    val description: String? = null
)

data class LiveCheckRoomsEntity(
    val roomCode: String? = null,
    val name: String? = null,
    val shortDescription: String? = null,
    val description: String? = null,
    val ratePlanes: List<LiveCheckRoomRatePlansEntity>? = null,
    val image: String? = null,
    val roomImages: List<String>? = null,
    val quantity: Int? = null
)

data class LiveCheckRoomRatePlansEntity(
    val ratePlanCode: String? = null,
    val name: String? = null,
    val description: String? = null,
    val baseCurrency: String? = null,
    //val promotion: String? = null,
    val guarantee: LiveCheckRoomRatePlanGuaranteeEntity? = null,
    val mealsIncluded: Boolean? = null,
    val amount: Double? = null,
    val normalAmount: Double? = null,
    val averageAmount: Double? = null,
    val nights: List<LiveCheckRoomRatePlansNightsEntity>? = null,
    val policies: List<LiveCheckRoomRatePlansPolicyEntity>? = null
)

data class LiveCheckRoomRatePlanGuaranteeEntity(
    val code: String? = null,
    val description: String? = null,
    val chargeRequired: Boolean? = null,
    val chargePercent: Double? = null
)

data class LiveCheckRoomRatePlansNightsEntity(
    val date: String? = null,
    val amount: Double? = null,
    val normalAmount: Double? = null
)

data class LiveCheckRoomRatePlansPolicyEntity(
    val code: String? = null,
    val description: String? = null,
    val amount: Double? = null,
    val penaltyDays: Int? = null,
    val dateToApply: String? = null
)