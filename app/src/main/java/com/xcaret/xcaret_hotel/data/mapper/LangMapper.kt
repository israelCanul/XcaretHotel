package com.xcaret.xcaret_hotel.data.mapper

import android.annotation.SuppressLint
import com.xcaret.xcaret_hotel.data.entity.*
import com.xcaret.xcaret_hotel.domain.*

class LangActivityMapper: FirebaseMapper<LangActivityEntity, LangActivity>(){
    override fun map(from: LangActivityEntity?, key: String?): LangActivity? {
        return LangActivity(
            parent_uid = key,
            lang_code = from?.langCode,
            image = from?.image,
            longDescription = from?.longDescription,
            note = from?.note,
            recomendation = from?.recomendation,
            shortDescription = from?.shortDescription,
            title = from?.title,
            warning = from?.warning
        )
    }
}

class LangAwardMapper: FirebaseMapper<LangAwardEntity, LangAward>(){
    override fun map(from: LangAwardEntity?, key: String?): LangAward {
        return LangAward(
            parent_uid = key ?: "",
            name = from?.name,
            lang_code = from?.langCode
        )
    }
}

class LangCategoryMapper: FirebaseMapper<LangCategoryEntity,LangCategory>(){
    override fun map(from: LangCategoryEntity?, key: String?): LangCategory {
        return LangCategory(
            parent_uid = key ?: "",
            image = from?.image,
            longDescription = from?.longDescription,
            shortDescription = from?.shortDescription,
            title = from?.title,
            lang_code = from?.langCode
        )
    }
}

class LangContactMapper: FirebaseMapper<LangContactEntity, LangContact>(){
    override fun map(from: LangContactEntity?, key: String?): LangContact {
        return LangContact(
            parent_uid = key ?: "",
            name = from?.name,
            lang_code = from?.langCode
        )
    }
}

class LangFilterMapMapper: FirebaseMapper<LangFilterMapEntity, LangFilterMap>(){
    override fun map(from: LangFilterMapEntity?, key: String?): LangFilterMap {
        return LangFilterMap(
            parent_uid = key ?: "",
            name = from?.name,
            lang_code = from?.langCode
        )
    }
}

class LangRoomAmenityMapper: FirebaseMapper<LangRoomAmenityEntity, LangRoomAmenity>(){
    override fun map(from: LangRoomAmenityEntity?, key: String?): LangRoomAmenity {
        return LangRoomAmenity(
            parent_uid = key ?: "",
            description = from?.description,
            descriptionShort = from?.descriptionShort,
            lang_code = from?.langCode
        )
    }
}

@SuppressLint("DefaultLocale")
class LangHotelMapper: FirebaseMapper<LangHotelEntity, LangHotel>(){

    override fun map(from: LangHotelEntity?, key: String?): LangHotel {
        return LangHotel(
            parent_uid = key ?: "",
            address = from?.address,
            slogan = from?.slogan?.capitalize(),
            lang_code = from?.langCode
        )
    }
}

class LangParkTourMapper: FirebaseMapper<LangParkTourEntity, LangParkTour>(){
    override fun map(from: LangParkTourEntity?, key: String?): LangParkTour {
        return LangParkTour(
            parent_uid = key ?: "",
            address = from?.address,
            slogan = from?.slogan,
            cancellationFee = from?.cancellationFee,
            include = from?.include,
            descriptionLong = from?.longDescription,
            descriptionShort = from?.shortDescription,
            image = from?.image,
            note = from?.note,
            recomendation = from?.recomendation,
            schedule = from?.schedule,
            onBoardServices = from?.onBoardServices,
            lang_code = from?.langCode
        )
    }
}

class LangPlaceMapper: FirebaseMapper<LangPlaceEntity, LangPlace>(){
    override fun map(from: LangPlaceEntity?, key: String?): LangPlace {
        return LangPlace(
            parent_uid = key ?: "",
            image = from?.image,
            descriptionLong = from?.longDescription,
            descriptionShort = from?.shortDescription,
            location = from?.location,
            title = from?.title,
            lang_code = from?.langCode
        )
    }
}

class LangRoomTypeMapper: FirebaseMapper<LangRoomTypeEntity, LangRoomType>(){
    override fun map(from: LangRoomTypeEntity?, key: String?): LangRoomType {
        return LangRoomType(
            parent_uid = key ?: "",
            image = from?.image,
            descriptionLong = from?.descriptionLong,
            descriptionShort = from?.descriptionShort,
            title = from?.title,
            lang_code = from?.langCode
        )
    }
}

class LangLabelMapper: FirebaseMapper<LangLabelEntity, LangLabel>(){
    override fun map(from: LangLabelEntity?, key: String?): LangLabel {
        return LangLabel(
            parent_uid = key ?: "",
            value = from?.value,
            lang_code = from?.langCode
        )
    }
}

class LangRestaurantDetailMapper: FirebaseMapper<LangRestaurantDetailEntity, LangRestaurantDetail>(){
    override fun map(from: LangRestaurantDetailEntity?, key: String?): LangRestaurantDetail {
        return LangRestaurantDetail(
            parent_uid = key ?: "",
            additionalInfo = from?.additionalInfo,
            concept = from?.concept,
            needReservation = from?.needReservation == 1,
            openFor = from?.openFor,
            type = from?.type,
            lang_code = from?.langCode
        )
    }
}

class LangTitleMapper: FirebaseMapper<TitleEntity, Title>(){
    override fun map(from: TitleEntity?, key: String?): Title {
        return Title(
            parent_uid = key ?: "",
            code = from?.code,
            value = from?.value,
            name = from?.name,
            lang_code = from?.langCode,
            enabled = from?.enabled == 1
        )
    }
}

class LangFaqMapper: FirebaseMapper<LangFaqEntity, LangFaq>(){
    override fun map(from: LangFaqEntity?, key: String?): LangFaq {
        return LangFaq(
            parent_uid = key ?: "",
            lang_code = from?.langCode,
            answer = from?.answer,
            question = from?.question

        )
    }
}

class LangAFIClassMapper: FirebaseMapper<LangAFIClassEntity, LangAfiClass>(){
    override fun map(from: LangAFIClassEntity?, key: String?): LangAfiClass {
        return LangAfiClass(
            parent_uid = key ?: "",
            lang_code = from?.langCode,
            description = from?.description,
            name = from?.name

        )
    }
}

class LangDestinationMapper: FirebaseMapper<LangDestinationEntity, LangDestination>(){
    override fun map(from: LangDestinationEntity?, key: String?): LangDestination {
        return LangDestination(
            parent_uid = key ?: "",
            lang_code = from?.langCode,
            title = from?.title,
            description = from?.description,


        )
    }
}

class LangDestinationActivityMapper: FirebaseMapper<LangDestinationActivityEntity, LangDestinationActivity>(){
    override fun map(from: LangDestinationActivityEntity?, key: String?): LangDestinationActivity {
        return LangDestinationActivity(
            parent_uid = key ?: "",
            lang_code = from?.langCode,
            description = from?.description,
            title = from?.title,
            schedule = from?.schedule

        )
    }
}