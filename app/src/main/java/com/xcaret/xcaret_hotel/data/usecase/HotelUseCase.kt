package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.HotelRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Hotel
import com.xcaret.xcaret_hotel.view.config.Language

class HotelUseCase: BaseUseCase<Hotel>() {

    private val dao = database.hotelDao()
    private val repository: HotelRepository by lazy { HotelRepository() }

    override fun getDao(): BaseDao<Hotel>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<Hotel>? = repository

    fun all(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Hotel>> = dao.all(lang)
    fun allNoFilter(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Hotel>> = dao.allNoFilter()
    fun findByCode(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Hotel?> = dao.findByCode(code, lang)
}