package com.xcaret.xcaret_hotel.data.entity

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class LangActivityEntity(
    val image: String? = null,
    val langCode: String? = null,
    val longDescription: String? = null,
    val note: String? = null,
    val recomendation: String? = null,
    val shortDescription: String? = null,
    val title: String? = null,
    val warning: String? = null
)

@IgnoreExtraProperties
data class LangAwardEntity(
    val name: String? = null,
    val langCode: String? = null
)

@IgnoreExtraProperties
data class LangCategoryEntity(
    val shortDescription: String? = null,
    val longDescription: String? = null,
    val title: String? = null,
    val image: String? = null,
    var langCode: String? = null
)

@IgnoreExtraProperties
data class LangFaqEntity(
    val answer: String? = null,
    val question: String? = null,
    val langCode: String? = null
)

@IgnoreExtraProperties
data class LangContactEntity(
    val name: String? = null,
    val langCode: String? = null
)

@IgnoreExtraProperties
data class LangFilterMapEntity(
    val name: String? = null,
    val langCode: String? = null
)

@IgnoreExtraProperties
data class LangRoomAmenityEntity(
    val description: String? = null,
    val descriptionShort: String? = null,
    val langCode: String? = null
)

@IgnoreExtraProperties
data class LangAFIClassEntity(
    val description: String? = null,
    val name: String? = null,
    val langCode: String? = null
)

@IgnoreExtraProperties
data class LangDestinationEntity(
    val description:String? = null,
    val title: String? = null,
    val langCode: String? = null
)

@IgnoreExtraProperties
data class LangDestinationActivityEntity(
    val description: String? = null,
    val title: String? = null,
    val langCode: String? = null,
    val schedule: String? = null
)

//region lang core

@IgnoreExtraProperties
data class LangHotelEntity(
    val address: String? = null,
    val slogan: String? = null,
    val langCode: String? = null
)

@IgnoreExtraProperties
data class LangParkTourEntity(
    val address: String? = null,
    val slogan: String? = null,
    val cancellationFee: Int = 0,
    val include: String? = null,
    val longDescription: String? = null,
    val shortDescription: String? = null,
    val image: String? = null,
    val note: String? = null,
    val recomendation: String? = null,
    val schedule: String? = null,
    val langCode: String? = null,
    val onBoardServices: String? = null
)

@IgnoreExtraProperties
data class LangPlaceEntity(
    val title: String? = null,
    val longDescription: String? = null,
    val shortDescription: String? = null,
    val location: String? = null,
    val image: String? = null,
    val langCode: String? = null
)

@IgnoreExtraProperties
data class LangRoomTypeEntity(
    val title: String? = null,
    val descriptionLong: String? = null,
    val descriptionShort: String? = null,
    val image: String? = null,
    val langCode: String? = null
)

@IgnoreExtraProperties
data class LangRestaurantDetailEntity(
    val additionalInfo: String? = null,
    val concept: String? = null,
    val needReservation: Int? = null,
    val openFor: String? = null,
    val type: String? = null,
    val langCode: String? = null
)

@IgnoreExtraProperties
data class TitleEntity(
    val code: String? = null,
    val value: String? = null,
    val name: String? = null,
    val enabled: Int? = null,
    val langCode: String? = null
)

//endregion

// region labels
data class LangLabelEntity(
    var uid: String? = null,
    val value: String? = null,
    var langCode: String? = null
)
// endregion