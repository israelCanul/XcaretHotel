package com.xcaret.xcaret_hotel.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.CategoryRepository
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.RoomType
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.Settings
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class CategoryUseCase: BaseUseCase<Category>(){

    private val dao = database.categoryDao()
    private val repository by lazy { CategoryRepository() }

    override fun getDao(): BaseDao<Category>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<Category>? = repository

    fun allByHotel(hotelUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>> = dao.allByHotel(hotelUID, lang)
    fun allByFilterGroup(categoryUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>> = dao.allByFilterGroup(categoryUID, lang)
    fun allByListFilterGroup(categoryUID: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>> = dao.allByListFilterGroup(categoryUID, lang)
    fun findById(uid: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = dao.findById(uid, lang)
    fun findByCode(uid: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): Category? = dao.findByCode(uid, lang)
    fun findByCodeNoHotel(uid: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): Category? = dao.findByCodeNoHotelUID(uid, lang)
    fun findByCodeOutHotel(uid: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): Category? = dao.findByCodeOutHotel(uid)
    fun findCategoryByCodeLive(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = dao.findCategoryByCodeLive(code)
    fun findByCodeLive(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = dao.findByCodeLive(code, lang)
    fun findByListIds(ids: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<Category> = dao.findByListIds(ids, lang)
    fun findByCodeLiveWithoutLang(code: String): LiveData<Category?> = dao.findByCodeLiveWithoutLang(code)
    fun findByCodeLiveWithoutHotel(code: String): LiveData<Category?> = dao.findByCodeLiveWithoutHotel(code)
    fun findByListCodes(codes: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext), hotelUID: String = Settings.getHotelUIDSelected(HotelXcaretApp.mContext)): LiveData<List<String>> = dao.findByListCodes(codes, hotelUID, lang)
    fun findByCodeLiveOutHotel(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Category?> = dao.findByCodeLiveOutHotel(code, lang)
    fun listByCodeLiveOutHotel(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Category>> = dao.listByCodeLiveOutHotel(code, lang)

    fun fetchForRooms(roomsType: List<RoomType>, result: (success: Boolean) -> Unit){
        doAsync {
            val ids = mutableListOf<String>()
            roomsType.forEach { rType ->
                rType.categoryUID?.let {
                    if(!ids.contains(it)) ids.add(it)
                }
            }
            val categories = findByListIds(ids)
            roomsType.forEach { rType ->
                rType.category = categories.firstOrNull { it.uid == rType.categoryUID }
            }
            uiThread { result(true) }
        }
    }

}