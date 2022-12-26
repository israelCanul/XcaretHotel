package com.xcaret.xcaret_hotel.data.room.dao

import android.annotation.SuppressLint
import android.os.Build
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.xcaret.xcaret_hotel.domain.LangBase
import com.xcaret.xcaret_hotel.view.config.LogHX
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.reflect.ParameterizedType

@Dao
abstract class BaseLangDao<Model: LangBase>(private val tableName: String){

    fun insert(items: List<Model>, response: (finish: List<Long>) -> Unit){
        doAsync {
            val finish = mutableListOf<Long>()
            /*items.forEach { item->
                remove(item)
            }*/

            if(!items.isNullOrEmpty()) {
                truncate()
                finish.addAll(insertAll(items))
            }
            uiThread {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    LogHX.e("insert/update lang ${getEntityClass()}", finish.toString())
                }
                response(finish)
            }
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(item: Model?): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(item: List<Model>): List<Long>

    @RawQuery
    abstract fun findByRawQuery(query: SupportSQLiteQuery): Model?

    @RawQuery
    abstract fun removeByQuery(query: SupportSQLiteQuery): Boolean?

    @Update
    abstract fun update(item: Model?): Int

    fun find(model: Model): Model? {
        val query = "SELECT * FROM $tableName" +
                " WHERE parent_uid = ? AND lang_code = ?"
        return findByRawQuery(SimpleSQLiteQuery(query, arrayOf(model.parent_uid, model.lang_code)))
    }

    fun remove(model: Model): Boolean? {
        val query = "DELETE FROM $tableName WHERE parent_uid = ? AND lang_code = ?"
        return removeByQuery(SimpleSQLiteQuery(query, arrayOf(model.parent_uid, model.lang_code)))
    }

    fun truncate(): Boolean? {
        val queryRemove = "DELETE FROM $tableName"
        val updatePrimary = "UPDATE sqlite_sequence SET seq=0 WHERE name='$tableName'"
        removeByQuery(SimpleSQLiteQuery(queryRemove))
        return removeByQuery(SimpleSQLiteQuery(updatePrimary))
    }

    @SuppressLint("NewApi")
    private fun getEntityClass(): String?{
        return this::class.java.genericSuperclass?.typeName
    }
}