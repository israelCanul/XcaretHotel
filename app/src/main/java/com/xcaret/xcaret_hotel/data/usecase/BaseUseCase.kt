package com.xcaret.xcaret_hotel.data.usecase

import android.graphics.ColorSpace
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.HotelAppDatabase
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.LangCategory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.reflect.ParameterizedType

abstract class BaseUseCase<Model> {
    val database = HotelAppDatabase.getDatabase(HotelXcaretApp.mContext)

    abstract fun getDao() : BaseDao<Model>?
    abstract fun getRepository(): FirebaseDatabaseRepository<Model>?

    fun insert(list: MutableList<Model>, result: (finish: List<Long>) -> Unit){
        getDao()?.insert(list, result)
    }

    fun getFromFirebase(listener: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Model>) {
        getRepository()?.addListener(listener)
    }

    fun getFromSingleFirebase(listener: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Model>){
        getRepository()?.addSingleListener(listener)
    }

    open fun waitFromSingleFirebase(response: (result: MutableList<Model>) -> Unit){
        getFromSingleFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Model>{
            override fun onSuccess(result: MutableList<Model>) {
                response(result)
            }

            override fun onError(e: Exception) {
                response(mutableListOf<Model>())
            }
        })
    }
}