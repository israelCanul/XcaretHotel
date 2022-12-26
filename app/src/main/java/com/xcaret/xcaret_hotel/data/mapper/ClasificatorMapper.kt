package com.xcaret.xcaret_hotel.data.mapper

import com.xcaret.xcaret_hotel.data.entity.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Constants

class AirlineMapper: FirebaseMapper<AirlinesEntity, Airline>(){
    override fun map(from: AirlinesEntity?, key: String?): Airline {
        return Airline(
            uid = key ?: "",
            name = from?.name ?: "",
            code = from?.code,
            enabled = from?.enabled == 1,
            order = from?.order ?: 0
        )
    }
}

class AirlineTerminalMapper: FirebaseMapper<AirlineTerminalEntity, AirlineTerminal>(){
    override fun map(from: AirlineTerminalEntity?, key: String?): AirlineTerminal? {
        return AirlineTerminal(
            uid = key ?: "",
            number = from?.number ?: "",
            code = from?.code ?: "",
            order = from?.order ?: 0,
            enabled = from?.enabled == 1
        )
    }
}

class AwardMapper: FirebaseMapper<AwardEntity, Award>(){
    override fun map(from: AwardEntity?, key: String?): Award {
        return Award(
            uid = key ?: "",
            parkUID = from?.parkUID,
            placeUID = from?.placeUID,
            icon = from?.icon,
            order = from?.order ?: 0,
            enabled = from?.enabled == 1
        )
    }
}

class CategoryMapper: FirebaseMapper<CategoryEntity, Category>(){
    override fun map(from: CategoryEntity?, key: String?): Category {
        return Category(
            uid = key ?: "",
            hotelUID = from?.hotelUID,
            filterGrouper = from?.filterGrouper,
            code = from?.code,
            icon = from?.icon,
            showInHome = from?.showInHome == 1,
            priority = from?.priority ?: 0,
            enabled = from?.enabled == 1,
        )
    }
}

class ContactMapper: FirebaseMapper<ContactEntity, Contact>(){
    override fun map(from: ContactEntity?, key: String?): Contact {
        return Contact(
            uid = key ?: "",
            categoryUID = from?.categoryUID,
            hotelUID = from?.hotelUID,
            value = from?.value,
            code = from?.code,
            icon = from?.icon,
            type = from?.type,
            order = from?.order ?: 0,
            enabled = from?.enabled == 1
        )
    }
}

class CountryMapper: FirebaseMapper<CountryEntity, Country>(){
    override fun map(from: CountryEntity?, key: String?): Country {
        val country = Country(
            id = from?.id,
            iSO = from?.iSO,
            iSO2 = from?.iSO2,
            continent = from?.continent,
            name = from?.name,
            region = from?.region
        )

        from?.states?.let { states ->
            states.forEach {  state ->
                country.states.add(State(id = state.id, countryId = from.id, abbreviation = state.abbreviation, name = state.name))
            }
        }

        return country
    }
}

class CurrencyMapper: FirebaseMapper<CurrencyEntity, Currency>(){
    override fun map(from: CurrencyEntity?, key: String?): Currency {
        return Currency(
            id = from?.id ?: 0,
            iso = from?.iso,
            name = from?.name,
            symbol = from?.symbol,
            enabled = from?.enabled == 1,
            icon = from?.icon,
            miles = from?.miles ?: Constants.MILES_DEFAULT,
            decimal = from?.decimal ?: Constants.DECIMAL_DEFAULT,
            isoCountry = from?.isoCountry,
            isoPayment = from?.isoPayment

        )
    }
}

class FilterMapMapper: FirebaseMapper<FilterMapEntity, FilterMap>(){
    override fun map(from: FilterMapEntity?, key: String?): FilterMap {
        return FilterMap(
            uid = key ?: "",
            categoryUID = from?.categoryUID,
            hotelUID = from?.hotelUID,
            parentUID = from?.parentUID,
            order = from?.order ?: 0,
            type = from?.type,
            code = from?.code,
            icon = from?.icon,
            enabled = from?.enabled == 1
        )
    }
}

class HouseIdMapper: FirebaseMapper<HouseIdEntity, HouseId>(){
    override fun map(from: HouseIdEntity?, key: String?): HouseId {
        return HouseId(
            uid = key ?: "",
            buildingUID = from?.buildingUID,
            id = from?.id ?: 0
        )
    }
}

class LanguageMapper: FirebaseMapper<LanguageEntity, Language>(){
    override fun map(from: LanguageEntity?, key: String?): Language {
        return Language(
            uid = key ?: "",
            code = from?.code,
            countryCode = from?.countryCode,
            name = from?.name,
            nameSF = from?.nameSF ?: Constants.LANGUAGE_SF_DEFAULT,
            icon = from?.icon,
            enabled = from?.enabled == 1,
            isTranslated = from?.translating ==1
        )
    }
}

