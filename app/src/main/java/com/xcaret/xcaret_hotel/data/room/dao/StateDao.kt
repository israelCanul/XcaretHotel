package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.amazonaws.regions.ServiceAbbreviations
import com.xcaret.xcaret_hotel.domain.State

@Dao
abstract class StateDao: BaseDao<State>(State::class.simpleName ?: ""){

    @Query("SELECT * FROM State s WHERE s.countryId = :countryId")
    abstract fun allByCountryCode(countryId: Int): List<State>

    @Query("SELECT * FROM State s WHERE s.abbreviation = :abbreviation")
    abstract fun findByAbbreviation(abbreviation: String): LiveData<State?>

    @Query("SELECT * FROM State s WHERE s.abbreviation = :abbreviation")
    abstract fun findByAbbreviationOutLiveData(abbreviation: String): State?
}