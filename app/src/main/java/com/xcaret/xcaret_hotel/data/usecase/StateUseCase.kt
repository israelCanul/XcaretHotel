package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.State

class StateUseCase: BaseUseCase<State>(){
    private val dao = database.stateDao()

    override fun getDao(): BaseDao<State> = dao
    override fun getRepository(): FirebaseDatabaseRepository<State>? = null

    fun allByCountryId(countryId: Int) = dao.allByCountryCode(countryId)
    fun findByAbbreviation(abbreviation: String): LiveData<State?> = dao.findByAbbreviation(abbreviation)
    fun findByAbbreviationOutLiveData(abbreviation: String): State? = dao.findByAbbreviationOutLiveData(abbreviation)

}