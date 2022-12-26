package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.domain.RoomType
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.Settings
import java.util.stream.Stream

@Dao
abstract class RoomTypeDao: BaseDao<RoomType>(RoomType::class.simpleName ?: ""){

    @Query(value = """
        SELECT * FROM RoomType r 
        INNER JOIN LangRoomType lr 
        ON r.uid == lr.parent_uid 
        WHERE r.enabled = 1 AND lr.lang_code = :lang AND r.hotel_uid = :hotelUID
        ORDER BY r.`order` ASC
        """)
    abstract fun all(hotelUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<RoomType>>

    @Query(value = """
        SELECT * FROM RoomType r 
        INNER JOIN LangRoomType lr 
        ON r.uid == lr.parent_uid 
        WHERE r.enabled = 1 AND lr.lang_code = :lang AND r.category_uid = :categoryUID AND r.hotel_uid = :hotelUID
        ORDER BY r.`order` ASC
        """)
    abstract fun findByCategory(categoryUID: String,
                                lang: String = Language.getLangCode(HotelXcaretApp.mContext),
                                hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): LiveData<List<RoomType>>

    @Query(value = """
        SELECT * FROM RoomType r 
        INNER JOIN LangRoomType lr 
        ON r.uid == lr.parent_uid 
        WHERE r.enabled = 1 AND lr.lang_code = :lang AND r.uid = :roomUID AND r.hotel_uid = :hotelUID
    """)
    abstract fun findById(roomUID: String,
                          lang: String = Language.getLangCode(HotelXcaretApp.mContext),
                          hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): LiveData<RoomType?>

    @Query(value = """
        SELECT * FROM RoomType r
        INNER JOIN RoomLocation rl 
        ON r.uid = rl.room_uid
        INNER JOIN LangRoomType lp 
        ON r.uid = lp.parent_uid
        WHERE rl.place_uid = :placeUID AND lp.lang_code = :lang AND r.enabled = 1 AND r.hotel_uid = :hotelUID
    """)
    abstract fun findByLocation(placeUID: String,
                                lang: String = Language.getLangCode(HotelXcaretApp.mContext),
                                hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): LiveData<List<RoomType>>

    @Query("""
        SELECT * FROM RoomType r 
        INNER JOIN LangRoomType lr 
        ON r.uid == lr.parent_uid 
        WHERE r.enabled = 1 AND lr.lang_code = :lang AND UPPER(r.code_synxis) IN (:codes) AND r.hotel_uid = :hotelUID
    """)
    abstract fun findByListCodes(codes: List<String>,
                                 lang: String = Language.getLangCode(HotelXcaretApp.mContext),
                                 hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): List<RoomType>
}