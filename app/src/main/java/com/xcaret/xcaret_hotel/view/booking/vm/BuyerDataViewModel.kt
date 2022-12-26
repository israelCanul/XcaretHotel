package com.xcaret.xcaret_hotel.view.booking.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Language
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class BuyerDataViewModel: ViewModel() {

    private val userUseCase: UserUseCase by lazy { UserUseCase() }
    private val titleUseCase: LangTitleUseCase by lazy { LangTitleUseCase() }
    private val countryUseCase: CountryUseCase by lazy { CountryUseCase() }
    private val stateUseCase: StateUseCase by lazy { StateUseCase() }
    private val phoneCodeUseCase: PhoneCodeUseCase by lazy { PhoneCodeUseCase() }

    //default
    val defaultTitleLiveData = MutableLiveData<Title?>()

    //current session
    val titleLiveData = MutableLiveData<Title?>()
    val countryLiveData = MutableLiveData<Country?>()
    val stateLiveData = MutableLiveData<State?>()
    val email = MutableLiveData("")

    //buyer data
    val buyerLiveData = MutableLiveData<User?>()
    val buyerTitleLiveData = MutableLiveData<Title?>()
    val buyerCountryLiveData = MutableLiveData<Country?>()
    val buyerStateLiveData = MutableLiveData<State?>()
    val isAdultLiveData = MutableLiveData(false)

    val useDataUser = MutableLiveData(false)

    fun getDefaultTitle(res:(title: Title?) -> Unit){
        doAsync {
            val default = firstTitle()
            uiThread {
                res(default)
            }
        }
    }

    fun getUserData(response: (user: User?) -> Unit){
        doAsync {
            val user = userUseCase.getUser()
            var country: Country? = null
            var state: State? = null
            var title: Title? = null
            user?.let {
                if(it.country_code.isNotEmpty())
                    country = findCountryByIso2(it.country_code)
                if(it.state_code.isNotEmpty())
                    state = findStateByAbbreviation(it.state_code)
                if(it.title_code.isNotEmpty())
                    title = findTitleByCode(it.title_code)
            }
            uiThread {
                titleLiveData.postValue(title)
                countryLiveData.postValue(country)
                stateLiveData.postValue(state)
                response(user)
            }
        }
    }

    fun findCountryByIso2(iso: String): Country? = countryUseCase.findByIso2OutLiveData(iso)
    fun findTitleByCode(code: String): Title? = titleUseCase.findByCodeOutLiveData(code)
    fun findStateByAbbreviation(iso: String): State? = stateUseCase.findByAbbreviationOutLiveData(iso)
    fun firstTitle(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = titleUseCase.firstOutLiveData(lang)
    fun findPhoneCodeByCountry(iso: String) = phoneCodeUseCase.findByCountry(iso)

    fun isValidUser(user: User): UserValidError{
        var validUser = userUseCase.userIsValid(user, false)
        val extraValid = userUseCase.userExtraInfoIsValid(user)
        if(!validUser.hasError) validUser.hasError = extraValid.hasError
        validUser.phone = extraValid.phone
        validUser.cp = extraValid.cp
        validUser.city = extraValid.city
        validUser.state = extraValid.state
        validUser.country = extraValid.country
        validUser.address = extraValid.address
        return validUser
    }
}