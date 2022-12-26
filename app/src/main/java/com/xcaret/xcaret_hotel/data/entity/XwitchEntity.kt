package com.xcaret.xcaret_hotel.data.entity


data class TranslateEntity(
    var lanCode: String? = null,
    var status: Boolean = false,
    var message: String? = null
)

//data class PathResponseEntity(
//    var id: String? = null,
//    var uid: String? = null,
//    var totalWeight: Int? = null,
//    var pointACode: String? = null,
//    var pointBCode: String? = null,
//    var pointA: PointsResponse? = null,
//    var pointB: PointsResponse? = null,
//    var vectors: List<VectorsResponse>? = null
//)
