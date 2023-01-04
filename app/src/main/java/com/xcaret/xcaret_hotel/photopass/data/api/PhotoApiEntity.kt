package com.xcaret.xcaret_hotel.photopass.data.api

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
data class Login(
    var token: String? = null,
    var iat: Long? = null,
    var exp: Long? = null
)

@IgnoreExtraProperties
data class Code(
    @ColumnInfo(name = "is_Valid")
    @SerializedName("IsValid")
    var isValid: Boolean? = null,

    @ColumnInfo(name = "is_Book")
    @SerializedName("IsBook")
    var isBook: Boolean? = null,

    @ColumnInfo(name = "product_Name")
    @SerializedName("ProductName")
    var productName: String? = null,

    @ColumnInfo(name = "selected_Park")
    @SerializedName("SelectedPark")
    var selectedPark: Int? = null,

    @ColumnInfo(name = "visitor_Email")
    @SerializedName("VisitorEmail")
    var visitorEmail: String? = null,

    @ColumnInfo(name = "visit_Date")
    @SerializedName("VisitDate")
    var visitDate: String? = null,

    @ColumnInfo(name = "expires_Date")
    @SerializedName("ExpiresDate")
    var expiresDate: String? = null,

    @ColumnInfo(name = "order_Type")
    @SerializedName("OrderType")
    var orderType: Int? = null,

    @ColumnInfo(name = "available_Parks")
    @SerializedName("AvailableParks")
    var availableParks: Int? = null
)

@IgnoreExtraProperties
data class  Album(
    @ColumnInfo(name = "park_Id")
    @SerializedName("ParkID")
    var parkId: Int? = null,

    @ColumnInfo(name = "visit_Date")
    @SerializedName("VisitDate")
    var visitDate: String? = null,

    @ColumnInfo(name = "total_Media")
    @SerializedName("TotalMedia")
    var totalMedia: Int? = null,

    @ColumnInfo(name = "album_Type")
    @SerializedName("AlbumType")
    var albumType: Int? = null,

    @SerializedName("Status")
    var status: Int? = null
)

@IgnoreExtraProperties
data class  SelectedPark(
    @ColumnInfo(name = "park_Id")
    @SerializedName("ParkID")
    var parkId: Int? = null
)

@IgnoreExtraProperties
data class GetPhotos(
    @ColumnInfo(name = "current_Page")
    @SerializedName("CurrentPage")
    var currentPage: Int? = null,

    @ColumnInfo(name = "total_Pages")
    @SerializedName("TotalPages")
    var totalPages: Int? = null,

    @ColumnInfo(name = "total_Photos")
    @SerializedName("TotalPhotos")
    var totalPhotos: Int? = null,

    @SerializedName("Photos")
    var photos: List<Photo>? = null
)

@IgnoreExtraProperties
@Parcelize
data class Photo(
    @ColumnInfo(name = "media_Id")
    @SerializedName("MediaID")
    var mediaID: Int? = null,

    @ColumnInfo(name = "thumb")
    @SerializedName("Thumb")
    var thumb: String? = null,

    @ColumnInfo(name = "mink")
    @SerializedName("Mink")
    var mink:String? = null,

    @ColumnInfo(name = "mini")
    @SerializedName("Mini")
    var mini:String? = null,

    @ColumnInfo(name = "orig")
    @SerializedName("Orig")
    var orig:String? = null,

    @Ignore var checked:Boolean = false
) : Parcelable

@IgnoreExtraProperties
data class ShareFile(
    var uri: Uri,
    var photo: Photo,
    var resource: Bitmap
)

@IgnoreExtraProperties
data class  PhotoStatus(
    @ColumnInfo(name = "pending")
    @SerializedName("Pending")
    var pending: Int?,

    @ColumnInfo(name = "total")
    @SerializedName("Total")
    var total: Int?
)

@IgnoreExtraProperties
data class VisitPark(
    @SerializedName("Status")
    var status: Boolean? = null,

    @ColumnInfo(name = "total_parks")
    @SerializedName("TotalParks")
    var totalParks: Int? = null
)

@IgnoreExtraProperties
data class UrlCode(
    @SerializedName("Status")
    var status: Boolean? = null,

    @SerializedName("Url")
    var url: String? = null,

    @SerializedName("Message")
    var message: String? = null
)

@IgnoreExtraProperties
data class MediaLog(
    @SerializedName("MediaLogID")
    var mediaLogID: String? = null,
    @SerializedName("JoinedCode")
    var joinedCode: String? = null,
    @SerializedName("ParkID")
    var parkID: Int? = null,
    @SerializedName("Action")
    var action: String? = null,
    @SerializedName("MediaTotal")
    var mediaTotal: Int? = null,
    @SerializedName("MediaDownload")
    var mediaDownload: Int? = null,
    @SerializedName("Device")
    var device: String? = "android",
    @SerializedName("IsGallery")
    var isGallery: Boolean? = true
)

data class PhotoResponse<T>(
    private val result: Boolean = false,
    val data: T? = null)

