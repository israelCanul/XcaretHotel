package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.domain.WebCam

@Dao
abstract class WebCamDao: BaseDao<WebCam>(WebCam::class.simpleName ?: ""){

    @Query("""
        SELECT * FROM WebCam w
        WHERE w.enabled = 1 AND w.category_uid = :categoryUID
        ORDER BY w.`order` ASC
    """)
    abstract fun getAllWebCamsByCategory(categoryUID: String): LiveData<List<WebCam>>

    @Query("""
        SELECT * FROM WebCam w
        WHERE w.enabled = 1 AND w.code = :code
        ORDER BY w.`order` ASC
    """)
    abstract fun findByCode(code: String): LiveData<WebCam?>

    @Query("""
        SELECT * FROM WebCam w
        WHERE w.enabled = 1 
        ORDER BY w.`order` ASC
    """)
    abstract fun getWebCams(): LiveData<List<WebCam>>
}