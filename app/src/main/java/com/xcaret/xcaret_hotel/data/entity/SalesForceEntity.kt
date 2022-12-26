package com.xcaret.xcaret_hotel.data.entity


class SalesForceGetProfileEntity(
    var SUCCESS: Boolean? = true,
    var MESSAGE: String = "",
    val COUNT: Int = -1,
    val CONTACT: SalesForceElementEntity? = null
)

class SalesForcePaxProfileEntity(
    var SUCCESS: Boolean? = false,
    val CONTACT: SalesForceElementEntity2? = null
)


open class SalesForceElementEntity(
    val ELEMENT: List<SalesForceProfileEntity>? = null
)

open class SalesForceElementEntity2(
    val element: List<SalesForceProfileEntity>? = null
)


data class SalesForceProfileEntity(
    val ID: String = "",
    val EMAIL: String = "",
    val FIRSTNAME: String = "",
    val LASTNAME: String = "",
    val NAME: String = "",
    val PHONE: String = "",
    val MAILINGCITY: String = "",
    val MAILINGCOUNTRYCODEISO__C: String = "",
    val MAILINGSTATECODEISO__C: String = "",
    val MAILINGPOSTALCODE: String = "",
    val MAILINGSTREET: String = "",
    val NOTIFICACION_NOTICIAS__C: Boolean = true,
    val EXTERNAL_ID__C: String = "",
    val TITLE: String = "",
    val COMO_SE_ENTERO_DE_NOSOTROS__C: String = ""
)