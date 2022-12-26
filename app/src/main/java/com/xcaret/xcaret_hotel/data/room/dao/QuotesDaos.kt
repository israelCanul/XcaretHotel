package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.sqlite.db.SimpleSQLiteQuery
import com.xcaret.xcaret_hotel.domain.DateQuotes
import com.xcaret.xcaret_hotel.domain.SuiteQuotes
import com.xcaret.xcaret_hotel.domain.SuiteRatePlans

@Dao
abstract class DateQuotesDao: BaseDao<DateQuotes>(DateQuotes::class.simpleName ?: ""){
    @Query("""
        DELETE FROM DateQuotes WHERE hotelId = :id
    """)
    abstract fun removeByHotelId(id: Int): Int

    @Query("""
        SELECT * FROM DateQuotes dq WHERE dq.hotelId = :hotelId
    """)
    abstract fun allByHotelId(hotelId: Int?): LiveData<DateQuotes?>

    @Query("""
        SELECT * FROM DateQuotes
    """)
    abstract fun all(): List<DateQuotes?>
}

@Dao
abstract class SuiteQuotesDao: BaseDao<SuiteQuotes>(SuiteQuotes::class.simpleName ?: "") {

    @Query("SELECT * FROM SuiteQuotes sq WHERE sq.hotel_code = :hotelCode ORDER BY sq.number ASC")
    abstract fun allByHotelId(hotelCode: Int): LiveData<List<SuiteQuotes>>

    @Query("SELECT * FROM SuiteQuotes sq WHERE sq.hotel_code = :hotelCode AND sq.number < 1000 ORDER BY sq.number ASC")
    abstract fun allByHotelIdExceptAdd(hotelCode: Int): LiveData<List<SuiteQuotes>>

    @Query("SELECT * FROM SuiteQuotes sq WHERE sq.hotel_code = :hotelCode AND sq.is_selected = 1 ORDER BY sq.number ASC")
    abstract fun findSelectedByHotelId(hotelCode: Int): LiveData<SuiteQuotes?>

    @Query("DELETE FROM SuiteQuotes WHERE hotel_code = :hotelCode")
    abstract fun deleteByHotel(hotelCode: Int?): Int

}

@Dao
abstract class SuiteRatePlansDao: BaseDao<SuiteRatePlans>(SuiteRatePlans::class.simpleName ?: ""){

    @Query("""
        DELETE FROM SuiteRatePlans WHERE room_id = :id AND rate_hotel_code = :hotelId
    """)
    abstract fun removeByRoomId(id: Long, hotelId: Int): Int

    @Query("""
        DELETE FROM SuiteRatePlans WHERE rate_hotel_code = :hotelId
    """)
    abstract fun removeByHotel(hotelId: Int): Int

    @Query("""
        SELECT * FROM SuiteRatePlans sr WHERE sr.room_id = :id AND sr.rate_hotel_code = :hotelId ORDER BY sr.amount ASC
    """)
    abstract fun findByRoomId(id: Long, hotelId: Int): LiveData<List<SuiteRatePlans>>

    @Query("""
        SELECT * FROM SuiteRatePlans sr WHERE UPPER(sr.rate_room_code) = UPPER(:code) AND sr.rate_hotel_code = :hotelId ORDER BY sr.amount ASC
    """)
    abstract fun findByRoomCode(code: String, hotelId: Int): LiveData<List<SuiteRatePlans>>

    @Query("""
        SELECT * FROM SuiteRatePlans sr 
        WHERE UPPER(sr.rate_room_code) = UPPER(:roomCode) AND sr.rate_hotel_code = :hotelId 
        AND sr.room_id = :roomId AND UPPER(sr.rate_place_code) = UPPER(:rateCode)
        ORDER BY sr.amount ASC LIMIT 1
    """)
    abstract fun findByRoomIdRoomCodeAndHotel(roomId: Long, roomCode: String, hotelId: Int, rateCode: String): SuiteRatePlans?
}