package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.domain.Language

@Dao
abstract class LanguageDao: BaseDao<Language>(Language::class.simpleName ?: ""){

    @Query(value = "SELECT * FROM Language l WHERE l.code = :code")
    abstract suspend fun getLanguage(code: String): Language?

    @Query(value = """
        SELECT * FROM Language l
        WHERE l.enabled = 1
        ORDER BY l.name ASC
    """)
    abstract fun all(): LiveData<List<Language>>
}