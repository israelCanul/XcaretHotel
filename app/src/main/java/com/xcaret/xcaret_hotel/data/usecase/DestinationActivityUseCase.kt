package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.data.repository.CategoryRepository
import com.xcaret.xcaret_hotel.data.repository.DestinationActivityRepository
import com.xcaret.xcaret_hotel.data.repository.FaqRepository
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.DestinationActivity
import com.xcaret.xcaret_hotel.domain.Faq

class DestinationActivityUseCase : BaseUseCase<DestinationActivity>(){

    private val dao = database.destinationActivityDao()
    private val repository by lazy { DestinationActivityRepository() }

    override fun getDao(): BaseDao<DestinationActivity>? = dao

    override fun getRepository(): FirebaseDatabaseRepository<DestinationActivity>? = repository

    fun findActivitiesByDestination(destination:String,langCode:String) =dao.findActivitiesByDestination(destination,langCode)

}