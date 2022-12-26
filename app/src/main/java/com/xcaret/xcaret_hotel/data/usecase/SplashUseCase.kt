package com.xcaret.xcaret_hotel.data.usecase


import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.domain.Currency
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.doAsync
import java.lang.RuntimeException
import java.util.*
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.Language


class SplashUseCase(private val resultListener: ResultListener){
    private val tag = "SplashUseCase"
    private var numObjectDownloaded = 0

    private val activityUseCase: ActivityUseCase by lazy { ActivityUseCase() }
    private val airlineUseCase: AirlineUseCase by lazy { AirlineUseCase() }
    private val awardUseCase: AwardUseCase by lazy { AwardUseCase() }
    private val categoryUseCase: CategoryUseCase by lazy { CategoryUseCase() }
    private val contactUseCase: ContactUseCase by lazy { ContactUseCase() }
    private val filterMapUseCase: FilterMapUseCase by lazy { FilterMapUseCase() }
    private val houseIdUseCase: HouseIdUseCase by lazy { HouseIdUseCase() }
    private val levelRoomUseCase: LevelRoomUseCase by lazy { LevelRoomUseCase() }
    private val mapConfigUseCase: MapConfigUseCase by lazy { MapConfigUseCase() }
    private val roomAmenityUseCase: RoomAmenityUseCase by lazy { RoomAmenityUseCase() }
    private val roomLocationUseCase: RoomLocationUseCase by lazy { RoomLocationUseCase() }
    private val hotelUseCase: HotelUseCase by lazy { HotelUseCase() }
    private val parkTourUseCase: ParkTourUseCase by lazy { ParkTourUseCase() }
    private val placeUseCase: PlaceUseCase by lazy { PlaceUseCase() }
    private val roomTypeUseCase: RoomTypeUseCase by lazy { RoomTypeUseCase() }
    private val galleryUseCase: GalleryUseCase by lazy { GalleryUseCase() }
    private val webCamUseCase: WebCamUseCase by lazy { WebCamUseCase() }
    private val currencyUseCase: CurrencyUseCase by lazy { CurrencyUseCase() }
    private val paramSettingUseCase: ParamSettingUseCase by lazy { ParamSettingUseCase() }
    private val phoneCodeUseCase: PhoneCodeUseCase by lazy { PhoneCodeUseCase() }
    private val nearPlaceUseCase: NearPlaceUseCase by lazy { NearPlaceUseCase() }
    private val scheduleActivityUseCase: ScheduleActivityUseCase by lazy { ScheduleActivityUseCase() }
    private val faqUseCase: FaqUseCase by lazy { FaqUseCase() }
    private val destinationUseCase: DestinationUseCase by lazy { DestinationUseCase() }
    private val afiClassUseCase: AFIClassUseCase by lazy { AFIClassUseCase() }
    private val destinationActivityUseCase: DestinationActivityUseCase by lazy { DestinationActivityUseCase() }

    private val langActivityUseCase: LangActivityUseCase by lazy { LangActivityUseCase() }
    private val langAwardUseCase: LangAwardUseCase by lazy { LangAwardUseCase() }
    private val langCategoryUseCase: LangCategoryUseCase by lazy { LangCategoryUseCase() }
    private val langContactUseCase: LangContactUseCase by lazy { LangContactUseCase() }
    private val langFilterMapUseCase: LangFilterMapUseCase by lazy { LangFilterMapUseCase() }
    private val langRoomAmenityUseCase: LangRoomAmenityUseCase by lazy { LangRoomAmenityUseCase() }
    private val langHotelUseCase: LangHotelUseCase by lazy { LangHotelUseCase() }
    private val langParkTourUseCase: LangParkTourUseCase by lazy { LangParkTourUseCase() }
    private val langPlaceUseCase: LangPlaceUseCase by lazy { LangPlaceUseCase() }
    private val langLabelUseCase: LangLabelUseCase by lazy { LangLabelUseCase() }
    private val langRoomTypeUseCase: LangRoomTypeUseCase by lazy { LangRoomTypeUseCase() }
    private val langRestaurantDetailUseCase: LangRestaurantDetailUseCase by lazy { LangRestaurantDetailUseCase() }
    private val languageUseCase: LanguageUseCase by lazy { LanguageUseCase() }
    private val countryUseCase: CountryUseCase by lazy { CountryUseCase() }
    private val stateUseCase: StateUseCase by lazy { StateUseCase() }
    private val langTitleUseCase: LangTitleUseCase by lazy { LangTitleUseCase() }
    private val langFaqUseCase: LangFaqUseCase by lazy { LangFaqUseCase() }
    private val langDestinationUseCase: LangDestinationUseCase by lazy { LangDestinationUseCase() }
    private val langDestinationActivityUseCase: LangDestinationActivityUseCase by lazy { LangDestinationActivityUseCase() }
    private val langAFIClassUseCase: LangAFIClassUseCase by lazy { LangAFIClassUseCase() }


