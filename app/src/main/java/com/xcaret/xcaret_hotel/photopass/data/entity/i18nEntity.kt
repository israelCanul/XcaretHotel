package com.xcaret.xcaret_hotel.photopass.data.entity

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class LangPhotoLabelEntity(
    var uid: String? = null,
    var lbl_key: String? = null,
    var name: String? = null
)