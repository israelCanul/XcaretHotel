package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.AwardRepository
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Award
import com.xcaret.xcaret_hotel.view.config.Language

class AwardUseCase: BaseUseCase<Award>() {

    private val dao = database.awardDao()
    private val repository by lazy { AwardRepository() }

    override fun getDao(): BaseDao<Award>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<Award>? = repository

    fun findByPlaceUID(placeUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Award>> = dao.findByPlaceUID(placeUID)
    fun findByParkUID(parkUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Award>> = dao.findByParkUID(parkUID)
}