    private val userUseCase = UserUseCase()

    //quotes
    private val dateQuotesUseCase = DateQuotesUseCase()
    private val suiteQuotesUseCase: SuiteQuotesUseCase by lazy { SuiteQuotesUseCase() }
    private val ratePlansUseCase: SuiteRatePlansUseCase by lazy { SuiteRatePlansUseCase() }

    //reservation
    private val reservationsUseCase: ReservationsUseCase by lazy { ReservationsUseCase() }

    //private val auxWeatherUseCase = mutableListOf<WeatherUseCase>()
    //private val auxListBaseUseCase = mutableListOf<BaseUseCase<*>>()
    //private val auxListLangUseCase = mutableListOf<BaseLangUseCase<*>>()
    private val airlineTerminalUseCase: AirlineTerminalUseCase by lazy { AirlineTerminalUseCase() }

    //Transalation
    private val translatorUseCase by lazy { TranslatorUseCase() }

    init {
        auxListBaseUseCase.add(activityUseCase)
        auxListBaseUseCase.add(airlineUseCase)
        auxListBaseUseCase.add(awardUseCase)
        auxListBaseUseCase.add(categoryUseCase)
        auxListBaseUseCase.add(contactUseCase)
        auxListBaseUseCase.add(filterMapUseCase)
        auxListBaseUseCase.add(houseIdUseCase)
        auxListBaseUseCase.add(levelRoomUseCase)
        auxListBaseUseCase.add(mapConfigUseCase)
        auxListBaseUseCase.add(roomAmenityUseCase)
        auxListBaseUseCase.add(roomLocationUseCase)
        auxListBaseUseCase.add(hotelUseCase)
        auxListBaseUseCase.add(parkTourUseCase)
        auxListBaseUseCase.add(placeUseCase)
        auxListBaseUseCase.add(roomTypeUseCase)
        auxListBaseUseCase.add(languageUseCase)
        auxListBaseUseCase.add(nearPlaceUseCase)
        auxListBaseUseCase.add(webCamUseCase)
        auxListBaseUseCase.add(countryUseCase)
        auxListBaseUseCase.add(userUseCase)
        auxListBaseUseCase.add(currencyUseCase)
        auxListBaseUseCase.add(paramSettingUseCase)
        auxListBaseUseCase.add(phoneCodeUseCase)
        auxListBaseUseCase.add(reservationsUseCase)
        auxListBaseUseCase.add(scheduleActivityUseCase)
        auxListBaseUseCase.add(airlineTerminalUseCase)
        auxListBaseUseCase.add(faqUseCase)
        auxListBaseUseCase.add(destinationActivityUseCase)
        auxListBaseUseCase.add(destinationUseCase)
        auxListBaseUseCase.add(afiClassUseCase)

    }

