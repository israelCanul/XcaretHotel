package com.xcaret.xcaret_hotel.data.repository

import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.mapper.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.LogHX

class LangActivityRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)):
    FirebaseDatabaseRepository<LangActivity>(){

    private var rootNode = FirebaseReference.ROOT_LANG;

    init {
        this.rootNode += "$lang/${FirebaseReference.ACTIVITY}"
        LogHX.d("root", rootNode)
        init(LangActivityMapper())
    }

    override fun getRootNode(): String = rootNode

}

class LangAwardRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)):
        FirebaseDatabaseRepository<LangAward>(){

    private var rootNode = FirebaseReference.ROOT_LANG;

    init {
        this.rootNode += "$lang/${FirebaseReference.AWARD}"
        LogHX.d("root", rootNode)
        init(LangAwardMapper())
    }

    override fun getRootNode(): String = rootNode

}

class LangCategoryRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) :
    FirebaseDatabaseRepository<LangCategory>() {

    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.CATEGORY}"
        LogHX.d("root", rootNode)
        init(LangCategoryMapper())
    }

    override fun getRootNode(): String = rootNode
}

class LangCountryRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) :
        FirebaseDatabaseRepository<Country>(){
    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.COUNTRY}"
        LogHX.d("root", rootNode)
        init(CountryMapper())
    }

    override fun getRootNode(): String = rootNode
}

class LangContactRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)):
        FirebaseDatabaseRepository<LangContact>(){

    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.CONTACT}"
        LogHX.d("root", rootNode)
        init(LangContactMapper())
    }

    override fun getRootNode(): String = rootNode
}

class LangFilterMapRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)):
        FirebaseDatabaseRepository<LangFilterMap>(){

    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.FILTER_MAP}"
        init(LangFilterMapMapper())
    }

    override fun getRootNode(): String = rootNode
}

class LangRoomAmenityRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) :
    FirebaseDatabaseRepository<LangRoomAmenity>() {

    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.ROOM_AMENITY}"
        LogHX.d("root", rootNode)
        init(LangRoomAmenityMapper())
    }

    override fun getRootNode(): String = rootNode
}


class LangHotelRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) :
    FirebaseDatabaseRepository<LangHotel>() {

    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.HOTEL}"
        LogHX.d("root", rootNode)
        init(LangHotelMapper())
    }

    override fun getRootNode(): String = rootNode
}

class LangParkTourRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) :
    FirebaseDatabaseRepository<LangParkTour>() {

    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.PARK_TOUR}"
        LogHX.d("root", rootNode)
        init(LangParkTourMapper())
    }

    override fun getRootNode(): String = rootNode
}


class LangPlaceRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) :
    FirebaseDatabaseRepository<LangPlace>() {

    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.PLACE}"
        LogHX.d("root", rootNode)
        init(LangPlaceMapper())
    }

    override fun getRootNode(): String = rootNode
}

class LangRoomTypeRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) :
    FirebaseDatabaseRepository<LangRoomType>() {

    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.ROOM_TYPE}"
        LogHX.d("root", rootNode)
        init(LangRoomTypeMapper())
    }

    override fun getRootNode(): String = rootNode
}

class LangLabelRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) :
    FirebaseDatabaseRepository<LangLabel>() {

    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.LABEL}"
        LogHX.d("root", rootNode)
        init(LangLabelMapper())
    }

    override fun getRootNode(): String = rootNode
}

class LangRestaurantDetailRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)):
        FirebaseDatabaseRepository<LangRestaurantDetail>(){

    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.RESTAURANT_DETAIL}"
        init(LangRestaurantDetailMapper())
    }

    override fun getRootNode(): String = rootNode
}

class TitleRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)):
        FirebaseDatabaseRepository<Title>(){
    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.TITLE}"
        init(LangTitleMapper())
    }

    override fun getRootNode(): String = rootNode
}

class LangFaqRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)):
    FirebaseDatabaseRepository<LangFaq>(){
    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.FAQ}"
        init(LangFaqMapper())
    }

    override fun getRootNode(): String = rootNode
}
class LangDestinationActivityRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)):
    FirebaseDatabaseRepository<LangDestinationActivity>(){
    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.DESTINATION_ACTIVITY}"
        init(LangDestinationActivityMapper())
    }

    override fun getRootNode(): String = rootNode
}
class LangDestinationRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)):
    FirebaseDatabaseRepository<LangDestination>(){
    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.DESTINATION}"
        init(LangDestinationMapper())
    }

    override fun getRootNode(): String = rootNode
}
class LangAFIClassRepository(lang: String = Language.getLangCode(HotelXcaretApp.mContext)):
    FirebaseDatabaseRepository<LangAfiClass>(){
    private var rootNode = FirebaseReference.ROOT_LANG

    init {
        this.rootNode += "$lang/${FirebaseReference.AFICLASS}"
        init(LangAFIClassMapper())
    }

    override fun getRootNode(): String = rootNode
}