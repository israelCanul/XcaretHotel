package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.LevelRoomRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.ExtraInfoBuilding
import com.xcaret.xcaret_hotel.domain.LevelRoom

class LevelRoomUseCase: BaseUseCase<LevelRoom>(){
    private val dao = database.levelRoomDao()
    private val repository: LevelRoomRepository by lazy { LevelRoomRepository() }

    override fun getDao(): BaseDao<LevelRoom>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LevelRoom>? = repository

    fun findExtraInfo(buildingUID: List<String>): List<ExtraInfoBuilding> = dao.findExtraInfo(buildingUID)
}