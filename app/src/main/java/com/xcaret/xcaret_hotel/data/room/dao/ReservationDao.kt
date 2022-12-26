package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.domain.Reservation

@Dao
abstract class ReservationDao: BaseDao<Reservation>(tableName = Reservation::class.simpleName ?: "") {

    @Query("""
        SELECT * FROM Reservation r
        INNER JOIN Hotel h ON
        r.hotelCode = h.id_synxis order by saleDate desc
    """)
    abstract fun allReservations(): LiveData<List<Reservation>>

    @Query("""
        SELECT * FROM Reservation r
        INNER JOIN Hotel h ON
        r.hotelCode = h.id_synxis order by saleDate desc LIMIT 20
    """)
    abstract fun allReservationsTop20(): LiveData<List<Reservation>>

    @Query("""
        SELECT * FROM Reservation r
        INNER JOIN Hotel h ON
        r.hotelCode = h.id_synxis order by saleDate desc LIMIT 20
    """)
    abstract fun allReservationsTop20NoLive(): List<Reservation>



    @Query("""
        SELECT COUNT (*) FROM Reservation r
        INNER JOIN Hotel h ON
        r.hotelCode = h.id_synxis """)
    abstract suspend fun getCountReservations(): Int

    @Query("""
        SELECT * FROM Reservation r
        INNER JOIN Hotel h ON
        r.hotelCode = h.id_synxis where r.idReservationNumber between :from and :to order by saleDate desc""")
    abstract fun getReservationsInRange(from:Int? ,to:Int?): List<Reservation>?
}