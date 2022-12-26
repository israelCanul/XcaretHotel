package com.xcaret.xcaret_hotel.data.entity

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class UserEntity(
    val salesForceId: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var provider: String = "",
    val title: String = "",
    val userName: String = "",
    val address: String = "",
    val country: String = "",
    val state: String = "",
    val cp: String = "",
    var platform: String = "",
    var lang: String = "",
    var macAddress: String = "",
    var registered: String = "",
    var device: String = "",
    var token: String = "",
    var version: String = "",
    var notify_promotions: Boolean = true,
    var picture: String = "",
    var city: String = ""
)