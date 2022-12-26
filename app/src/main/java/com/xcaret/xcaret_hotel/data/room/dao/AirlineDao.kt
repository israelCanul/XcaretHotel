package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.domain.Airline
import com.xcaret.xcaret_hotel.domain.AirlineTerminal

@Dao
abstract class AirlineDao: BaseDao<Airline>(Airline::class.simpleName ?: ""){

    @Query("""
        SELECT * FROM Airline a WHERE a.enabled = 1 ORDER BY a.`order` ASC
    """)
    abstract fun all(): LiveData<List<Airline>>
}

@Dao
abstract class AirlineTerminalDao: BaseDao<AirlineTerminal>(AirlineTerminal::class.simpleName ?: ""){
    @Query("""
        SELECT * FROM AirlineTerminal a WHERE a.enabled = 1 ORDER BY a.`order` ASC
    """)
    abstract fun all(): LiveData<List<AirlineTerminal>>
}