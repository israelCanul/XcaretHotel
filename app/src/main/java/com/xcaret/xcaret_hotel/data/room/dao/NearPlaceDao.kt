package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.NearPlace
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.view.config.Language

@Dao
abstract class NearPlaceDao: BaseDao<NearPlace>(NearPlace::class.simpleName ?: ""){
    @Query(value = """
        SELECT * FROM Place p 
        INNER JOIN NearPlace np 
        ON p.uid = np.n_near_place_uid 
        INNER JOIN LangPlace l 
        ON np.n_near_place_uid = l.parent_uid 
        WHERE np.n_place_uid = :placeUID AND l.lang_code = :lang AND p.enabled = 1 ORDER BY np.n_order ASC
    """)
    abstract fun findNearPlace(placeUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>>
}