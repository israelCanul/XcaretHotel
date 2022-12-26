package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.WebCamRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.WebCam

class WebCamUseCase: BaseUseCase<WebCam>(){
    private val dao = database.webCamDao()
    private val repository by lazy { WebCamRepository() }

    override fun getDao(): BaseDao<WebCam>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<WebCam>? = repository

    fun getAllWebCamsByCategory(categoryUID: String): LiveData<List<WebCam>> = dao.getAllWebCamsByCategory(categoryUID)
    fun getAllWebCams(): LiveData<List<WebCam>> = dao.getWebCams()
    fun findByCode(code: String): LiveData<WebCam?> = dao.findByCode(code)
}