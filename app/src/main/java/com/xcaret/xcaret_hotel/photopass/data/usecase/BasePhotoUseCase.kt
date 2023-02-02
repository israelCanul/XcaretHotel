package com.xcaret.xcaret_hotel.photopass.data.usecase

import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.room.HotelAppDatabase
import com.xcaret.xcaret_hotel.photopass.data.room.dao.BasePhotoDao

abstract class BasePhotoUseCase<Model>{
    val database = HotelAppDatabase.getDatabase(HotelXcaretApp.mContext)
    abstract fun getDao() : BasePhotoDao<Model>?
    abstract fun insertAll(list: MutableList<*>)
    abstract fun insertAllFromList(list: MutableList<*>)
    abstract fun removeBy(data: Any)
    fun insert(list: MutableList<Model>, result: (finish: List<Long>) -> Unit){
        getDao()?.insert(list, result)
    }

}