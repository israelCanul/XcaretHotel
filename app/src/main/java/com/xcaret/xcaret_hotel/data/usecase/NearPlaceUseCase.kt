package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.NearPlaceRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.NearPlace
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.view.config.Language

class NearPlaceUseCase: BaseUseCase<NearPlace>(){

    private val dao = database.nearPlaceDao()
    private val repository: NearPlaceRepository by lazy { NearPlaceRepository() }

    override fun getDao(): BaseDao<NearPlace>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<NearPlace>? = repository

    fun findNearPlace(placeUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>> = dao.findNearPlace(placeUID, lang)
}