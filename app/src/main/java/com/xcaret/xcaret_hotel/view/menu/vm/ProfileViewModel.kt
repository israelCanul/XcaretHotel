package com.xcaret.xcaret_hotel.view.menu.vm

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.MarketingCloud
import org.jetbrains.anko.doBeforeSdk
import kotlin.Exception

class ProfileViewModel: ViewModel() {

    private val userUseCase = UserUseCase()
    private val titleUseCase = LangTitleUseCase()
    private val countryUseCase = CountryUseCase()
    private val stateUseCase = StateUseCase()
    private val salesForceUseCase = SalesForceUseCase()
    private val phoneCodeUseCase: PhoneCodeUseCase by lazy { PhoneCodeUseCase() }
    val labelUseCase:LangLabelUseCase by lazy {LangLabelUseCase()  }

    val newEmail = MutableLiveData<String>()
    val oldEmailLiveData = MutableLiveData<String>()
    val userLiveData = MutableLiveData<User?>()
    val uidLiveData = MutableLiveData<String>()
    val countryLiveData = MutableLiveData<Country?>()
    val stateLiveData = MutableLiveData<State?>()
    val titleLiveData = MutableLiveData<Title?>()
    val defaultTitleLiveData = MutableLiveData<Title>()
    val activeEditProfile = MutableLiveData(false)

    fun getSession() = userUseCase.getSession(uidLiveData.value ?: "")
    fun findCountryByIso2(): LiveData<Country?> = countryUseCase.findByIso2(userLiveData.value?.country_code ?: "")
    fun findTitleByCode(lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<Title?> = titleUseCase.findByCode(userLiveData.value?.title_code ?: "", lang)
    fun findStateByAbbreviation(): LiveData<State?> = stateUseCase.findByAbbreviation(userLiveData.value?.state_code ?: "")
    fun firstTitle(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = titleUseCase.first(lang)
    fun updatePassword(currentPassword: String, newPassword: String, response: (result: Boolean) -> Unit) = userUseCase.updatePassword(currentPassword, newPassword, response)
    fun findPhoneCodeByCountry() = phoneCodeUseCase.findByCountry(countryLiveData.value?.iSO ?: "")
    fun addEmail(email: String, res: (success: Boolean) -> Unit) = userUseCase.addEmail(email, res)
    /*fun addEmail(email: String,res: (success: Boolean) -> Unit) {
        Handler().postDelayed({
            res(true)
        }, 2000)
    }*/
    fun confirmEmail(code: String, res: (success: Boolean) -> Unit) = userUseCase.confirmEmail(code, res)
    /*fun confirmEmail(code: String,res: (success: Boolean) -> Unit) {
        Handler().postDelayed({
            res(true)
        }, 1500)
    }*/
    fun resendConfirmEmail(res: (success: Boolean) -> Unit) = userUseCase.resendConfirmEmail(res)
    fun updateEmailFB(email: String, res: (success: Boolean) -> Unit) = userUseCase.updateEmailFB(email, res)

    fun getOrCreateProfileSF(response: (result: Boolean) -> Unit){
        try {
            salesForceUseCase.getOrCreateProfileSF(user = userLiveData.value!!) { user ->
                if (user.success == true) {
                    MarketingCloud.registerDataToMKTCloud(user)
                    userUseCase.updateVisitorFromSF(user, response)
                } else response(user.success == true)
            }
        }catch (e: Exception){ response(true) }
    }

    fun updateProfile(user: User, response: (result: AuthResult) -> Unit){
        val userValid = isValid(user)
        val result = AuthResult(userValidError = userValid)
        try {
            if(userValid.hasError) response(result)
            else {
                val request = user.toRequest()
                salesForceUseCase.paxProfileRQ(request = request) { response ->
                    result.success = response.SUCCESS == true
                    if(response.SUCCESS == true){
                        user.salesForceId = response.CONTACT?.element?.get(0)?.ID ?: ""
                        userUseCase.updateVisitorFromSF(user){
                            response(result)
                        }
                    } else response(result)
                }
            }
        }catch (e: Exception) {
            result.success = false
            response(result)
        }
    }

    private fun isValid(user: User): UserValidError{
        val userValidError = UserValidError()

        if(user.firstName.isEmpty()){
            userValidError.hasError = true
            userValidError.firstName = R.string.field_required
        }

        if(user.lastName.isEmpty()){
            userValidError.hasError = true
            userValidError.lastName = R.string.field_required
        }

        return userValidError
    }
}