package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Language

@Dao
abstract class DestinationDao : BaseDao<Destination>(Destination::class.simpleName ?: ""){

    @Query(""" SELECT * FROM Destination d 
        INNER JOIN LangDestination ld ON ld.parent_uid = d.uid
        WHERE ld.lang_code = :lang AND  d.status == 1 
    """)
    abstract fun findDesinations(lang: String):LiveData<List<Destination>>


}