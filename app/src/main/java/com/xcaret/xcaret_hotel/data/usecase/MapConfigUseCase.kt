package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.MapConfigRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.MapConfig
import com.xcaret.xcaret_hotel.view.config.Constants

class MapConfigUseCase: BaseUseCase<MapConfig>() {
    private val dao = database.mapConfigDao()
    private val repository: MapConfigRepository by lazy { MapConfigRepository() }

    override fun getDao(): BaseDao<MapConfig>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<MapConfig>? = repository

    fun findByHotel(hotelUID: String, platform: String = Constants.PLATFORM) = dao.findByHotel(hotelUID)
    fun first(hotelUID: String, platform: String = Constants.PLATFORM) = dao.first(hotelUID)

}