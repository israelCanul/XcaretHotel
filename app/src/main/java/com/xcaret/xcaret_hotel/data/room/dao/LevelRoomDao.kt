package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.domain.ExtraInfoBuilding
import com.xcaret.xcaret_hotel.domain.LevelRoom

@Dao
abstract class LevelRoomDao: BaseDao<LevelRoom>(LevelRoom::class.simpleName ?: ""){

    @Query("""
        SELECT house_id, COUNT(*) as num_rooms, MAX(level) as levels FROM LevelRoom l WHERE l.building_uid IN (:buildingUIDS)  GROUP BY house_id
    """)
    abstract fun findExtraInfo(buildingUIDS: List<String>): List<ExtraInfoBuilding>
}