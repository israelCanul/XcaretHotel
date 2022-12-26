package com.xcaret.xcaret_hotel.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.domain.ParamSetting

@Dao
abstract class ParamSettingDao: BaseDao<ParamSetting>(ParamSetting::class.simpleName ?: "") {

    @Query("""
        SELECT * FROM ParamSetting ps WHERE ps.code = :code LIMIT 1
    """)
    abstract fun findParamSettingByCode(code: String): ParamSetting?

}