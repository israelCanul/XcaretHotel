package com.xcaret.xcaret_hotel.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xcaret.xcaret_hotel.BuildConfig
import com.xcaret.xcaret_hotel.data.room.dao.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.photopass.data.room.dao.PhotoCodesDao
import com.xcaret.xcaret_hotel.view.photopass.domain.AlbumList
import com.xcaret.xcaret_hotel.view.photopass.domain.PhotoCodes

@Database(entities = [
    Airline::class,
    Award::class,
    Category::class,
    BookingAttemptInfo::class,
    Faq::class,
    Contact::class,
    Country::class,
    Currency::class,
    FilterMap::class,
    HouseId::class,
    Language::class,
    LevelRoom::class,
    MapConfig::class,
    NearPlace::class,
    ParamSetting::class,
    PhoneCode::class,
    RoomAmenity::class,
    RoomLocation::class,
    Activity::class,
    Hotel::class,
    ParkTour::class,
    Place::class,
    RoomType::class,
    State::class,
    LangActivity::class,
    LangAward::class,
    LangCategory::class,
    LangContact::class,
    LangFilterMap::class,
    LangRoomAmenity::class,
    LangHotel::class,
    LangParkTour::class,
    LangPlace::class,
    LangRoomType::class,
    LangLabel::class,
    LangFaq::class,
    LangRestaurantDetail::class,
    Title::class,
    Weather::class,
    WebCam::class,
    User::class,
    DateQuotes::class,
    SuiteQuotes::class,
    SuiteRatePlans::class,
    Reservation::class,
    ScheduleActivity::class,
    AirlineTerminal::class,
    ReservationKeys::class,
    Gallery::class,
    Destination::class,
    DestinationActivity::class,
    AfiClass::class,
    LangAfiClass::class,
    LangDestinationActivity::class,
    LangDestination::class,
    PhotoCodes::class, AlbumList::class,// se agrega para el modulo de photo pass
], version = BuildConfig.VERSION_CODE, exportSchema = false)

abstract class HotelAppDatabase: RoomDatabase(){
    abstract fun activityDao(): ActivityDao
    abstract fun airlineDao(): AirlineDao
    abstract fun awardDao(): AwardDao
    abstract fun categoryDao(): CategoryDao
    abstract fun contactDao(): ContactDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun filterMapDao(): FilterMapDao
    abstract fun houseIdDao(): HouseIdDao
    abstract fun languageDao(): LanguageDao
    abstract fun levelRoomDao(): LevelRoomDao
    abstract fun mapConfigDao(): MapConfigDao
    abstract fun nearPlaceDao(): NearPlaceDao
    abstract fun paramSettingDao(): ParamSettingDao
    abstract fun phoneCodeDao(): PhoneCodeDao
    abstract fun roomAmenityDao(): RoomAmenityDao
    abstract fun roomLocationDao(): RoomLocationDao
    abstract fun faqDao():FaqDao
    abstract fun afiClassDao():AFIClassDao
    abstract fun destinationDao():DestinationDao
    abstract fun destinationActivityDao():DestinationActivityDao

    abstract fun hotelDao(): HotelDao
    abstract fun parkTourDao(): ParkTourDao
    abstract fun placeDao(): PlaceDao
    abstract fun roomTypeDao(): RoomTypeDao
    abstract fun webCamDao(): WebCamDao

    abstract fun langActivityDao(): LangActivityDao
    abstract fun langAwardDao(): LangAwardDao
    abstract fun langCategoryDao(): LangCategoryDao
    abstract fun langContactDao(): LangContactDao
    abstract fun langFilterMapDao(): LangFilterMapDao
    abstract fun langRoomAmenityDao(): LangRoomAmenityDao
    abstract fun langHotelDao(): LangHotelDao
    abstract fun langParkTour(): LangParkTourDao
    abstract fun langPlaceDao(): LangPlaceDao
    abstract fun langRoomTypeDao(): LangRoomTypeDao
    abstract fun langLabelDao(): LangLabelDao
    abstract fun langRestaurantDetailDao(): LangRestaurantDetailDao
    abstract fun countryDao(): CountryDao
    abstract fun stateDao(): StateDao
    abstract fun titleDao(): TitleDao
    abstract fun scheduleActivityDao(): ScheduleActivityDao

    abstract fun weatherDao(): WeatherDao

    abstract fun userDao(): UserDao

    abstract fun dateQuotesDao(): DateQuotesDao
    abstract fun suiteQuotesDao(): SuiteQuotesDao
    abstract fun suiteRatePlansDao(): SuiteRatePlansDao

    abstract fun reservationDao(): ReservationDao
    abstract fun reservationKeyDao(): ReservationKeyDao
    abstract fun airlineTerminalDao(): AirlineTerminalDao

    abstract fun langFaqDao():LangFaqDao
    abstract fun langDestinationActivityDao(): LangDestinationActivityDao
    abstract fun langDestinationDao(): LangDestinationDao
    abstract fun langAfiClassDao(): LangAFIClassDao

    abstract fun galleryDao():GalleryDao

    abstract fun bookingAttemptDao():BookingDao


    // PHOTO PASS
    abstract fun photoCodesDao(): PhotoCodesDao


    companion object {
        private const val DB_NAME = "hotel_xcaret"
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: HotelAppDatabase? = null

        fun getDatabase(context: Context): HotelAppDatabase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HotelAppDatabase::class.java,
                    DB_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }

}