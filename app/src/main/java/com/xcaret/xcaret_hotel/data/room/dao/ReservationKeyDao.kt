package com.xcaret.xcaret_hotel.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.sqlite.db.SupportSQLiteQuery
import com.xcaret.xcaret_hotel.domain.ReservationKeys

@Dao
abstract class ReservationKeyDao :BaseDao<ReservationKeys>(tableName = ReservationKeys::class.simpleName ?: "") {
    @Insert(onConflict = REPLACE)
    abstract suspend fun saveReservationKeys(reservationKey: ReservationKeys)

    @Query("SELECT * FROM reservationKeys ORDER BY id DESC")
    abstract suspend fun getReservationKeys(): List<ReservationKeys>

}