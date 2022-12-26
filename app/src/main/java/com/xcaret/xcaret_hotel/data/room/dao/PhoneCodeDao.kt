package com.xcaret.xcaret_hotel.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.domain.PhoneCode

@Dao
abstract class PhoneCodeDao: BaseDao<PhoneCode>(PhoneCode::class.simpleName ?: ""){

    @Query("""
        SELECT * FROM PhoneCode p WHERE p.iso = :iso LIMIT 1
    """)
    abstract fun findByCountry(iso: String): PhoneCode?
}