package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.Award
import com.xcaret.xcaret_hotel.view.config.Language

@Dao
abstract class AwardDao: BaseDao<Award>(Award::class.simpleName ?: ""){

    @Query("""
        SELECT * FROM Award a 
        WHERE a.place_uid = :placeUID AND a.enabled = 1
        ORDER BY a.`order` ASC
    """)
    abstract fun findByPlaceUID(placeUID: String): LiveData<List<Award>>


    @Query("""
        SELECT * FROM Award a 
        WHERE a.park_uid = :parkUID AND a.enabled = 1
        ORDER BY a.`order` ASC
    """)
    abstract fun findByParkUID(parkUID: String): LiveData<List<Award>>
}