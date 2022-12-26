package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.Gallery
import com.xcaret.xcaret_hotel.domain.LangFaq
import com.xcaret.xcaret_hotel.domain.RoomType
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.Settings

@Dao
abstract class GalleryDao : BaseDao<Gallery>(Gallery::class.simpleName ?: ""){

    @Query("SELECT * FROM Gallery l WHERE parent_uid = :key and enabled = 1")
    abstract fun findImagesFromGalleryByParentUID(key: String): List<Gallery>

}