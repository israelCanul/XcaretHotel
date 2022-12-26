package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.RoomTypeRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Gallery
import com.xcaret.xcaret_hotel.domain.RoomType

class GalleryUseCase : BaseUseCase<Gallery>(){

    private val dao = database.galleryDao()
    private val repository: RoomTypeRepository by lazy { RoomTypeRepository() }

    override fun getDao(): BaseDao<Gallery>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<Gallery>? = null

    fun getImagesFromGallery(parentUID:String):List<Gallery> = dao.findImagesFromGalleryByParentUID(parentUID)
}