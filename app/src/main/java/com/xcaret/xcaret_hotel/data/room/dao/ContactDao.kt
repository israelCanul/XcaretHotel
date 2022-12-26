package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.Contact
import com.xcaret.xcaret_hotel.view.config.Language

@Dao
abstract class ContactDao: BaseDao<Contact>(Contact::class.simpleName ?: ""){

    @Query(value = """
        SELECT * FROM Contact c 
        WHERE c.hotel_uid = :hotelUID AND c.type = :type AND c.enabled = 1 LIMIT 1
    """)
    abstract fun findByHotelAndType(hotelUID: String, type: String): LiveData<Contact?>

    @Query(value = """
        SELECT * FROM Contact c 
        WHERE c.code = :code AND c.enabled = 1
    """)
    abstract fun findByCode(code: String): LiveData<Contact?>

    @Query(value = """
        SELECT * FROM Contact c
        INNER JOIN LangContact l
        ON c.uid = l.parent_uid 
        WHERE c.category_uid = :categoryUID AND c.hotel_uid = :hotelUID AND l.lang_code = :lang AND c.enabled = 1 
        ORDER BY c.`order` ASC
    """)
    abstract fun findByCategoryAndHotel(categoryUID: String, hotelUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Contact>>

    @Query(value = """
        SELECT * FROM Contact c
        INNER JOIN LangContact l
        ON c.uid = l.parent_uid 
        WHERE c.category_uid = :categoryUID AND l.lang_code = :lang AND c.enabled = 1 
        ORDER BY c.`order` ASC
    """)
    abstract fun findByCategoryOutHotel(categoryUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Contact>>

}