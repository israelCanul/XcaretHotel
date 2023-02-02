package com.xcaret.xcaret_hotel.photopass.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xcaret.xcaret_hotel.photopass.domain.DefaultPhotoLangLabel


@Dao
abstract class DefaultPhotoLangLabelDao: BasePhotoDao<DefaultPhotoLangLabel>() {



    /*
    @Transaction
    override fun insertAll(list: MutableList<*>) = list.forEach {p ->
        val item = p as DefaultLang
            insert(item)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: DefaultLang)
     */

    @Query("""SELECT * FROM DefaultPhotoLangLabel """)
    abstract fun getDefaultLang(): LiveData<List<DefaultPhotoLangLabel>>

    @Query("""SELECT * FROM DefaultPhotoLangLabel dl""")
    abstract fun getAllDefaultLang(): LiveData<List<DefaultPhotoLangLabel>>

    @Query("""SELECT * FROM DefaultPhotoLangLabel dl 
        WHERE dl.lbl_key in (:label)""")
    abstract fun getDefaultLangByLabelLive(label: List<String>): LiveData<List<DefaultPhotoLangLabel>>

    @Query("""SELECT * FROM DefaultPhotoLangLabel dl
        WHERE dl.lbl_key in (:label)""")
    abstract fun getDefaultLangByLabelList(label: List<String>): List<DefaultPhotoLangLabel>

    @Query("""SELECT * FROM DefaultPhotoLangLabel dl
        WHERE dl.lbl_key in (:key)""")
    abstract fun getDefaultLangByKey(key: String): DefaultPhotoLangLabel?

    @Query("""SELECT * FROM DefaultPhotoLangLabel dl
        WHERE dl.lbl_key = :label""")
    abstract fun getDefaultLangByLabelList(label: String): DefaultPhotoLangLabel
}