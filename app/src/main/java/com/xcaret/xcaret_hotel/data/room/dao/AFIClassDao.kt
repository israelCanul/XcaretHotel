package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Language

@Dao
abstract class AFIClassDao : BaseDao<AfiClass>(AfiClass::class.simpleName ?: ""){

    @Query("""SELECT * FROM AFICLASS A 
        INNER JOIN LANGAFICLASS  L ON L.parent_uid = A.uid   
        WHERE A.enabled = 1 ORDER BY A.`order` ASC""")
    abstract fun findAllAFIClasses(): LiveData<List<AfiClass>?>

    @Query("""SELECT * FROM AFICLASS A 
        INNER JOIN LANGAFICLASS  L ON L.parent_uid = A.uid   
        WHERE A.uid = :UID AND l.lang_code =  :langCode AND A.enabled = 1 """)
    abstract fun findAFIClassesByUID(UID:String,langCode :String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<AfiClass>
}