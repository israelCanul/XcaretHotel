package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.domain.Currency

@Dao
abstract class CurrencyDao: BaseDao<Currency>(Currency::class.simpleName ?: "") {

    @Query("""
        SELECT * FROM Currency c WHERE c.enabled = 1
    """)
    abstract fun all(): LiveData<List<Currency>>

    @Query("""
        SELECT * FROM Currency c WHERE c.iso = :iso
    """)
    abstract fun findByIso(iso:String): LiveData<Currency>
}