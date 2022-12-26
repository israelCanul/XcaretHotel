package com.xcaret.xcaret_hotel.data.repository

import com.xcaret.xcaret_hotel.data.entity.AFIClassEntity
import com.xcaret.xcaret_hotel.data.mapper.*
import com.xcaret.xcaret_hotel.domain.*

class AirlineRepository(): FirebaseDatabaseRepository<Airline>(AirlineMapper()) {
    override fun getRootNode(): String = FirebaseReference.AIRLINES
}

class AirlineTerminalRepository(): FirebaseDatabaseRepository<AirlineTerminal>(AirlineTerminalMapper()){
    override fun getRootNode(): String = FirebaseReference.AIRLINE_TERMINAL
}

class AwardRepository(): FirebaseDatabaseRepository<Award>(AwardMapper()){
    override fun getRootNode(): String = FirebaseReference.AWARD
}

class CategoryRepository() : FirebaseDatabaseRepository<Category>(CategoryMapper()) {
    override fun getRootNode(): String = FirebaseReference.CATEGORY
}

class ContactRepository(): FirebaseDatabaseRepository<Contact>(ContactMapper()){
    override fun getRootNode(): String = FirebaseReference.CONTACT
}

class CurrencyRepository(): FirebaseDatabaseRepository<Currency>(CurrencyMapper()){
    override fun getRootNode(): String = FirebaseReference.CURRENCY
}

class FilterMapRepository(): FirebaseDatabaseRepository<FilterMap>(FilterMapMapper()){
    override fun getRootNode(): String = FirebaseReference.FILTER_MAP
}

class HouseIdRepository(): FirebaseDatabaseRepository<HouseId>(HouseIdMapper()){
    override fun getRootNode(): String = FirebaseReference.HOUSE_ID
}

class LanguageRepository(): FirebaseDatabaseRepository<Language>(LanguageMapper()) {
    override fun getRootNode(): String = FirebaseReference.LANGUAGE
}

class LevelRoomRepository(): FirebaseDatabaseRepository<LevelRoom>(LevelRoomMapper()){
    override fun getRootNode(): String = FirebaseReference.LEVEL_ROOM
}

class MapConfigRepository(): FirebaseDatabaseRepository<MapConfig>(MapConfigMapper()){
    override fun getRootNode(): String = FirebaseReference.MAPCONFIG
}

class NearPlaceRepository(): FirebaseDatabaseRepository<NearPlace>(NearPlaceMapper()){
    override fun getRootNode(): String = FirebaseReference.NEAR_PLACE
}

class ParamSettingRepository(): FirebaseDatabaseRepository<ParamSetting>(ParamSettingMapper()){
    override fun getRootNode(): String = FirebaseReference.PARAM_SETTING
}

class PhoneCodeRepository(): FirebaseDatabaseRepository<PhoneCode>(PhoneCodeMapper()){
    override fun getRootNode(): String = FirebaseReference.PHONE_CODE
}

class RoomAmenityRepository(): FirebaseDatabaseRepository<RoomAmenity>(RoomAmenityMapper()) {
    override fun getRootNode(): String = FirebaseReference.ROOM_AMENITY
}

class RoomLocationRepository(): FirebaseDatabaseRepository<RoomLocation>(RoomLocationMapper()) {
    override fun getRootNode(): String = FirebaseReference.ROOM_LOCATION
}

class ScheduleActivityRepository(): FirebaseDatabaseRepository<ScheduleActivity>(ScheduleActivityMapper()){
    override fun getRootNode(): String = FirebaseReference.SCHEDULE_ACTIVITY
}

class FaqRepository(): FirebaseDatabaseRepository<Faq>(FaqMapper()){
    override fun getRootNode(): String = FirebaseReference.FAQ
}

class DestinationRepository(): FirebaseDatabaseRepository<Destination>(DestinationMapper()){
    override fun getRootNode(): String = FirebaseReference.DESTINATION
}

class AFIClassRepository(): FirebaseDatabaseRepository<AfiClass>(AFIClassMapper()){
    override fun getRootNode(): String = FirebaseReference.AFICLASS
}

class DestinationActivityRepository(): FirebaseDatabaseRepository<DestinationActivity>(DestinationActivityMapper()){
    override fun getRootNode(): String = FirebaseReference.DESTINATION_ACTIVITY
}