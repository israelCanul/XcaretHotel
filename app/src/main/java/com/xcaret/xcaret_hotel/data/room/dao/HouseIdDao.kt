package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.domain.HouseId

@Dao
abstract class HouseIdDao: BaseDao<HouseId>(HouseId::class.simpleName ?: "") {

    @Query("""
        SELECT * FROM HouseId
    """)
    abstract fun all(): List<HouseId>
}