package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.RoomAmenityRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.RoomAmenity
import com.xcaret.xcaret_hotel.domain.RoomType
import com.xcaret.xcaret_hotel.view.config.Language
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class RoomAmenityUseCase: BaseUseCase<RoomAmenity>(){

    private val dao = database.roomAmenityDao()
    private val repository: RoomAmenityRepository by lazy { RoomAmenityRepository() }

    override fun getDao(): BaseDao<RoomAmenity>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<RoomAmenity>? = repository

    fun allByRoomUID(roomUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<RoomAmenity>> = dao.allByRoomUID(roomUID, lang)
    fun allByListIds(ids: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<RoomAmenity> = dao.allByListIds(ids, lang)
    fun allByCategory(categoryUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<RoomAmenity> = dao.allByCategory(categoryUID, lang)

    fun fetchMainAmenitiesForRooms(roomsType: List<RoomType>, roomsCategory: Category?, result: (success: Boolean) -> Unit){
        doAsync {
            val amenities = allByCategory(roomsCategory?.uid ?: "")
            roomsType.forEach { rType ->
                val amenities = amenities.filter { it.roomTypeUID == rType.uid && it.categoryUID == roomsCategory?.uid}
                rType.mainAmenity = amenities.sortedByDescending { it.order }
            }
            uiThread { result(true) }
        }
    }

    fun fetchMainAmenitiesForRoom(roomUID: String, roomsCategory: Category?, result: (amenities: List<RoomAmenity>) -> Unit){
        doAsync {
            val amenities = allByCategory(roomsCategory?.uid ?: "")
            val res = amenities.filter { it.roomTypeUID == roomUID && it.categoryUID == roomsCategory?.uid }.sortedByDescending { it.order }
            uiThread {
                result(res)
            }
        }
    }
}