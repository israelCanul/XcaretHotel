package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.RoomTypeRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.domain.RoomType
import com.xcaret.xcaret_hotel.view.config.Language

class RoomTypeUseCase: BaseUseCase<RoomType>(){

    private val dao = database.roomTypeDao()
    private val repository: RoomTypeRepository by lazy { RoomTypeRepository() }

    override fun getDao(): BaseDao<RoomType>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<RoomType>? = repository

    fun all(hotelUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<RoomType>> = dao.all(hotelUID, lang)
    fun findByCategory(categoryUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<RoomType>> = dao.findByCategory(categoryUID, lang)
    fun findById(roomUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<RoomType?> = dao.findById(roomUID, lang)
    fun findByLocation(placeUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<RoomType>> = dao.findByLocation(placeUID, lang)
    fun findByListCodes(codes: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<RoomType> = dao.findByListCodes(codes, lang)
}