    fun startDownload(isBasic: Boolean = false){
        downloadLanguages { response ->
            response?.let {
                resultListener.onResult(ResultDownload(false, it))
            } ?: kotlin.run {
                if(numObjectDownloaded == 0){
                    runBlocking {
                        launch {

                            val phoneLangCode = Locale.getDefault().language
                            LogHX.debugMode("phoneLangCode $phoneLangCode")
                            val langDefault = languageUseCase.getLanguage("en")
                            val langDevice = languageUseCase.getLanguage(phoneLangCode)
                            //var lang : com.xcaret.xcaret_hotel.domain.Language? = null
                            LogHX.debugMode("langDevice $langDevice")

                            if (langDevice?.isTranslated == true) {
                                if (Language.isLangChangeRequestedFromDialog(HotelXcaretApp.mContext)){
                                    val langCode = Language.getLangCode(HotelXcaretApp.mContext)
                                    val langSaved = languageUseCase.getLanguage(langCode)
                                    LogHX.debugMode("isLangChangeRequestedFromDialog $langSaved")
                                    proceedLanguageChange(langSaved,isBasic)
                                }else{
                                    LogHX.debugMode("LangDevice ${langDevice.code}")
                                    proceedLanguageChange(langDevice, isBasic)
                                }
                                return@launch
                            }else{
                                LogHX.debugMode("langDefault ${langDefault?.code}")
                                requestTranslation(phoneLangCode)
                                proceedLanguageChange(langDefault,isBasic)

                            }

//                            lang = if (Language.isNullOrEmpty(HotelXcaretApp.mContext)) {
//                                if (langDevice?.isTranslated == false) {
//                                    Language.setDeviceLangAvailable(false, HotelXcaretApp.mContext)
//                                    requestTranslation(phoneLangCode)
//                                    langDefault
//                                } else {
//                                    langDevice
//                                }
//                            }else{
//                                languageUseCase.getLanguage(Language.getLangCode(HotelXcaretApp.mContext))
//                            }
//                            proceedLanguageChange(lang,isBasic)

//                            var lang =
//                                if(com.xcaret.xcaret_hotel.view.config.Language.isNullOrEmpty(HotelXcaretApp.mContext))
//                                    languageUseCase.getLanguage(Locale.getDefault().language)
//                                else
//                                    languageUseCase.getLanguage(com.xcaret.xcaret_hotel.view.config.Language.getLangCode(HotelXcaretApp.mContext))
//
//                            if(lang == null){
//                                lang = languageUseCase.getLanguage(com.xcaret.xcaret_hotel.view.config.Language.getLangCode(HotelXcaretApp.mContext))
//                            }
//
//                            lang?.let {
//                                com.xcaret.xcaret_hotel.view.config.Language.changeLanguage(it.code!!, it.countryCode!!, it.nameSF, HotelXcaretApp.mContext){
//                                    addLangsUseCaseToList()
//                                    numObjectDownloaded++
//
//                                    if(isBasic) downloadBasicInfo()
//                                    else {
//                                        downloadInfoByUser()
//                                        downloadInfo()
//                                        downloadLangInfo()
//                                    }
//                                }
//                            }
                        }
                    }
                }
            }
        }
    }

    private fun proceedLanguageChange(lang: com.xcaret.xcaret_hotel.domain.Language?, isBasic: Boolean){
        if (lang != null) {
            Language.changeLanguage(
                lang.code!!,
                lang.countryCode!!,
                lang.nameSF,
                HotelXcaretApp.mContext) {
                addLangsUseCaseToList()
                numObjectDownloaded++
            }
            if (isBasic) {
                downloadBasicInfo()
            } else {
                downloadInfo()
                downloadLangInfo()
            }
        }
    }

    private suspend fun requestTranslation(phoneLangCode :String){
        try {
            val result = translatorUseCase.requestLanguage(phoneLangCode)
            LogHX.debugMode("StartDownload requestTranslation  result: ${result.code()}")
            //if (result.code() != 200) phoneLangCode = "en"
        }catch (e:Exception){
            LogHX.debugMode("StartDownload requestTranslation Error ${e.printStackTrace().toString()}")
        }
    }

