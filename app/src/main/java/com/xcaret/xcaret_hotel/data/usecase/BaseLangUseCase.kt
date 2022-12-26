package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.HotelAppDatabase
import com.xcaret.xcaret_hotel.data.room.dao.BaseLangDao
import com.xcaret.xcaret_hotel.domain.LangBase

abstract class BaseLangUseCase<Model: LangBase> {
    val database = HotelAppDatabase.getDatabase(HotelXcaretApp.mContext)

    abstract fun getDao() : BaseLangDao<Model>?
    abstract fun getRepository(): FirebaseDatabaseRepository<Model>?

    fun insert(list: MutableList<Model>,  response: (finish: List<Long>) -> Unit){
        getDao()?.insert(list, response)
    }

    fun getFromFirebase(listener: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Model>) {
        getRepository()?.addListener(listener)
    }

    fun getFromSingleFirebase(listener: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Model>){
        getRepository()?.addSingleListener(listener)
    }
}