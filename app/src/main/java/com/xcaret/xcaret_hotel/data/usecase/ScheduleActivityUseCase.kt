package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.ScheduleActivityRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.ScheduleActivity

class ScheduleActivityUseCase: BaseUseCase<ScheduleActivity>() {

    private val dao = database.scheduleActivityDao()
    private val repository by lazy { ScheduleActivityRepository() }

    override fun getDao(): BaseDao<ScheduleActivity> = dao
    override fun getRepository(): FirebaseDatabaseRepository<ScheduleActivity> = repository

    fun activityWithSchedules(activityUID: String) = dao.activityWithSchedules(activityUID)
}