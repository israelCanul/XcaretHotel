package com.xcaret.xcaret_hotel.view.photopass.data.entity

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

class PhotoUserAlbumsEntity(
    var uid: String = "",
    var dateRegister: String? = null
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "dateRegister" to dateRegister
        )
    }
}

@IgnoreExtraProperties
data class PhotoCodesEntity(
    var uid: String? = null,
    var albumsList: Map<String, Any>? = null,
    var book: Boolean = false,
    var code: String? = null,
    var dateRegister: String? = null,
    var totalPurchase: Int? = null,
    var totalUnlock: Int? = null,
    var expiresDate: String? = null,
    var visitDate: String? = null,
    var valid: Boolean? = false
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "albumsList" to albumsList,
            "book" to book,
            "code" to code,
            "dateRegister" to dateRegister,
            "totalPurchase" to totalPurchase,
            "totalUnlock" to totalUnlock,
            "expiresDate" to expiresDate,
            "visitDate" to visitDate,
            "valid" to valid
        )
    }
}

@IgnoreExtraProperties
data class AlbumListEntity (
    var uid: String? = null,
    var parkID: Int? = null,
    var totalMedia: Int? = null,
    var unlock: Boolean = false,
    var visitDate: String? = null
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "parkID" to parkID,
            "totalMedia" to totalMedia,
            "unlock" to unlock,
            "visitDate" to visitDate
        )
    }
}

data class PhotoLogin(
    var token: String? = null,
    var exp: String? = null
)
