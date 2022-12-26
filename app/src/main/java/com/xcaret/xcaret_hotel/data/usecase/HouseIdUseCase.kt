package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.HouseIdRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.HouseId

class HouseIdUseCase: BaseUseCase<HouseId>(){
    private val dao = database.houseIdDao()
    private val repository: HouseIdRepository by lazy { HouseIdRepository() }

    override fun getDao(): BaseDao<HouseId>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<HouseId>? = repository

    fun all(): List<HouseId> = dao.all()
}