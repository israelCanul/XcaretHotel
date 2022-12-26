package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.Activity
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.Settings

@Dao
abstract class ActivityDao: BaseDao<Activity>(Activity::class.simpleName ?: "") {
    @Query("""
        SELECT * FROM Activity a 
        INNER JOIN LangActivity l 
        ON a.uid = l.parent_uid 
        WHERE a.categoryUID = :catUID AND a.hotelUID = :hotelUID AND l.lang_code = :lang
    """)
    abstract fun allByCategory(catUID: String,
                               lang: String? = Language.getLangCode(HotelXcaretApp.mContext),
                               hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): LiveData<List<Activity>>

    @Query("""
        SELECT * FROM Activity a 
        INNER JOIN LangActivity l 
        ON a.uid = l.parent_uid 
        WHERE a.uid = :uid AND l.lang_code = :lang
    """)
    abstract fun findByUID(uid: String, lang: String? = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Activity?>

}