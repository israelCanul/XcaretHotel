package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.*
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.data.room.dao.BaseLangDao
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class LangActivityUseCase: BaseLangUseCase<LangActivity>(){
    private val dao = database.langActivityDao()
    private val repository by lazy { LangActivityRepository() }

    override fun getDao(): BaseLangDao<LangActivity> = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangActivity> = repository
}

class LangAwardUseCase: BaseLangUseCase<LangAward>(){
    private val dao = database.langAwardDao()
    private val repository = LangAwardRepository()

    override fun getDao(): BaseLangDao<LangAward>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangAward>? = repository
}

class LangCategoryUseCase: BaseLangUseCase<LangCategory>(){

    private val dao = database.langCategoryDao()
    private val repository = LangCategoryRepository()

    override fun getDao(): BaseLangDao<LangCategory>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangCategory>? = repository

    fun findLangCategory(UID:String,lang: String = Language.getLangCode(HotelXcaretApp.mContext)):LiveData<LangCategory?> = dao.findByUID(UID,lang)

    fun findByFilterGrouper(filterGrouper:String,lang: String = Language.getLangCode(HotelXcaretApp.mContext)):List<LangCategory?> = dao.findByFilterGrouper(filterGrouper)
}

class LangContactUseCase: BaseLangUseCase<LangContact>(){

    private val dao = database.langContactDao()
    private val repository = LangContactRepository()

    override fun getDao(): BaseLangDao<LangContact>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangContact>? = repository
}

class LangFilterMapUseCase: BaseLangUseCase<LangFilterMap>(){

    private val dao = database.langFilterMapDao()
    private val repository = LangFilterMapRepository()

    override fun getDao(): BaseLangDao<LangFilterMap>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangFilterMap>? = repository
}

class LangRoomAmenityUseCase: BaseLangUseCase<LangRoomAmenity>(){

    private val dao = database.langRoomAmenityDao()
    private val repository = LangRoomAmenityRepository()

    override fun getDao(): BaseLangDao<LangRoomAmenity>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangRoomAmenity>? = repository
}

class LangHotelUseCase: BaseLangUseCase<LangHotel>(){

    private val dao = database.langHotelDao()
    private val repository: LangHotelRepository by lazy { LangHotelRepository() }

    override fun getDao(): BaseLangDao<LangHotel>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangHotel>? = repository
}

class LangParkTourUseCase: BaseLangUseCase<LangParkTour>(){

    private val dao = database.langParkTour()
    private val repository: LangParkTourRepository by lazy { LangParkTourRepository() }

    override fun getDao(): BaseLangDao<LangParkTour>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangParkTour>? = repository
}

class LangPlaceUseCase: BaseLangUseCase<LangPlace>(){

    private val dao = database.langPlaceDao()
    private val repository: LangPlaceRepository by lazy { LangPlaceRepository() }

    override fun getDao(): BaseLangDao<LangPlace>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangPlace>? = repository
}

class LangRoomTypeUseCase: BaseLangUseCase<LangRoomType>(){

    private val dao = database.langRoomTypeDao()
    private val repository: LangRoomTypeRepository by lazy { LangRoomTypeRepository() }

    override fun getDao(): BaseLangDao<LangRoomType>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangRoomType>? = repository
}

class LangLabelUseCase: BaseLangUseCase<LangLabel>(){

    private val dao = database.langLabelDao()
    private val repository: LangLabelRepository by lazy { LangLabelRepository() }

    override fun getDao(): BaseLangDao<LangLabel>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangLabel>? = repository


    fun findLabel(key: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)):LiveData<LangLabel?> = dao.findLabel(key, lang)

    fun findLabelOutLive(key: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LangLabel? = dao.findLabelOutLive(key, lang)
}

class LangRestaurantDetailUseCase: BaseLangUseCase<LangRestaurantDetail>(){
    private val dao = database.langRestaurantDetailDao()
    private val repository: LangRestaurantDetailRepository by lazy { LangRestaurantDetailRepository() }

    override fun getDao(): BaseLangDao<LangRestaurantDetail>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangRestaurantDetail>? = repository

    fun `in`(restIds: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<LangRestaurantDetail> = dao.`in`(restIds, lang)
    fun findById(restId: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<LangRestaurantDetail?> = dao.findById(restId, lang)
}

class LangTitleUseCase: BaseLangUseCase<Title>(){
    private val dao = database.titleDao()
    private val repository: TitleRepository by lazy { TitleRepository() }

    override fun getDao(): BaseLangDao<Title> = dao
    override fun getRepository(): FirebaseDatabaseRepository<Title> = repository

    fun all(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = dao.all(lang)
    fun first(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = dao.first(lang)
    fun firstOutLiveData(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): Title? = dao.firstOutLiveData(lang)
    fun findByCode(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Title?> = dao.findByCode(code, lang)
    fun findByCodeOutLiveData(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): Title? = dao.findByCodeOutLiveData(code, lang)
}

class LangFaqUseCase: BaseLangUseCase<LangFaq>(){

    private val dao = database.langFaqDao()
    private val repository = LangFaqRepository()

    override fun getDao(): BaseLangDao<LangFaq>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangFaq>? = repository

    fun findLangFaq(UID:String,lang: String = Language.getLangCode(HotelXcaretApp.mContext)):LiveData<LangFaq?> = dao.findLabel(UID,lang)
}

class LangDestinationUseCase: BaseLangUseCase<LangDestination>(){

    private val dao = database.langDestinationDao()
    private val repository = LangDestinationRepository()

    override fun getDao(): BaseLangDao<LangDestination>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangDestination>? = repository


}

class LangDestinationActivityUseCase: BaseLangUseCase<LangDestinationActivity>(){

    private val dao = database.langDestinationActivityDao()
    private val repository = LangDestinationActivityRepository()

    override fun getDao(): BaseLangDao<LangDestinationActivity>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangDestinationActivity>? = repository


}

class LangAFIClassUseCase: BaseLangUseCase<LangAfiClass>(){

    private val dao = database.langAfiClassDao()
    private val repository = LangAFIClassRepository()

    override fun getDao(): BaseLangDao<LangAfiClass>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangAfiClass>? = repository


}
