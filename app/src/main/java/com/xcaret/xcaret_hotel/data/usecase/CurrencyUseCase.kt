package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.data.repository.CurrencyRepository
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Currency

class CurrencyUseCase: BaseUseCase<Currency>() {
    private val dao = database.currencyDao()
    private val repository: CurrencyRepository by lazy { CurrencyRepository() }

    override fun getDao(): BaseDao<Currency> = dao
    override fun getRepository(): FirebaseDatabaseRepository<Currency> = repository

    fun all() = dao.all()
    fun finByIso(iso:String) = dao.findByIso(iso)
}