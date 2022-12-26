package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.domain.Country

@Dao
abstract class CountryDao: BaseDao<Country>(Country::class.simpleName ?: ""){

    @Query("SELECT * FROM Country c")
    abstract fun all(): List<Country>

    @Query("SELECT * FROM Country c WHERE c.iso2 = :iso2 OR c.iso = :iso2")
    abstract fun findByIso2(iso2: String): LiveData<Country?>

    @Query("SELECT * FROM Country c WHERE c.iso2 = :iso2 OR c.iso = :iso2")
    abstract fun findByIso2OutLiveData(iso2: String): Country?
}