package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.data.entity.BookingActivityEntity
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.BookingAttemptInfo

class BookingUseCase:BaseUseCase<BookingAttemptInfo>() {
    private val dao = database.bookingAttemptDao()
    override fun getDao(): BaseDao<BookingAttemptInfo>? = dao

    override fun getRepository(): FirebaseDatabaseRepository<BookingAttemptInfo>? = null

    fun findAttemptBySalesId(salesId:Int):BookingAttemptInfo = dao.findBySalesId()
    suspend fun saveAttemptInfo(bookinfInfo:BookingAttemptInfo){
        dao.insert(bookinfInfo)
    }
    suspend fun deleteAttempts(){
        dao.clearAllAttempts()
    }
}