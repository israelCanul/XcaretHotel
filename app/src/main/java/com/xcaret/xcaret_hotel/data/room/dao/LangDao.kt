package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.sqlite.db.SimpleSQLiteQuery
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.domain.LangRestaurantDetail
import com.xcaret.xcaret_hotel.photopass.domain.DefaultPhotoLangLabel
import com.xcaret.xcaret_hotel.view.config.Language
import java.util.*

@Dao
abstract class LangActivityDao: BaseLangDao<LangActivity>(LangActivity::class.simpleName ?: "")

@Dao
abstract class LangAwardDao: BaseLangDao<LangAward>(LangAward::class.simpleName ?: "")

@Dao
abstract class LangCategoryDao: BaseLangDao<LangCategory>(LangCategory::class.simpleName ?: "") {
    @Query("""SELECT * FROM LangCategory WHERE parent_uid = :UID and lang_code = :lang""")
    abstract fun findByUID(
        UID: String,
        lang: String = Language.getLangCode(HotelXcaretApp.mContext)
    ): LiveData<LangCategory?>

    @Query("""SELECT l.id,l.lcat_title,l.parent_uid FROM LangCategory l
        INNER JOIN Category c
        ON l.parent_uid = c.uid
    WHERE c.filter_grouper =:filterGrouper""")
    abstract fun findByFilterGrouper(filterGrouper: String): List<LangCategory?>

}


@Dao
abstract class LangContactDao: BaseLangDao<LangContact>(LangContact::class.simpleName ?: "")

@Dao
abstract class LangFilterMapDao: BaseLangDao<LangFilterMap>(LangFilterMap::class.simpleName ?: "")

@Dao
abstract class LangRoomAmenityDao: BaseLangDao<LangRoomAmenity>(LangRoomAmenity::class.simpleName ?: "")

@Dao
abstract class LangHotelDao: BaseLangDao<LangHotel>(LangHotel::class.simpleName ?: "")

@Dao
abstract class LangParkTourDao: BaseLangDao<LangParkTour>(LangParkTour::class.simpleName ?: "")

@Dao
abstract class LangPlaceDao: BaseLangDao<LangPlace>(LangPlace::class.simpleName ?: "")

@Dao
abstract class LangRoomTypeDao: BaseLangDao<LangRoomType>(LangRoomType::class.simpleName ?: "")

@Dao
abstract class LangLabelDao: BaseLangDao<LangLabel>(LangLabel::class.simpleName ?: "") {

    @Query("SELECT * FROM LangLabel l WHERE parent_uid = :key AND lang_code = :lang")
    abstract fun findLabel(key: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<LangLabel?>

    @Query("SELECT * FROM LangLabel l WHERE parent_uid = :key AND lang_code = :lang")
    abstract fun findLabelOutLive(key: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LangLabel?

    @Query("SELECT * FROM LangLabel dl WHERE dl.parent_uid in (:label) AND dl.lang_code = :lang")
    abstract fun getLangByLabelLive(label: List<String>, lang: String = Locale.getDefault().language ): LiveData<List<LangLabel>>

}

@Dao
abstract class LangRestaurantDetailDao: BaseLangDao<LangRestaurantDetail>(LangRestaurantDetail::class.simpleName ?: ""){

    @Query(value = """
        SELECT * FROM LangRestaurantDetail l
        WHERE l.parent_uid IN (:restIds) AND l.lang_code = :lang
    """)
    abstract fun `in`(restIds: List<String>, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<LangRestaurantDetail>

    @Query(value = """
            SELECT * FROM LangRestaurantDetail l
            WHERE l.parent_uid = :restId AND lang_code = :lang
        """
    )
    abstract fun findById(restId: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<LangRestaurantDetail?>
}

@Dao
abstract class TitleDao: BaseLangDao<Title>(Title::class.simpleName ?: ""){

    @Query("SELECT * FROM Title t WHERE t.lang_code = :lang AND t.enabled = 1")
    abstract fun all(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): List<Title>

    @Query("SELECT * FROM Title t WHERE t.lang_code = :lang AND t.enabled = 1 LIMIT 1")
    abstract fun first(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Title?>

    @Query("SELECT * FROM Title t WHERE t.lang_code = :lang AND t.enabled = 1 LIMIT 1")
    abstract fun firstOutLiveData(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): Title?

    @Query("SELECT * FROM Title t WHERE t.value =:code AND t.lang_code = :lang AND t.enabled = 1 LIMIT 1")
    abstract fun findByCode(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Title?>

    @Query("SELECT * FROM Title t WHERE t.value =:code AND t.lang_code = :lang AND t.enabled = 1 LIMIT 1")
    abstract fun findByCodeOutLiveData(code: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): Title?

}

@Dao
abstract class LangFaqDao: BaseLangDao<LangFaq>(LangFaq::class.simpleName ?: "") {

    @Query("SELECT * FROM LangFaq l WHERE parent_uid = :key AND lang_code = :lang")
    abstract fun findLabel(key: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<LangFaq?>

    @Query("SELECT * FROM LangFaq l WHERE parent_uid = :key AND lang_code = :lang")
    abstract fun findLabelOutLive(key: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LangFaq?

}

@Dao
abstract class LangDestinationDao: BaseLangDao<LangDestination>(LangDestination::class.simpleName ?: "") {

}

@Dao
abstract class LangDestinationActivityDao: BaseLangDao<LangDestinationActivity>(LangDestinationActivity::class.simpleName ?: "") {

}

@Dao
abstract class LangAFIClassDao: BaseLangDao<LangAfiClass>(LangAfiClass::class.simpleName ?: "") {

}