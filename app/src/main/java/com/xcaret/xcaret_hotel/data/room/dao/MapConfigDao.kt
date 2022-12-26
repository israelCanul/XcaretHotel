package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.domain.MapConfig
import com.xcaret.xcaret_hotel.view.config.Constants

@Dao
abstract class MapConfigDao: BaseDao<MapConfig>(MapConfig::class.simpleName ?: ""){

    @Query("""
        SELECT * FROM MapConfig m 
        WHERE m.hotel_uid = :hotelUID AND m.platform = :platform
    """)
    abstract fun findByHotel(hotelUID: String, platform: String = Constants.PLATFORM): LiveData<MapConfig?>

    @Query("""
        SELECT * FROM MapConfig m 
        WHERE m.platform = :platform LIMIT 1
    """)
    abstract fun first(platform: String = Constants.PLATFORM): LiveData<MapConfig?>

}