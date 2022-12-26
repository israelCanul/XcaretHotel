package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.ContactRepository
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Contact
import com.xcaret.xcaret_hotel.view.config.Language

class ContactUseCase: BaseUseCase<Contact>(){
    private val dao = database.contactDao()
    private val repository: ContactRepository by lazy { ContactRepository() }

    override fun getDao(): BaseDao<Contact>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<Contact>? = repository

    fun findByHotelAndType(hotelUID: String, type: String) = dao.findByHotelAndType(hotelUID, type)
    fun findByCode(code: String): LiveData<Contact?> = dao.findByCode(code)
    fun findByCategoryAndHotel(categoryUID: String, hotelUID: String, lang: String = Language.getLangCode(
        HotelXcaretApp.mContext)): LiveData<List<Contact>> = dao.findByCategoryAndHotel(categoryUID, hotelUID, lang)
    fun findByCategoryOutHotel(categoryUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = dao.findByCategoryOutHotel(categoryUID, lang)
}