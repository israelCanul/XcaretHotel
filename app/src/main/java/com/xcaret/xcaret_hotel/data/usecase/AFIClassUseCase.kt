package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.AFIClassRepository
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.AfiClass
import com.xcaret.xcaret_hotel.view.config.Language

class AFIClassUseCase : BaseUseCase<AfiClass>(){

    private val dao = database.afiClassDao()
    private val repository by lazy { AFIClassRepository() }

    override fun getDao(): BaseDao<AfiClass>? = dao

    override fun getRepository(): FirebaseDatabaseRepository<AfiClass>? = repository

    fun findAllAFIClasses()= dao.findAllAFIClasses()
    fun findAFIClassByUID(classUID:String,lang_code:String = Language.getLangCode(HotelXcaretApp.mContext))= dao.findAFIClassesByUID(classUID,  lang_code)

}