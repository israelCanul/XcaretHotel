package com.xcaret.xcaret_hotel.data.mapper

import com.xcaret.xcaret_hotel.data.entity.*
import com.xcaret.xcaret_hotel.domain.*

class ActivityMapper: FirebaseMapper<ActivityEntity, Activity>(){
    override fun map(from: ActivityEntity?, key: String?): Activity? {
        return Activity(
            uid = key ?: "",
            capacity = from?.capacity,
            categoryUID = from?.categoryUID,
            code = from?.code,
            duration = from?.duration,
            enabled = from?.enabled == 1,
            hotelUID = from?.hotelUID,
            icon = from?.icon,
            placeUID = from?.placeUID
        )
    }
}

class HotelMapper: FirebaseMapper<HotelEntity, Hotel>(){
    override fun map(from: HotelEntity?, key: String?): Hotel {
        return Hotel(
            uid = key ?: "",
            idSynxis = from?.idSynxis,
            name = from?.name,
            code = from?.code,
            logo = from?.logo,
            enabled = from?.enabled == 1,
            paxDefault = from?.paxDefault ?: "",
            paxRules = from?.paxRules ?: "",
            bookingActive = from?.bookingActive == 1,
            baseColor = from?.baseColor,
            baseColorDark = from?.baseColorDark,
            order = from?.order ?: 0,
            minimumAgeChildren = from?.minimumAgeChildren?:0,
            maxNight = from?.max_night?:0,
            minNight = from?.min_night?:0

        )
    }
}

class ParkTourMapper: FirebaseMapper<ParkTourEntity, ParkTour>(){
    override fun map(from: ParkTourEntity?, key: String?): ParkTour {
        return ParkTour(
            uid = key ?: "",
            name = from?.name,
            categoryUID = from?.categoryUID,
            sivexCode = from?.sivexCode,
            code = from?.code,
            enabled = from?.enabled == 1,
            isPark = from?.isPark == 1,
            latitude = from?.latitude,
            longitude = from?.longitude,
            needReservation = from?.needReservation == 1,
            tags = from?.tags,
            order = from?.order ?: 0,
            classUID = from?.classUID
        )
    }
}

class PlaceMapper: FirebaseMapper<PlaceEntity, Place>(){
    override fun map(from: PlaceEntity?, key: String?): Place {
        return Place(
            uid = key ?: "",
            categoryUID = from?.categoryUID,
            contactCategoryUID = from?.contactCategoryUID,
            code = from?.code,
            enabled = from?.enabled == 1,
            extra = from?.extra,
            hotelUID = from?.hotelUID,
            iconMap = from?.iconMap,
            level = from?.level,
            colorMarkerMap = from?.colorMarkerMap,
            latitude = from?.latitude,
            longitude = from?.longitude,
            parentUID = from?.parentUID,
            order = from?.order ?: 0,
            weather = from?.weather == 1,
            type = from?.type
        )
    }
}

class RoomTypeMapper: FirebaseMapper<RoomTypeEntity, RoomType>(){
    override fun map(from: RoomTypeEntity?, key: String?): RoomType {
        return RoomType(
            uid = key ?: "",
            code = from?.code,
            codeSynxis = from?.codeSynxis,
            hotelUID = from?.hotelUID,
            categoryUID = from?.categoryUID,
            enabled = from?.enabled == 1,
            order = from?.order ?: 0,
            tour360 = from?.tour360,
            galleryMap = from?.gallery
        )
    }
}

class WebCamMapper: FirebaseMapper<WebCamEntity, WebCam>(){
    override fun map(from: WebCamEntity?, key: String?): WebCam {
        return WebCam(
            uid = key ?: "",
            categoryUID = from?.categoryUID,
            name = from?.name,
            image = from?.image,
            videoId = from?.videoId,
            url = from?.url,
            order = from?.order ?: 0,
            code = from?.code,
            enabled = from?.enabled == 1
        )
    }
}