package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.data.room.dao.DateQuotesDao
import com.xcaret.xcaret_hotel.data.room.dao.SuiteQuotesDao
import com.xcaret.xcaret_hotel.domain.DateQuotes
import com.xcaret.xcaret_hotel.domain.SuiteQuotes

class DateQuotesUseCase: BaseUseCase<DateQuotes>(){
    private val dao: DateQuotesDao by lazy { database.dateQuotesDao() }
    override fun getDao(): BaseDao<DateQuotes>? = dao

    override fun getRepository(): FirebaseDatabaseRepository<DateQuotes>? = null

    fun allByHotelId(hotelId: Int?): LiveData<DateQuotes?> = dao.allByHotelId(hotelId)
    fun all(): List<DateQuotes?> = dao.all()
    fun save(dateQuotes: DateQuotes) = dao.insert(dateQuotes)
    fun delete(dateQuotes: DateQuotes) = dao.remove(dateQuotes)
    fun deleteByHotel(hotelId:Int) = dao.removeByHotelId(hotelId)
    fun update(dateQuotes: DateQuotes) = dao.update(dateQuotes)
    fun truncate() = dao.truncate()
}

class SuiteQuotesUseCase: BaseUseCase<SuiteQuotes>() {
    private val dao: SuiteQuotesDao by lazy { database.suiteQuotesDao() }

    override fun getDao(): BaseDao<SuiteQuotes> = dao

    override fun getRepository(): FirebaseDatabaseRepository<SuiteQuotes>? = null

    fun findSelectedByHotelId(hotelId: Int) = dao.findSelectedByHotelId(hotelId)
    fun allByHotelId(hotelCode: Int): LiveData<List<SuiteQuotes>> = dao.allByHotelId(hotelCode)
    fun allByHotelIdExceptAdd(hotelCode: Int): LiveData<List<SuiteQuotes>> = dao.allByHotelIdExceptAdd(hotelCode)
    fun save(suiteQuote: SuiteQuotes): Long = dao.insert(suiteQuote)
    fun saveList(suiteQuote: List<SuiteQuotes>) = dao.insertAll(suiteQuote)
    fun delete(suiteQuote: SuiteQuotes) = dao.remove(suiteQuote)
    fun deleteByHotel(hotelCode: Int?) = dao.deleteByHotel(hotelCode)
    fun truncate() = dao.truncate()
    fun update(suiteQuote: SuiteQuotes) = dao.update(suiteQuote)
}