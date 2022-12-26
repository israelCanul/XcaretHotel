package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.domain.Booking
import com.xcaret.xcaret_hotel.domain.BookingAttemptInfo
import com.xcaret.xcaret_hotel.domain.Country

@Dao
abstract class BookingDao: BaseDao<BookingAttemptInfo>(BookingAttemptInfo::class.simpleName ?: "") {

    @Query("SELECT * FROM BookingAttemptInfo c ORDER BY primary_key DESC ")
    abstract fun findBySalesId(): BookingAttemptInfo

    @Query("DELETE FROM BookingAttemptInfo")
    abstract fun clearAllAttempts()

}
