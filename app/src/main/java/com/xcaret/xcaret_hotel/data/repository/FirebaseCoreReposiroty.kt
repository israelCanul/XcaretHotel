package com.xcaret.xcaret_hotel.data.repository

import com.xcaret.xcaret_hotel.data.mapper.*
import com.xcaret.xcaret_hotel.domain.*

class ActivityRepository(): FirebaseDatabaseRepository<Activity>(ActivityMapper()){
    override fun getRootNode(): String = FirebaseReference.ACTIVITY
}

class HotelRepository(): FirebaseDatabaseRepository<Hotel>(HotelMapper()){
    override fun getRootNode(): String = FirebaseReference.HOTEL
}

class ParkTourRepository(): FirebaseDatabaseRepository<ParkTour>(ParkTourMapper()){
    override fun getRootNode(): String = FirebaseReference.PARK_TOUR
}

class PlaceRepository(): FirebaseDatabaseRepository<Place>(PlaceMapper()){
    override fun getRootNode(): String = FirebaseReference.PLACE
}

class RoomTypeRepository(): FirebaseDatabaseRepository<RoomType>(RoomTypeMapper()){
    override fun getRootNode(): String = FirebaseReference.ROOM_TYPE
}

class WebCamRepository(): FirebaseDatabaseRepository<WebCam>(WebCamMapper()){
    override fun getRootNode(): String = FirebaseReference.WEBCAM
}