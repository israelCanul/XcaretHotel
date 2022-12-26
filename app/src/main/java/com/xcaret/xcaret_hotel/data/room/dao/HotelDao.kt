package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.Hotel
import com.xcaret.xcaret_hotel.view.config.Language

@Dao
abstract class HotelDao: BaseDao<Hotel>(Hotel::class.simpleName ?: ""){

    @Query(value = """
        SELECT * FROM Hotel h 
        INNER JOIN LangHotel l 
        ON h.uid = l.parent_uid 
        WHERE l.lang_code = :lang AND h.enabled = 1
    """)
    abstract fun all(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Hotel>>

    @Query(value = """
        SELECT * FROM Hotel h 

    """)
    abstract fun allNoFilter(): LiveData<List<Hotel>>


    @Query(value = """
        SELECT * FROM Hotel h 
        INNER JOIN LangHotel l 
        ON h.uid = l.parent_uid 
        WHERE l.lang_code = :lang AND h.code = :code AND h.enabled = 1
    """)
    abstract fun findByCode(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Hotel?>
}