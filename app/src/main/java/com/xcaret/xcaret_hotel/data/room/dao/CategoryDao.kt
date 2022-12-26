package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.RoomType
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.Settings

@Dao
abstract class CategoryDao: BaseDao<Category>(Category::class.simpleName ?: ""){

    @Query(value = """
        SELECT * FROM Category c 
        INNER JOIN LangCategory l 
        ON c.uid = l.parent_uid 
        WHERE c.hotel_uid = :hotelUID AND l.lang_code = :lang AND c.show_in_home = 1
        ORDER BY c.priority ASC
    """)
    abstract fun allByHotel(hotelUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>>

    @Query(value = """
        SELECT * FROM Category c 
        INNER JOIN LangCategory l 
        ON c.uid = l.parent_uid 
        WHERE c.filter_grouper = :categoryUID AND l.lang_code = :lang AND c.enabled = 1
        ORDER BY c.priority ASC
    """)
    abstract fun allByFilterGroup(categoryUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>>

    @Query(value = """
        SELECT * FROM Category c 
        INNER JOIN LangCategory l 
        ON c.uid = l.parent_uid 
        WHERE c.filter_grouper IN (:categoryUID) AND l.lang_code = :lang
        ORDER BY c.priority ASC
    """)
    abstract fun allByListFilterGroup(categoryUID: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>>


    @Query(value = """
        SELECT * FROM Category c 
        INNER JOIN LangCategory l 
        ON c.uid = l.parent_uid 
        WHERE c.uid = :uid AND l.lang_code = :lang
    """)
    abstract fun findById(uid: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?>

    @Query(value = """
        SELECT * FROM Category c 
        INNER JOIN LangCategory l 
        ON c.uid = l.parent_uid 
        WHERE c.code = :code AND l.lang_code = :lang AND c.hotel_uid = :hotelUID
        ORDER BY c.priority ASC
    """)
    abstract fun findByCode(code: String,
                            lang: String = Language.getLangCode(HotelXcaretApp.mContext),
                            hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): Category?

    @Query(value = """
        SELECT * FROM Category c 
        INNER JOIN LangCategory l 
        ON c.uid = l.parent_uid 
        WHERE c.code = :code AND l.lang_code = :lang 
        ORDER BY c.priority ASC
    """)
    abstract fun findByCodeNoHotelUID(code: String,
                            lang: String = Language.getLangCode(HotelXcaretApp.mContext)): Category?

    @Query(value = """
        SELECT * FROM Category c
        WHERE c.code = :code
        ORDER BY c.priority ASC
    """)
    abstract fun findByCodeOutHotel(code: String): Category?

    @Query(value = """
        SELECT * FROM Category c
        WHERE c.code = :code
        ORDER BY c.priority ASC
    """)
    abstract fun findCategoryByCodeLive(code: String): LiveData<Category?>

    @Query(value = """
        SELECT * FROM Category c 
        INNER JOIN LangCategory l 
        ON c.uid = l.parent_uid 
        WHERE c.code = :code AND l.lang_code = :lang AND c.hotel_uid = :hotelUID
        ORDER BY c.priority ASC
    """)
    abstract fun findByCodeLive(code: String,
                                lang: String = Language.getLangCode(HotelXcaretApp.mContext),
                                hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): LiveData<Category?>

    @Query(value = """
        SELECT * FROM Category c 
        INNER JOIN LangCategory l 
        ON c.uid = l.parent_uid 
        WHERE c.code = :code AND l.lang_code = :lang
        ORDER BY c.priority ASC
    """)
    abstract fun findByCodeLiveOutHotel(code: String,
                                lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?>

    @Query(value = """
        SELECT * FROM Category c 
        INNER JOIN LangCategory l 
        ON c.uid = l.parent_uid 
        WHERE c.code = :code AND l.lang_code = :lang
        ORDER BY c.priority ASC
    """)
    abstract fun listByCodeLiveOutHotel(code: String,
                                        lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>>

    @Query(value = """
        SELECT * FROM Category c 
        WHERE c.code = :code AND c.hotel_uid = :hotelUID
        ORDER BY c.priority ASC
    """)
    abstract fun findByCodeLiveWithoutLang(code: String, hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): LiveData<Category?>

    @Query(value = """
        SELECT * FROM Category c 
        WHERE c.code = :code 
        ORDER BY c.priority ASC
    """)
    abstract fun findByCodeLiveWithoutHotel(code: String): LiveData<Category?>

    @Query(value = """
        SELECT * FROM Category c 
        INNER JOIN LangCategory l 
        ON c.uid = l.parent_uid 
        WHERE c.uid IN (:ids) AND l.lang_code = :lang
        ORDER BY c.priority ASC
    """)
    abstract fun findByListIds(ids: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<Category>

    @Query(value = """
        SELECT c.uid FROM Category c 
        INNER JOIN LangCategory l 
        ON c.uid = l.parent_uid 
        WHERE c.code IN (:codes) AND l.lang_code = :lang AND c.hotel_uid = :hotelUID
        ORDER BY c.priority ASC
    """)
    abstract fun findByListCodes(codes: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext), hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): LiveData<List<String>>

    @Query("""
        SELECT * FROM Category c 
        INNER JOIN LangCategory l 
        ON c.uid = l.parent_uid 
        INNER JOIN RoomType r 
        ON c.uid = r.category_uid 
        WHERE r.code_synxis IN (:codes) AND l.lang_code = :lang AND c.hotel_uid = :hotelUID
        GROUP BY c.uid
    """)
    abstract fun findCategoryByRoomCodes(codes: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext), hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): List<RoomType>
}