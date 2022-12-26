package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.ParkTourRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.ParkTour
import com.xcaret.xcaret_hotel.view.config.Language

class ParkTourUseCase: BaseUseCase<ParkTour>(){
    private val dao = database.parkTourDao()
    private val repository: ParkTourRepository by lazy { ParkTourRepository() }

    override fun getDao(): BaseDao<ParkTour>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<ParkTour>? = repository

    fun getByCategory(categoryUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<ParkTour>> = dao.getByCategory(categoryUID, lang)
    fun findById(parkUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<ParkTour> = dao.findById(parkUID, lang)
    fun getByClassUID(classUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<ParkTour>> = dao.getByClass(classUID, lang)
}