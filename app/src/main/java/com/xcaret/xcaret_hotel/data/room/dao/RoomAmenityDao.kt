package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.RoomAmenity
import com.xcaret.xcaret_hotel.view.config.Language

@Dao
abstract class RoomAmenityDao: BaseDao<RoomAmenity>(RoomAmenity::class.simpleName ?: ""){

    @Query(value = """
        SELECT * FROM RoomAmenity r 
        INNER JOIN LangRoomAmenity l 
        ON r.uid == l.parent_uid 
        WHERE r.room_type_uid = :roomUID AND l.lang_code = :lang and r.enabled = 1
        ORDER BY r.`order` ASC
    """)
    abstract fun allByRoomUID(roomUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<RoomAmenity>>

    @Query(value = """
        SELECT * FROM RoomAmenity r 
        INNER JOIN LangRoomAmenity l 
        ON r.uid == l.parent_uid 
        WHERE r.room_type_uid IN (:ids) AND l.lang_code = :lang and r.enabled = 1
        ORDER BY r.`order` ASC
    """)
    abstract fun allByListIds(ids: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<RoomAmenity>

    @Query(value = """
        SELECT * FROM RoomAmenity r 
        INNER JOIN LangRoomAmenity l 
        ON r.uid == l.parent_uid 
        WHERE r.category_uid = :categoryUID AND l.lang_code = :lang and r.enabled = 1
        ORDER BY r.`order` ASC
    """)
    abstract fun allByCategory(categoryUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<RoomAmenity>
}