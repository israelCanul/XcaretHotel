package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.PlaceRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.Settings

class PlaceUseCase: BaseUseCase<Place>(){

    private val dao = database.placeDao()
    private val repository = PlaceRepository()

    override fun getDao(): BaseDao<Place>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<Place>? = repository

    fun findById(placeUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Place?> = dao.findById(placeUID, lang)
    fun findHotelsPlace(categoryUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>> = dao.findHotelsPlace(categoryUID, lang)
    fun findByCategory(categoryUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>> = dao.findByCategory(categoryUID, lang)
    fun findWithIn(placeIDS: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext), hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): List<Place> = dao.findWithIn(placeIDS, lang, hotelUID)
    fun findInCategory(categoryIds: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext), hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): LiveData<List<Place>> = dao.findInCategory(categoryIds, lang, hotelUID)
    fun findByCategoryAndHotel(catUID: String, hotelUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Place?> = dao.findByCategoryAndHotel(catUID, hotelUID, lang)
    fun allByHotel(hotelUID: String, type: String = Constants.PLACE, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>> = dao.allByHotel(hotelUID, type, lang)
    fun search(query: String, hotelUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>> = dao.search("%$query%", hotelUID, lang)
    fun forceFindByCategoryOutHotel(categoryUID: String, hotelUID: String): List<Place> = dao.forceFindByCategory(categoryUID, hotelUID)
}