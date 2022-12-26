package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.Contact
import com.xcaret.xcaret_hotel.domain.Faq
import com.xcaret.xcaret_hotel.domain.FilterMap
import com.xcaret.xcaret_hotel.view.config.Language

@Dao
abstract class FaqDao : BaseDao<Faq>(Faq::class.simpleName ?: ""){

    @Query("""
        SELECT * FROM Faq f 
        INNER JOIN LangFaq l 
        ON f.uid = l.parent_uid 
        WHERE f.hotel_uid = :hotelUID AND l.lang_code = :lang AND f.enabled = 1 
        ORDER BY f.`order` ASC
    """)
    abstract fun findFaqsByHotel(hotelUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<FilterMap>>

    @Query("""
        SELECT * FROM Faq f 
        INNER JOIN LangFaq l 
        ON f.uid = l.parent_uid 
        WHERE  f.category_uid = :categoryUID AND f.enabled = 1 
        ORDER BY f.`order` ASC
    """)
    abstract fun findFaqs(categoryUID: String): List<Faq>


}