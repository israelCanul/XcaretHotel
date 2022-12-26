package com.xcaret.xcaret_hotel.view.security.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.api.CognitoApiService
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.Language
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SignUpViewModel: ViewModel(){
    private val userUseCase = UserUseCase()
    private val salesForceUseCase = SalesForceUseCase()
    private val titleUseCase = LangTitleUseCase()
    private val paramSettingUseCase = ParamSettingUseCase()
    private val labelUseCase = LangLabelUseCase()
    private val phoneCodeUseCase: PhoneCodeUseCase by lazy {
        PhoneCodeUseCase()
    }
    private val securityUseCase: SecurityUseCase by lazy { SecurityUseCase() }

    val redirectDestinationId = MutableLiveData(-1)
    val email = MutableLiveData("")
    val titleSelected = MutableLiveData<Title>()
    val countrySelected = MutableLiveData<Country>()
    val stateSelected = MutableLiveData<State>()
    val isRequired = MutableLiveData(false)
    val isSessionActive = MutableLiveData(false)

    fun findLabel(key:String) = labelUseCase.findLabel(key)

    fun signUp(user: User, isRequired: Boolean = false, response: (result: AuthResult) -> Unit){
        userUseCase.signUp(user, isRequired = isRequired, response)
    }

    fun login(user: User, response: (result: AuthResult) -> Unit){
        userUseCase.login(user) { res ->
            if(res.success){
                userUseCase.signInWithCustomToken {
                    Session.setVisitor(false, HotelXcaretApp.mContext)
                    res.success = it
                    user.cognitoId = Session.getUID(HotelXcaretApp.mContext) ?: ""
                    salesForceUseCase.getProfile(user.email) {
                        if (it.success == true){
                            userUseCase.saveVisitor(user) {
                                securityUseCase.startDownload {
                                    response(res)
                                }
                            }
                        }else {
                            salesForceUseCase.getOrCreateProfileSF(user = user) {
                                if (user.success== true) {
                                    MarketingCloud.registerDataToMKTCloud(user)
                                    userUseCase.saveVisitor(user) {
                                        securityUseCase.startDownload {
                                            response(res)
                                        }
                                    }
                                }else{
                                    res.success = false
                                    response(res)
                                }
                            }
                        }
                    }
                }
            } else(response(res))
        }
    }

    fun confirmSignUp(code: String, email: String, response: (result: AuthResult) -> Unit){
        userUseCase.confirmSignUp(code, email, response)
    }

    fun resendCode(email: String, response: (result: String) -> Unit) = userUseCase.resendCode(email, response)
    fun firstTitle(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = titleUseCase.first(lang)
    fun findPhoneCodeByCountry(): PhoneCode? = phoneCodeUseCase.findByCountry(countrySelected.value?.iSO ?: "")
    fun saveRememberDate() = paramSettingUseCase.saveRememberDate()
    fun isSessionActive(result: (status: Boolean) -> Unit){
        userUseCase.isSessionActive(result)
    }
}