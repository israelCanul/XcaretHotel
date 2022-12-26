package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.FilterMap
import com.xcaret.xcaret_hotel.view.config.Language

@Dao
abstract class FilterMapDao: BaseDao<FilterMap>(FilterMap::class.simpleName ?: ""){

    @Query("""
        SELECT * FROM FilterMap f 
        INNER JOIN LangFilterMap l 
        ON f.uid = l.parent_uid 
        WHERE f.hotel_uid = :hotelUID AND l.lang_code = :lang AND f.enabled = 1 
        ORDER BY f.`order` ASC
    """)
    abstract fun findByHotel(hotelUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<FilterMap>>

}