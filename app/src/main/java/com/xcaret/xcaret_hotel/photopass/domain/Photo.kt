package com.xcaret.xcaret_hotel.photopass.domain

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.view.config.ListItemViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class PhotoUser(
    var Username: String = "",
    var Password: String = ""

){
    fun getCreateRequest(): JsonObject {
        val request = JsonObject()
        request.addProperty("Username", Username) //"comercial_mobile"
        request.addProperty("Password", Password) //"yRuIACTyVqbtAQ7B<!GMdQ"
        Log.d("CreateRequest", request.toString())
        return request
    }
}


@Entity
data class PhotoCodes(
    @ColumnInfo(name = "photo_id")
    @PrimaryKey var uid: String = "",
    var book: Boolean = false,
    var code: String? = null,
    @ColumnInfo(name = "date_register")
    var dateRegister: String? = null,
    @ColumnInfo(name = "total_purchase")
    var totalPurchase: Int? = null,
    @ColumnInfo(name = "total_unlock")
    var totalUnlock: Int? = null,
    var expiresDate: String? = null,
    var visitDate: String? = null,
    var valid: Boolean? = false,
    @Ignore var albumsList: MutableList<AlbumList>? = mutableListOf()
) : ListItemViewModel()

@Entity
data class AlbumList(
    @ColumnInfo(name = "generic_id")
    @PrimaryKey var uid: String = "",
    @ColumnInfo(name = "park_id")
    var parkID: Int? = null,
    @ColumnInfo(name = "total_media")
    var totalMedia: Int? = null,
    var unlock: Boolean = false,
    @ColumnInfo(name = "visit_date")
    var visitDate: String? = null,
    var code: String? = null,
    var expiresDate: String? = null,
    var visitDateAlbum: String? = null,
    @Ignore var isTitle: Boolean = false,
    @Ignore var totalPurchase: Int? = null,
    @Ignore var totalUnlock: Int? = null,
    @Ignore var statusExpiresDate: Int? = null,
    @Ignore var valid: Boolean? = false,
    @Ignore var showStatus: Boolean? = false
) : ListItemViewModel() {
    @Ignore
    fun totalExpiresDate(): String {
        return if(expiresDate != null && visitDateAlbum != null) {
            val expiresDate = LocalDate.parse(expiresDate)
            val visitDate = LocalDate.parse(visitDateAlbum)
            visitDate.until(expiresDate, ChronoUnit.DAYS).toString()
        }else{
            "0"
        }
    }

    @Ignore
    fun getStatusVisitDate(): Int {
        return try {
            val dateTime = LocalDate.now()

            if(expiresDate != null && visitDateAlbum != null){
                val visitDate = LocalDate.parse(visitDateAlbum)
                val expiresDate = LocalDate.parse(expiresDate)
                val otherDay = expiresDate.until(dateTime, ChronoUnit.DAYS)
                val greenDay = visitDate.until(dateTime, ChronoUnit.DAYS) //Colocar logica de vigencia
                val yellowDay = dateTime.until(expiresDate, ChronoUnit.DAYS)
                val regularDay = visitDate.until(expiresDate, ChronoUnit.DAYS) - 16

                return when {
                    greenDay in 0..3 -> {
                        statusExpiresDate = 1
                        valid = true
                        showStatus = true
                        1
                    }
                    yellowDay in 0..15 -> {
                        statusExpiresDate = 0
                        valid = true
                        showStatus = true
                        1
                    }
                    yellowDay < 0 -> {
                        statusExpiresDate = 0
                        valid = false
                        showStatus = false
                        1
                    }
                    greenDay in 4..regularDay ->{
                        statusExpiresDate = 0
                        valid = true
                        showStatus = false
                        1
                    }
                    else -> {
                        statusExpiresDate = 0
                        valid = false
                        showStatus = false
                        0
                    }
                }

            }else{
                statusExpiresDate = 0
                valid = false
                showStatus = false
                return 0
            }
        } catch (e: Exception) {
            statusExpiresDate = 0
            valid = false
            showStatus = false
            0
        }
    }
}
