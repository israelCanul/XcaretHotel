package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.data.repository.AirlineRepository
import com.xcaret.xcaret_hotel.data.repository.AirlineTerminalRepository
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Airline
import com.xcaret.xcaret_hotel.domain.AirlineTerminal

class AirlineUseCase: BaseUseCase<Airline>(){
    private val dao = database.airlineDao()
    private val repository by lazy { AirlineRepository() }

    override fun getDao(): BaseDao<Airline> = dao
    override fun getRepository(): FirebaseDatabaseRepository<Airline> = repository

    fun all(): LiveData<List<Airline>> = dao.all()
}


class AirlineTerminalUseCase: BaseUseCase<AirlineTerminal>(){
    private val dao = database.airlineTerminalDao()
    private val repository by lazy { AirlineTerminalRepository() }

    override fun getDao(): BaseDao<AirlineTerminal> = dao
    override fun getRepository(): FirebaseDatabaseRepository<AirlineTerminal> = repository

    fun all(): LiveData<List<AirlineTerminal>> = dao.all()
}