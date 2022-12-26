package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.FilterMapRepository
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.FilterMap
import com.xcaret.xcaret_hotel.view.config.Language

class FilterMapUseCase: BaseUseCase<FilterMap>(){
    private val dao = database.filterMapDao()
    private val repository: FilterMapRepository by lazy { FilterMapRepository() }

    override fun getDao(): BaseDao<FilterMap>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<FilterMap>? = repository

    fun findByHotel(hotelUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = dao.findByHotel(hotelUID, lang)
}