package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.data.repository.ActivityRepository
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Activity

class ActivityUseCase: BaseUseCase<Activity>() {

    private val dao = database.activityDao()
    private val repository by lazy { ActivityRepository() }

    override fun getDao(): BaseDao<Activity> = dao
    override fun getRepository(): FirebaseDatabaseRepository<Activity> = repository

    fun allByCategory(catUID: String) = dao.allByCategory(catUID)
    fun findByUID(uid: String) = dao.findByUID(uid)
}