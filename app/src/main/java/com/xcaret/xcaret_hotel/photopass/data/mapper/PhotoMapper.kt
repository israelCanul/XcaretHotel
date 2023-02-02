package com.xcaret.xcaret_hotel.photopass.data.mapper

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.gson.Gson

import com.xcaret.xcaret_hotel.photopass.data.entity.LangPhotoLabelEntity
import com.xcaret.xcaret_hotel.photopass.data.entity.PhotoCodesEntity
import com.xcaret.xcaret_hotel.photopass.domain.AlbumList
import com.xcaret.xcaret_hotel.photopass.domain.DefaultPhotoLangLabel
import com.xcaret.xcaret_hotel.photopass.domain.PhotoCodes

class PhotoUserMapper: FirebasePhotoMapperSingleObject<PhotoCodesEntity, PhotoCodes>(){
    private var gson = Gson()


    override fun map(from: PhotoCodesEntity?, key: String?): PhotoCodes {
        val photoCodes = PhotoCodes()
        photoCodes.uid = key?: ""
        from.let {
            photoCodes.book = from?.book ?: false
            photoCodes.code = from?.code
            photoCodes.dateRegister = from?.dateRegister
            photoCodes.totalPurchase = from?.totalPurchase
            photoCodes.totalUnlock = from?.totalUnlock
            photoCodes.expiresDate = from?.expiresDate
            photoCodes.visitDate = from?.visitDate
            photoCodes.valid = from?.valid
            it?.albumsList?.let { albumlist ->
                albumlist.forEach { (key, value) ->
                    Log.d("", key)
                    val jsonStr: String = gson.toJson(value)
                    val album =  gson.fromJson(jsonStr, AlbumList::class.java)
                    Log.d("", jsonStr)
                    album.uid = key
                    album.code = photoCodes.code
                    album.expiresDate = from?.expiresDate
                    album.visitDateAlbum = from?.visitDate
                    photoCodes.albumsList?.add(album)
                }
            }
        }
        return photoCodes
    }
}

class UserPhotoMapper: FirebasePhotoMapper<Any, String>(){
    override fun map(from: Any?, key: String?): String {
        return key?: ""
    }
}
class DefaultPhotoLangMapper: FirebasePhotoMapper<LangPhotoLabelEntity, DefaultPhotoLangLabel>() {
    override fun map(from: LangPhotoLabelEntity?, key: String?): DefaultPhotoLangLabel {
        val defaultLang = DefaultPhotoLangLabel()
        defaultLang.uid = key!!
        defaultLang.lbl_key = from?.lbl_key
        defaultLang.name = from?.name
        return defaultLang
    }
}