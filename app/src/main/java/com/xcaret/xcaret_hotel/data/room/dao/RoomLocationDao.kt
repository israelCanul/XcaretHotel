package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.domain.RoomLocation
import com.xcaret.xcaret_hotel.view.config.Language

@Dao
abstract class RoomLocationDao: BaseDao<RoomLocation>(RoomLocation::class.simpleName ?: ""){
    @Query(value = """
        SELECT * FROM Place p
        INNER JOIN RoomLocation rl 
        ON p.uid = rl.place_uid
        INNER JOIN LangPlace lp 
        ON p.uid = lp.parent_uid
        WHERE rl.room_uid = :roomUID AND lp.lang_code = :lang ORDER BY rl.room_location_order ASC
    """)
    abstract fun getLocations(roomUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>>
}