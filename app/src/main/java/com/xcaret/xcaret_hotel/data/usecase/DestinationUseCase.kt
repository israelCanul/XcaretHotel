package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.data.repository.CategoryRepository
import com.xcaret.xcaret_hotel.data.repository.DestinationRepository
import com.xcaret.xcaret_hotel.data.repository.FaqRepository
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.Destination
import com.xcaret.xcaret_hotel.domain.Faq

class DestinationUseCase : BaseUseCase<Destination>(){

    private val dao = database.destinationDao()
    private val repository by lazy { DestinationRepository() }

    override fun getDao(): BaseDao<Destination>? = dao

    override fun getRepository(): FirebaseDatabaseRepository<Destination>? = repository

    fun findDestinations(lang: String) = dao.findDesinations(lang)

}