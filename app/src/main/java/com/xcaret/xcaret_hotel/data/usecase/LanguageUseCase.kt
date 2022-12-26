package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.LanguageRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Language

class LanguageUseCase: BaseUseCase<Language>() {

    private val dao = database.languageDao()
    private val repository = LanguageRepository()

    override fun getDao(): BaseDao<Language>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<Language>? = repository

    suspend fun getLanguage(code: String): Language? = dao.getLanguage(code)
    fun all(): LiveData<List<Language>> = dao.all()
}