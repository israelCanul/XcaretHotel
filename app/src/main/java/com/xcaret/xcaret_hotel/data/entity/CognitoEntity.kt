package com.xcaret.xcaret_hotel.data.entity

data class CognitoValidUserEntity(
    val SUCCESS: Boolean = false,
    val MESSAGE: String? = null,
    val UserData: CognitoUserDataEntityAlt? = null,
    val USERDATA: CognitoUserDataEntity? = null,
    val TOKEN: String = ""
)

data class CognitoUserDataEntity(
    val SUB: String = "",
    val EMAIL_VERIFIED: Boolean = true,
    val IIS: String =  "",
    val COGNITO_USERNAME: String = "",
    val AUD: String = "",
    val EVENT_ID: String = "",
    val TOKEN_USE: String = "",
    val AUTH_TIME: String = "",
    val EXP: String = "",
    val IAT: String = "",
    val EMAIL: String = ""
)
data class CognitoUserDataEntityAlt(
    val UserName: String = "",
    val Enabled: Boolean = true,
    val Status: String =  "",
    val CreateDate: String = "",
    val ModifiedDate: String = ""
)