class LevelRoomMapper: FirebaseMapper<LevelRoomEntity, LevelRoom>(){
    override fun map(from: LevelRoomEntity?, key: String?): LevelRoom {
        return LevelRoom(
            uid = key ?: "",
            buildingUID = from?.buildingUID,
            houseId = from?.houseId ?: 0,
            level = from?.level ?: 0,
            number = from?.number,
            isSpecial = from?.isSpecial == 1,
            enabled = from?.enabled == 1
        )
    }
}

class MapConfigMapper: FirebaseMapper<MapConfigEntity, MapConfig>(){
    override fun map(from: MapConfigEntity?, key: String?): MapConfig {
        return MapConfig(
            uid = key ?: "",
            bound1Lat = from?.bound1Lat,
            bound1Lon = from?.bound1Lon,
            bound2Lat = from?.bound2Lat,
            bound2Lon = from?.bound2Lon,
            defaultZoom = from?.defaultZoom,
            hotelUID = from?.hotelUID,
            imgOverlay = from?.imgOverlay,
            latitude = from?.latitude,
            longitude = from?.longitude,
            maxZoom = from?.maxZoom,
            minZoom = from?.minZoom,
            overlay1Lat = from?.overlay1Lat,
            overlay1Lon = from?.overlay1Lon,
            overlay2Lat = from?.overlay2Lat,
            overlay2Lon = from?.overlay2Lon,
            platform = from?.platform,
            radiusLimit = from?.radiusLimit
        )
    }
}

class NearPlaceMapper: FirebaseMapper<NearPlaceEntity, NearPlace>(){
    override fun map(from: NearPlaceEntity?, key: String?): NearPlace {
        return NearPlace(
            uid = key ?: "",
            placeUID = from?.placeUID,
            nearPlaceUID = from?.nearPlaceUID,
            order = from?.order ?: 0
        )
    }
}

class ParamSettingMapper: FirebaseMapper<ParamSettingEntity, ParamSetting>(){
    override fun map(from: ParamSettingEntity?, key: String?): ParamSetting {
        return ParamSetting(
            code = key ?: "",
            value = from?.value
        )
    }
}

class PhoneCodeMapper: FirebaseMapper<PhoneCodeEntity, PhoneCode>(){
    override fun map(from: PhoneCodeEntity?, key: String?): PhoneCode {
        return PhoneCode(
            iso = from?.iso,
            iso2 = from?.iso2,
            id = from?.id,
            name = from?.name,
            code = from?.code ?: ""
        )
    }
}

class RoomAmenityMapper: FirebaseMapper<RoomAmenityEntity, RoomAmenity>(){
    override fun map(from: RoomAmenityEntity?, key: String?): RoomAmenity {
        return RoomAmenity(
            uid = key ?: "",
            categoryUID = from?.categoryUID,
            code = from?.code,
            icon = from?.icon,
            roomTypeUID = from?.roomTypeUID,
            enabled = from?.enabled == 1,
            order = from?.order ?: 0
        )
    }
}

class RoomLocationMapper: FirebaseMapper<RoomLocationEntity, RoomLocation>(){
    override fun map(from: RoomLocationEntity?, key: String?): RoomLocation {
        return RoomLocation(
            uid = key ?: "",
            placeUID = from?.placeUID,
            roomUID = from?.roomUID,
            order = from?.order ?: 0
        )
    }
}

class ScheduleActivityMapper: FirebaseMapper<ScheduleActivityEntity, ScheduleActivity>(){
    override fun map(from: ScheduleActivityEntity?, key: String?): ScheduleActivity {
        return ScheduleActivity(
            uid = key ?: "",
            activityUID = from?.activityUID,
            day = from?.day,
            enabled = from?.enabled == 1,
            hourStart = from?.hourStart,
            hourEnd = from?.hourEnd
        )
    }
}

class FaqMapper: FirebaseMapper<FaqEntity, Faq>(){
    override fun map(from: FaqEntity?, key: String?): Faq {
        return Faq(
            uid = key ?: "",
            hotelUID = from?.hotelUID,
            code = from?.code,
            categoryUID = from?.categoryUID,
            order = from?.order,
            enabled = from?.enabled
        )
    }
}
class AFIClassMapper: FirebaseMapper<AFIClassEntity, AfiClass>(){
    override fun map(from: AFIClassEntity?, key: String?): AfiClass {
        return AfiClass(
            uid = key ?: "",
            icon = from?.icon,
            code = from?.code,
            order = from?.order,
            enabled = from?.enabled == 1
        )
    }
}
class DestinationMapper: FirebaseMapper<DestinationEntity, Destination>(){
    override fun map(from: DestinationEntity?, key: String?): Destination {
        return Destination(
            uid = key ?: "",
            code = from?.code,
            image = from?.image,
            order = from?.order,
            status = from?.status
        )
    }
}
class DestinationActivityMapper: FirebaseMapper<DestinationActivityEntity, DestinationActivity>(){
    override fun map(from: DestinationActivityEntity?, key: String?): DestinationActivity {
        return DestinationActivity(
            uid = key ?: "",
            destinationUID = from?.destinationUID,
            code = from?.code,
            image = from?.image,
            order = from?.order,
            enabled = from?.enabled ==1 ,
            type = from?.type
        )
    }
}
