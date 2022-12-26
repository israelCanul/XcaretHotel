package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.RoomLocationRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.domain.RoomLocation
import com.xcaret.xcaret_hotel.view.config.Language

class RoomLocationUseCase: BaseUseCase<RoomLocation>(){

    private val dao = database.roomLocationDao()
    private val repository: RoomLocationRepository by lazy { RoomLocationRepository() }

    override fun getDao(): BaseDao<RoomLocation>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<RoomLocation>? = repository

    fun getLocations(roomUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>> = dao.getLocations(roomUID, lang)
}