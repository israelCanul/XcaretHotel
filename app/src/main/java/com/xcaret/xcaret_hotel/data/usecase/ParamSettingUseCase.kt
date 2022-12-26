package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.ParamSettingRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.ParamSetting
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.DateUtil
import com.xcaret.xcaret_hotel.view.config.LogHX
import com.xcaret.xcaret_hotel.view.config.Session
import org.jetbrains.anko.doAsync

class ParamSettingUseCase: BaseUseCase<ParamSetting>() {
    private val dao = database.paramSettingDao()
    private val repository = ParamSettingRepository()

    override fun getDao(): BaseDao<ParamSetting>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<ParamSetting>? = repository

    fun findParamSettingByCode(code: String): ParamSetting? = dao.findParamSettingByCode(code)

    fun saveRememberDate(){
        doAsync {
            val paramSetting = findParamSettingByCode(Constants.DAYS_REMEMBER_INCOMPLETE_PROFILE)
            paramSetting?.let { ps ->
                ps.value?.toIntOrNull()?.let {
                    val dateToRemember = DateUtil.addDaysToCurrentDay(numDays = it)
                    LogHX.i("ParamSettingUseCase", dateToRemember)
                    Session.setRememberDate(dateToRemember, HotelXcaretApp.mContext)
                }
            }
        }
    }


}