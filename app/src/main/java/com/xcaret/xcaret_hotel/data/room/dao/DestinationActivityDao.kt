package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Language

@Dao
abstract class DestinationActivityDao : BaseDao<DestinationActivity>(DestinationActivity::class.simpleName ?: ""){

    @Query(""" SELECT * FROM DestinationActivity d 
        INNER JOIN LangDestinationActivity lda ON lda.parent_uid = d.uid
        WHERE d.destinationUID = :destinationUID AND lda.lang_code = :lang AND  d.enabled == 1 
    """)
    abstract fun findActivitiesByDestination(destinationUID:String,lang: String):LiveData<List<DestinationActivity>>

}