    private fun addLangsUseCaseToList(){
        auxListLangUseCase.add(langActivityUseCase)
        auxListLangUseCase.add(langAwardUseCase)
        auxListLangUseCase.add(langCategoryUseCase)
        auxListLangUseCase.add(langContactUseCase)
        auxListLangUseCase.add(langFilterMapUseCase)
        auxListLangUseCase.add(langRoomAmenityUseCase)
        auxListLangUseCase.add(langHotelUseCase)
        auxListLangUseCase.add(langParkTourUseCase)
        auxListLangUseCase.add(langPlaceUseCase)
        auxListLangUseCase.add(langRoomTypeUseCase)
        auxListLangUseCase.add(langLabelUseCase)
        auxListLangUseCase.add(langRestaurantDetailUseCase)
        auxListLangUseCase.add(langTitleUseCase)
        auxListLangUseCase.add(langFaqUseCase)
        auxListLangUseCase.add(langDestinationUseCase)
        auxListLangUseCase.add(langDestinationActivityUseCase)
        auxListLangUseCase.add(langAFIClassUseCase)
    }

    private fun downloadLanguages(response: (result: String?) -> Unit){
        languageUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<com.xcaret.xcaret_hotel.domain.Language>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-languageUseCase", e.localizedMessage)
                response(languageUseCase::class.simpleName)
            }

