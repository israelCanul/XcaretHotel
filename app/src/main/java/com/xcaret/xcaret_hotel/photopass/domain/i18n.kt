package com.xcaret.xcaret_hotel.photopass.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xcaret.xcaret_hotel.view.config.ListItemViewModel

@Entity
data class DefaultPhotoLangLabel(
    @PrimaryKey var uid: String = "",
    var lbl_key: String? = null,
    var name:String? = null
): ListItemViewModel()

@Entity
data class LocalPhotoLangLabel(
    @PrimaryKey var uid: String = "",
    var lbl_key: String? = null,
    var name:String? = null
): ListItemViewModel()