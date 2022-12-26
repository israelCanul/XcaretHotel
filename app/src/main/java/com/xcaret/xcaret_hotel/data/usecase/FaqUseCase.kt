package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.data.repository.CategoryRepository
import com.xcaret.xcaret_hotel.data.repository.FaqRepository
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.Faq

class FaqUseCase : BaseUseCase<Faq>(){

    private val dao = database.faqDao()
    private val repository by lazy { FaqRepository() }

    override fun getDao(): BaseDao<Faq>? = dao

    override fun getRepository(): FirebaseDatabaseRepository<Faq>? = repository

    fun getFaqs(categoryUID: String) = dao.findFaqs(categoryUID)
}