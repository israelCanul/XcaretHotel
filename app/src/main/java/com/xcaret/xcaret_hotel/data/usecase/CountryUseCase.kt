package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.LangCountryRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Country

class CountryUseCase: BaseUseCase<Country>(){
    private val dao = database.countryDao()
    private val repository: LangCountryRepository by lazy { LangCountryRepository() }

    override fun getDao(): BaseDao<Country> = dao
    override fun getRepository(): FirebaseDatabaseRepository<Country> = repository

    fun all() = dao.all()
    fun findByIso2(iso2: String): LiveData<Country?> = dao.findByIso2(iso2)
    fun findByIso2OutLiveData(iso2: String): Country? = dao.findByIso2OutLiveData(iso2)
}