            override fun onSuccess(result: MutableList<com.xcaret.xcaret_hotel.domain.Language>) {
                languageUseCase.insert(result) {
                    response(null)
                }
            }
        })
    }

    private fun downloadBasicInfo(){
        NUM_OBJECT_TO_DOWNLOAD = NUM_OBJECT_TO_DOWNLOAD_BASIC
        langLabelUseCase.getFromSingleFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangLabel>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langLabelUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangLabel>) {
                langLabelUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        countryUseCase.getFromSingleFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Country>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-countryUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<Country>) {
                countryUseCase.insert(result) {
                    if(result.isEmpty()) notifyDownload()
                    else {
                        val stateList = mutableListOf<State>()
                        result.forEach { country ->
                            if (country.states.isNotEmpty()) stateList.addAll(country.states)
                        }

                        stateUseCase.insert(stateList) {
                            notifyDownload()
                        }
                    }
                }
            }
        })

        langTitleUseCase.getFromSingleFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Title>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langTitleUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<Title>) {
                langTitleUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        paramSettingUseCase.getFromSingleFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<ParamSetting>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-paramSettingUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<ParamSetting>) {
                paramSettingUseCase.insert(result){
                    result.forEach { param -> Settings.setParam(param.code ?: "", param.value ?: "", HotelXcaretApp.mContext)}
                    notifyDownload()
                }
            }
        })

        phoneCodeUseCase.getFromSingleFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<PhoneCode>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-phoneCodeUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<PhoneCode>) {
                phoneCodeUseCase.insert(result){
                    notifyDownload()
                }
            }
        })
    }

    private fun downloadInfo(){
        activityUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Activity>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-activityUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<Activity>) {
                activityUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        airlineUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Airline>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-airlineUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<Airline>) {
                airlineUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        awardUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Award>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-awardUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<Award>) {
                awardUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        categoryUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Category>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-categoryUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<Category>) {
                categoryUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        contactUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Contact>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-contactUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<Contact>) {
                contactUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        countryUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Country>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-countryUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<Country>) {
                countryUseCase.insert(result) {
                    if(result.isEmpty()) notifyDownload()
                    else {
                        val stateList = mutableListOf<State>()
                        result.forEach { country ->
                            if (country.states.isNotEmpty()) stateList.addAll(country.states)
                        }

                        stateUseCase.insert(stateList) {
                            notifyDownload()
                        }
                    }
                }
            }
        })

        currencyUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Currency>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-currencyUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<Currency>) {
                currencyUseCase.insert(result){
                    val currency = com.xcaret.xcaret_hotel.view.config.Language.getCurrency(HotelXcaretApp.mContext)
                    LogHX.e("Actual currency", currency)
                    val default = com.xcaret.xcaret_hotel.view.config.Language.LANG_CURRENCY_ISO_DEFAULT
                    result.find { it.iso.equals(currency, ignoreCase = true) }?.let { currentCurrency ->
                        com.xcaret.xcaret_hotel.view.config.Language.setCurrency(currentCurrency, HotelXcaretApp.mContext)
                    }
                    if(currency.isEmpty() && (result.find { it.iso.equals(currency, ignoreCase = true) && it.enabled} == null)){
                        var currentCountry: Currency? = null
                        val region = Utils.getCountry()
                        result.find { it.iso?.contains(region,ignoreCase = true) == true }?.let{ found->
                            LogHX.e("Currency", found.iso)
                           currentCountry = found
                        }

                        if(currentCountry == null)
                            currentCountry = result.find { it.iso.equals(default, ignoreCase = true) }

                        currentCountry?.let { currency ->
                            com.xcaret.xcaret_hotel.view.config.Language.setCurrency(currency, HotelXcaretApp.mContext)
                        }
                        LogHX.e("Save Currency", currentCountry?.iso ?: default)
                    }
                    notifyDownload()
                }
            }
        })

        filterMapUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<FilterMap>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-filterMapUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<FilterMap>) {
                filterMapUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        houseIdUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<HouseId>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-houseIdUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<HouseId>) {
                houseIdUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        levelRoomUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LevelRoom>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-levelRoomUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LevelRoom>) {
                levelRoomUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        mapConfigUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<MapConfig>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-mapConfigUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<MapConfig>) {
                mapConfigUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        nearPlaceUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<NearPlace>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-nearPlaceUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<NearPlace>) {
                nearPlaceUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        paramSettingUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<ParamSetting>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-phoneCodeUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<ParamSetting>) {
                paramSettingUseCase.insert(result){
                    result.forEach { param -> Settings.setParam(param.code ?: "", param.value ?: "", HotelXcaretApp.mContext) }
                    notifyDownload()
                }
            }
        })

        phoneCodeUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<PhoneCode>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-phoneCodeUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<PhoneCode>) {
                phoneCodeUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        roomAmenityUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<RoomAmenity>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-roomAmenityUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<RoomAmenity>) {
                roomAmenityUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        roomLocationUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<RoomLocation>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-roomLocationUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<RoomLocation>) {
                roomLocationUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        hotelUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Hotel>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-categoryUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<Hotel>) {
                hotelUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        parkTourUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<ParkTour>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-parkTourUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<ParkTour>) {
                parkTourUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        placeUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Place>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-placeUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<Place>) {
                placeUseCase.insert(result) {
                    val placesWeather = result.filter { it.weather }
                    NUM_OBJECT_TO_DOWNLOAD = NUM_OBJECT_TO_DOWNLOAD_ORIGIN + placesWeather.size
                    notifyDownload()
                    downloadWeatherByPlace(placesWeather)
                }
            }
        })

        roomTypeUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<RoomType>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-roomTypeUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<RoomType>) {

                roomTypeUseCase.insert(result) {
                    val listGallery = mutableListOf<Gallery>()
                    result.forEach { room->
                        if (room.galleryMap != null){
                            room.galleryMap!!.forEach { (k, v) ->
                                listGallery.add(Gallery(uid = k,
                                    parentUID = room.uid,
                                    isMain = v.isMain == 1,
                                    name = v.name,
                                    order = v.order,
                                    path = v.path,
                                    enabled =  v.enabled== 1
                                ))
                            }


                        }

                    }
                    galleryUseCase.insert(listGallery,{})
                    notifyDownload()
                }
            }
        })

        webCamUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<WebCam>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-webCamUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<WebCam>) {
                webCamUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        scheduleActivityUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<ScheduleActivity>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-scheduleActivityUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<ScheduleActivity>) {
                scheduleActivityUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        airlineTerminalUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<AirlineTerminal>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-airlineTerminalUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<AirlineTerminal>) {
                airlineTerminalUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        faqUseCase.getFromFirebase(object :FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Faq>{
            override fun onSuccess(result: MutableList<Faq>) {
                faqUseCase.insert(result){
                    notifyDownload()
                }
            }

            override fun onError(e: Exception) {
                LogHX.e("$tag-faqUseCase", e.localizedMessage)
            }

        })

        afiClassUseCase.getFromFirebase(object :FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<AfiClass>{
            override fun onSuccess(result: MutableList<AfiClass>) {
                afiClassUseCase.insert(result){
                    notifyDownload()
                }
            }

            override fun onError(e: Exception) {
                LogHX.e("$tag-faqUseCase", e.localizedMessage)
            }

        })

        destinationActivityUseCase.getFromFirebase(object :FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<DestinationActivity>{
            override fun onSuccess(result: MutableList<DestinationActivity>) {
                destinationActivityUseCase.insert(result){
                    notifyDownload()
                }
            }

            override fun onError(e: Exception) {
                LogHX.e("$tag-faqUseCase", e.localizedMessage)
            }

        })

        destinationUseCase.getFromFirebase(object :FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Destination>{
            override fun onSuccess(result: MutableList<Destination>) {
                destinationUseCase.insert(result){
                    notifyDownload()
                }
            }

            override fun onError(e: Exception) {
                LogHX.e("$tag-faqUseCase", e.localizedMessage)
            }

        })
    }

    private fun downloadInfoByUser(){
        val callbackReservation = object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Reservation>{
            override fun onSuccess(result: MutableList<Reservation>) {
                reservationsUseCase.insert(result){
                    notifyDownload()
                }
            }

            override fun onError(e: Exception) {
                LogHX.e("$tag-reservationsUseCase", e.localizedMessage)
            }

        }

        val callbackUser = object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<User>{
            override fun onSuccess(result: MutableList<User>) {
                userUseCase.insert(result){
                    notifyDownload()
                }
            }

            override fun onError(e: Exception) {
                LogHX.e("$tag-userUseCase", e.localizedMessage)
            }

        }
        if(Session.isVisitor(HotelXcaretApp.mContext)){
            reservationsUseCase.getFromSingleFirebase(callbackReservation)
            userUseCase.getFromSingleFirebase(callbackUser)
        }else{
            reservationsUseCase.getFromFirebase(callbackReservation)
            userUseCase.getFromFirebase(callbackUser)
        }
    }

    private fun downloadLangInfo(){
        langActivityUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangActivity>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langActivityUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangActivity>) {
                langActivityUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        langAwardUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangAward>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langAwardUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangAward>) {
                langAwardUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        langCategoryUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangCategory>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langCategoryUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangCategory>) {
                langCategoryUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        langContactUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangContact>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langContactUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangContact>) {
                langContactUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        langFilterMapUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangFilterMap>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langFilterMapUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangFilterMap>) {
                langFilterMapUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        langRoomAmenityUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangRoomAmenity>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langRoomAmenityUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangRoomAmenity>) {
                langRoomAmenityUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        langHotelUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangHotel>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langHotelUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangHotel>) {
                langHotelUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        langParkTourUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangParkTour>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langParkTourUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangParkTour>) {
                langParkTourUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        langPlaceUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangPlace>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langPlaceUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangPlace>) {
                langPlaceUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        langRoomTypeUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangRoomType>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langRoomTypeUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangRoomType>) {
                langRoomTypeUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        langRestaurantDetailUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangRestaurantDetail>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langRoomTypeUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangRestaurantDetail>) {
                langRestaurantDetailUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        langLabelUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangLabel>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langLabelUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangLabel>) {
                langLabelUseCase.insert(result) {
                    notifyDownload()
                }
            }
        })

        langTitleUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Title>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langLabelUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<Title>) {
                langTitleUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        langFaqUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangFaq>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langFaqUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangFaq>) {
                langFaqUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        langDestinationUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangDestination>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langFaqUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangDestination>) {
                langDestinationUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        langDestinationActivityUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangDestinationActivity>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langFaqUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangDestinationActivity>) {
                langDestinationActivityUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        langAFIClassUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<LangAfiClass>{
            override fun onError(e: Exception) {
                LogHX.e("$tag-langFaqUseCase", e.localizedMessage)
            }

            override fun onSuccess(result: MutableList<LangAfiClass>) {
                langAFIClassUseCase.insert(result){
                    notifyDownload()
                }
            }
        })

        //quotes detect if date is valid
        doAsync {
            val listDates = dateQuotesUseCase.all()
            if(listDates.isEmpty()) {
                suiteQuotesUseCase.truncate()
                ratePlansUseCase.truncate()
                notifyDownload()
            }
            else{
                val format = DateUtil.DATE_FORMAT_WEATHER
                val currentDate = DateUtil.getDateByFormat(format)
                val calCurrentDate = DateUtil.convertStringToDate(currentDate, format)

                listDates.forEach { dateQuotes ->
                    dateQuotes?.let { dq ->
                        LogHX.i("CompareDate", "hotelCode: ${dq.hotelId}, arrival: ${dq.dateArrival}, departure" )
                        val arrivalDate = DateUtil.convertStringToDate(dq.dateArrival, format)
                        if(calCurrentDate.timeInMillis > arrivalDate.timeInMillis){
                            suiteQuotesUseCase.deleteByHotel(dq.hotelId)
                            ratePlansUseCase.removeByHotel(dq.hotelId)
                            dateQuotesUseCase.delete(dq)
                        }
                    }
                }
                notifyDownload()
            }
        }
    }

    private fun downloadWeatherByPlace(places: List<Place>){
        try {
            auxWeatherUseCase.forEach {
                it.getRepository()?.removeListener()
            }
            auxWeatherUseCase.clear()

            places.forEach { place ->
                val weatherUseCase = WeatherUseCase(place.uid)
                weatherUseCase.getFromFirebase(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Weather> {
                    override fun onError(e: Exception) {
                        LogHX.e("$tag-weatherUseCase", e.localizedMessage)
                    }

                    override fun onSuccess(result: MutableList<Weather>) {
                        weatherUseCase.insert(result) {
                            notifyDownload()
                        }
                    }
                })
                auxWeatherUseCase.add(weatherUseCase)
            }
        }catch (e: RuntimeException){ e.printStackTrace() }
    }

    private fun notifyDownload(){
        numObjectDownloaded ++
        LogHX.i("Downloader", "$numObjectDownloaded to $NUM_OBJECT_TO_DOWNLOAD")
        if(numObjectDownloaded == NUM_OBJECT_TO_DOWNLOAD)
            resultListener.onResult(ResultDownload(true))
    }

    companion object{
        const val NUM_OBJECT_TO_DOWNLOAD_ORIGIN = 40 //aumentar el numero de descargas, siempre uno mas porque el contador primero aumenta y luego verifica.
        const val NUM_OBJECT_TO_DOWNLOAD_BASIC = 5
        var NUM_OBJECT_TO_DOWNLOAD = NUM_OBJECT_TO_DOWNLOAD_ORIGIN

        private val auxWeatherUseCase = mutableListOf<WeatherUseCase>()
        private val auxListBaseUseCase = mutableListOf<BaseUseCase<*>>()
        private val auxListLangUseCase = mutableListOf<BaseLangUseCase<*>>()

        fun cleanListener(){
            try {
                auxListBaseUseCase.forEach { it.getRepository()?.removeListener() }
                auxWeatherUseCase.forEach { it.getRepository()?.removeListener() }
                auxListLangUseCase.forEach { it.getRepository()?.removeListener() }
            }catch (e: java.lang.Exception){
                e.printStackTrace()
            }
        }
    }

    interface ResultListener{
        fun onResult(result: ResultDownload)
    }

    class ResultDownload(
        val success: Boolean = false,
        val errorCode: String = ""
    )
}