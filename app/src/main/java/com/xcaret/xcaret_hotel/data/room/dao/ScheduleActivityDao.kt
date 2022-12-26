package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.domain.ScheduleActivity

@Dao
abstract class ScheduleActivityDao: BaseDao<ScheduleActivity>(ScheduleActivity::class.simpleName ?: "") {

    @Query("""
        SELECT * FROM ScheduleActivity s WHERE s.activityUID = :uid 
    """)
    abstract fun activityWithSchedules(uid: String): LiveData<List<ScheduleActivity>>
}