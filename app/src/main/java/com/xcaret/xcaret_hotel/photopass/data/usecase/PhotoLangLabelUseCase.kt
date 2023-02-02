package com.xcaret.xcaret_hotel.photopass.data.usecase

import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.LangLabelRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseLangDao
import com.xcaret.xcaret_hotel.data.usecase.BaseLangUseCase
import com.xcaret.xcaret_hotel.domain.LangLabel
import com.xcaret.xcaret_hotel.photopass.domain.DefaultPhotoLangLabel
import com.xcaret.xcaret_hotel.view.config.Language
import java.util.*

class PhotoLangLabelUseCase: BaseLangUseCase<LangLabel>() {
    private val dao = database.langLabelDao()
    private val repository: LangLabelRepository by lazy { LangLabelRepository() }

    override fun getDao(): BaseLangDao<LangLabel>? = dao
    override fun getRepository(): FirebaseDatabaseRepository<LangLabel>? = repository

    fun findLabel(key: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<LangLabel?> = dao.findLabel(key, lang)
    fun findLabelOutLive(key: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LangLabel? = dao.findLabelOutLive(key, lang)
    fun getLangByLabelLive(lang: String, label: List<String>): LiveData<List<LangLabel>> = dao.getLangByLabelLive(label,  Locale.getDefault().language)
}