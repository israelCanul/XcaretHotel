package com.xcaret.xcaret_hotel.photopass.data.repository

import com.xcaret.xcaret_hotel.photopass.data.mapper.DefaultPhotoLangMapper
import com.xcaret.xcaret_hotel.photopass.domain.DefaultPhotoLangLabel


class PhotoLangRepository {
}
class DefaultPhotoLangLabelRepository() : SingleFirebaseDatabaseRepository<DefaultPhotoLangLabel>(
    DefaultPhotoLangMapper()
) {
    override fun getRootNode(): String = FirebasePhotoReference.DEFAULT_LANGS
}