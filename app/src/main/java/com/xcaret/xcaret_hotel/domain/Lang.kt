package com.xcaret.xcaret_hotel.domain

import androidx.room.*
import com.google.firebase.database.IgnoreExtraProperties
import com.xcaret.xcaret_hotel.view.config.ListItemViewModel

abstract class LangBase: ListItemViewModel() {
    abstract var id: Long
    abstract var parent_uid: String?
    abstract var lang_code: String?
}

@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangActivity(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,
    override var parent_uid: String? = null,
    override var lang_code: String? = null,

    var image: String? = null,
    var longDescription: String? = null,
    var note: String? = null,
    var recomendation: String? = null,
    var shortDescription: String? = null,
    var title: String? = null,
    var warning: String? = null
): LangBase()

@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangAward(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,

    val name: String? = null,
    override var lang_code: String?
): LangBase()

@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangCategory(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,

    @ColumnInfo(name = "lcat_image")
    var image: String? = null,

    @ColumnInfo(name = "lcat_long_description")
    var longDescription: String? = null,

    @ColumnInfo(name = "lcat_short_description")
    var shortDescription: String? = null,

    @ColumnInfo(name = "lcat_title")
    var title: String? = null,

    override var lang_code: String?
): LangBase()

@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangFaq(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,

    @ColumnInfo(name = "lfaq_answer")
    var answer: String? = null,

    @ColumnInfo(name = "lfaq_question")
    var question: String? = null,

    override var lang_code: String?
): LangBase()

@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangContact(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,
    var name: String? = null,
    override var lang_code: String?
): LangBase()

@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangFilterMap(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,

    val name: String? = null,
    override var lang_code: String? = null
): LangBase()

@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangRoomAmenity(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,

    @ColumnInfo(name = "lra_description")
    var description: String? = null,

    @ColumnInfo(name = "lra_description_short")
    var descriptionShort: String? = null,

    override var lang_code: String? = null
): LangBase()


@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangHotel(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,

    @ColumnInfo(name = "lhot_address")
    var address: String? = null,

    @ColumnInfo(name = "lhot_slogan")
    var slogan: String? = null,

    override var lang_code: String? = null
): LangBase()

@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangParkTour(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,
    override var parent_uid: String? = null,

    var address: String? = null,
    var slogan: String? = null,

    @ColumnInfo(name = "cancellation_fee")
    var cancellationFee: Int? = 0,
    var include: String? = null,

    @ColumnInfo(name = "description_long")
    var descriptionLong: String? = null,

    @ColumnInfo(name = "description_short")
    var descriptionShort: String? = null,
    var image: String? = null,
    var note: String? = null,
    var recomendation: String? = null,
    var schedule: String? = null,
    var onBoardServices:String? =null,
    override var lang_code: String? = null

): LangBase()

@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangPlace(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,

    @ColumnInfo(name = "lp_title")
    var title: String? = null,

    @ColumnInfo(name = "lp_description_long")
    var descriptionLong: String? = null,

    @ColumnInfo(name = "lp_description_short")
    var descriptionShort: String? = null,

    @ColumnInfo(name = "lp_location")
    var location: String? = null,

    @ColumnInfo(name = "lp_image")
    var image: String? = null,

    override var lang_code: String? = null
): LangBase()

@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangRoomType(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,

    @ColumnInfo(name = "lrp_title")
    var title: String? = null,

    @ColumnInfo(name = "lrp_description_long")
    var descriptionLong: String? = null,

    @ColumnInfo(name = "lrp_description_short")
    var descriptionShort: String? = null,

    @ColumnInfo(name = "lrp_image")
    var image: String? = null,

    override var lang_code: String? = null
): LangBase() {
    fun formatTitle() = title?.toLowerCase()?.split(" ")?.joinToString(" "){ it.capitalize() }
}

@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangRestaurantDetail(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,

    @ColumnInfo(name = "additional_info")
    var additionalInfo: String? = null,
    var concept: String? = null,

    @ColumnInfo(name = "need_reservation")
    var needReservation: Boolean? = false,

    @ColumnInfo(name = "open_for")
    var openFor: String? = null,
    var type: String? = null,
    override var lang_code: String? = null
): LangBase()

@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangLabel(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,

    @ColumnInfo(name = "ll_value")
    var value: String? = null,

    override var lang_code: String? = null,
    @Ignore var is_selected: Boolean = false
): LangBase()

@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class Title(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,
    var code: String? = null,
    var value: String? = null,
    var name: String? = null,
    var enabled: Boolean = false,
    var is_selected: Boolean = false,
    override var lang_code: String? = null
): LangBase()

@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangAfiClass(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,

    @ColumnInfo(name = "lAfi_description")
    var description : String? = null,
    @ColumnInfo(name = "lAfi_name")
    var name: String? = null,

    override var lang_code: String?
): LangBase()
@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangDestination(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,

    @ColumnInfo(name = "lDestination_description")
    var description: String? = null,

    @ColumnInfo(name = "lDestination_title")
    var title: String? = null,

    override var lang_code: String?
): LangBase()
@Entity(indices = [(Index("parent_uid", "lang_code"))])
data class LangDestinationActivity(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var parent_uid: String? = null,

    @ColumnInfo(name = "lDestAct_description")
    var description: String? = null,

    @ColumnInfo(name = "lDestAct_title")
    var title: String? = null,

    var schedule: String? = null,

    override var lang_code: String?
): LangBase()