package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.ParkTour
import com.xcaret.xcaret_hotel.view.config.Language

@Dao
abstract class ParkTourDao: BaseDao<ParkTour>(ParkTour::class.simpleName ?: ""){

    @Query(value = """
        SELECT * FROM ParkTour p
        INNER JOIN LangParkTour l
        ON p.uid = l.parent_uid
        WHERE p.categoryUID = :categoryUID AND l.lang_code = :lang AND p.enabled = 1 
        ORDER BY p.`order` ASC
    """)
    abstract fun getByCategory(categoryUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<ParkTour>>

    @Query(value = """
        SELECT * FROM ParkTour p
        INNER JOIN LangParkTour l
        ON p.uid = l.parent_uid
        WHERE p.uid = :parkUID AND l.lang_code = :lang
    """)
    abstract fun findById(parkUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<ParkTour>

    @Query(value = """
        SELECT * FROM ParkTour p
        INNER JOIN LangParkTour l
        ON p.uid = l.parent_uid
        WHERE p.classUID = :classUID AND l.lang_code = :lang AND p.enabled = 1 
        ORDER BY p.`order` ASC
    """)
    abstract fun getByClass(classUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<ParkTour>>

}