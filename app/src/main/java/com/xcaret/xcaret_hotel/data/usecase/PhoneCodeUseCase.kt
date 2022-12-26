package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.PhoneCodeRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.PhoneCode

class PhoneCodeUseCase: BaseUseCase<PhoneCode>() {
    private val dao = database.phoneCodeDao()
    private val repository = PhoneCodeRepository()

    override fun getDao(): BaseDao<PhoneCode> = dao
    override fun getRepository(): FirebaseDatabaseRepository<PhoneCode> = repository

    fun findByCountry(iso: String): PhoneCode? = dao.findByCountry(iso)
}