package com.xcaret.xcaret_hotel.data.room.dao

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.xcaret.xcaret_hotel.view.config.LogHX
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.reflect.ParameterizedType

@Dao
abstract class BaseDao<Model>(private val tableName: String = ""){
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(item: Model?): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(item: List<Model>): List<Long>

    @Update
    abstract fun update(item: Model?): Int

    @Delete
    abstract fun remove(item: Model?)

    open fun insert(item: List<Model>?, result: (ids: List<Long>) -> Unit){
        doAsync {
            val ids = mutableListOf<Long>()

            if(!item.isNullOrEmpty()) {
                truncate()
                item.let { ids.addAll(insertAll(it)) }
            }
            uiThread {
                LogHX.i("insert $tableName", ids.toString())
                result(ids)
            }
        }
    }

    fun removeAll(): Boolean? {
        val queryRemove = "DELETE FROM $tableName"
        return removeByQuery(SimpleSQLiteQuery(queryRemove))
    }

    fun truncate(): Boolean? {
        val queryRemove = "DELETE FROM $tableName"
        val updatePrimary = "UPDATE sqlite_sequence SET seq=0 WHERE name='$tableName'"
        removeByQuery(SimpleSQLiteQuery(queryRemove))
        return removeByQuery(SimpleSQLiteQuery(updatePrimary))
    }

    @RawQuery
    abstract fun removeByQuery(query: SupportSQLiteQuery): Boolean?

    @SuppressLint("NewApi")
    private fun getEntityClass(): String?{
        return this::class.java.genericSuperclass?.typeName
    }
}