package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.Settings

@Dao
abstract class PlaceDao: BaseDao<Place>(Place::class.simpleName ?: ""){
    @Query(value = """
        SELECT * FROM Place p 
        INNER JOIN LangPlace l
        ON p.uid = l.parent_uid
        WHERE p.category_uid = :categoryUID AND l.lang_code = :lang AND p.enabled = 1
        ORDER BY p.`order` ASC
    """)
    abstract fun findHotelsPlace(categoryUID: String,
                                lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>>

    @Query(value = """
        SELECT * FROM Place p
        WHERE p.category_uid = :categoryUID AND p.hotel_uid = :hotelUID
        ORDER BY p.`order` ASC
    """)
    abstract fun forceFindByCategory(categoryUID: String,
                                     hotelUID: String): List<Place>

    @Query(value = """
        SELECT * FROM Place p 
        INNER JOIN LangPlace l
        ON p.uid = l.parent_uid
        WHERE p.category_uid = :categoryUID AND l.lang_code = :lang AND p.enabled = 1 AND p.hotel_uid = :hotelUID
        ORDER BY p.`order` ASC
    """)
    abstract fun findByCategory(categoryUID: String,
                                lang: String = Language.getLangCode(HotelXcaretApp.mContext),
                                hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): LiveData<List<Place>>

    @Query(value = """
        SELECT * FROM Place p 
        INNER JOIN LangPlace l
        ON p.uid = l.parent_uid
        WHERE p.category_uid IN (:categoryIds) AND l.lang_code = :lang AND p.hotel_uid = :hotelUID AND p.enabled = 1 ORDER BY p.`order` ASC
    """)
    abstract fun findInCategory(categoryIds: List<String>,
                                lang: String = Language.getLangCode(HotelXcaretApp.mContext),
                                hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): LiveData<List<Place>>

    @Query(value = """
        SELECT * FROM Place p 
        INNER JOIN LangPlace l
        ON p.uid = l.parent_uid
        WHERE p.uid = :placeUID AND l.lang_code = :lang AND p.enabled = 1 AND p.hotel_uid = :hotelUID ORDER BY p.`order` ASC
    """)
    abstract fun findById(placeUID: String,
                          lang: String = Language.getLangCode(HotelXcaretApp.mContext),
                          hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): LiveData<Place?>

    @Query(value = """
        SELECT * FROM Place p 
        INNER JOIN LangPlace l
        ON p.uid = l.parent_uid
        WHERE p.uid IN (:placeIDS) AND l.lang_code = :lang  AND p.hotel_uid = :hotelUID ORDER BY p.`order` ASC
    """)
    abstract fun findWithIn(placeIDS: List<String>,
                            lang: String = Language.getLangCode(HotelXcaretApp.mContext),
                            hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): List<Place>

    @Query(value = """
        SELECT * FROM Place p 
        INNER JOIN LangPlace l
        ON p.uid = l.parent_uid
        WHERE p.category_uid = :catUID AND p.hotel_uid = :hotelUID AND l.lang_code = :lang ORDER BY p.`order` ASC
    """)
    abstract fun findByCategoryAndHotel(catUID: String, hotelUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Place?>

    @Query(value = """
        SELECT * FROM Place p 
        INNER JOIN LangPlace l
        ON p.uid = l.parent_uid
        WHERE p.hotel_uid = :hotelUID AND l.lang_code = :lang AND p.type = :type AND p.enabled = 1 ORDER BY p.`order` ASC
    """)
    abstract fun allByHotel(hotelUID: String, type: String = Constants.PLACE, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>>

    @Query(value = """
        SELECT * FROM Place p 
        INNER JOIN LangPlace l
        ON p.uid = l.parent_uid
        WHERE p.hotel_uid = :hotelUID AND l.lang_code = :lang AND p.type = :type AND p.enabled = 1
        AND (l.lp_title LIKE :query OR l.lp_description_long LIKE :query) ORDER BY p.`order` ASC
    """)
    abstract fun search(query: String, hotelUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext), type: String = Constants.PLACE): LiveData<List<Place>>

}