package com.xcaret.xcaret_hotel.view.security.vm

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.AuthProvider
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.api.CognitoApiService
import com.xcaret.xcaret_hotel.data.entity.CognitoUserDataEntity
import com.xcaret.xcaret_hotel.data.entity.CognitoValidUserEntity
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.AuthResult
import com.xcaret.xcaret_hotel.domain.ParamSetting
import com.xcaret.xcaret_hotel.domain.ProviderType
import com.xcaret.xcaret_hotel.domain.User
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.LogHX
import com.xcaret.xcaret_hotel.view.config.MarketingCloud
import com.xcaret.xcaret_hotel.view.config.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult

class LoginViewModel: ViewModel() {
    private val userUseCase = UserUseCase()
    private val salesForceUseCase = SalesForceUseCase()
    private val paramSettingUseCase = ParamSettingUseCase()
    private val securityUseCase: SecurityUseCase by lazy { SecurityUseCase() }
    val labelUseCase:LangLabelUseCase by lazy { LangLabelUseCase() }

    val redirectDestinationId = MutableLiveData(-1)
    val email = MutableLiveData("")
    val isVisitor = MutableLiveData(false)
    val emailIsConfirming = MutableLiveData(false)

    fun loginVisitor(response: (result: AuthResult) -> Unit){
        doAsync {
            val userParam = paramSettingUseCase.findParamSettingByCode(Constants.cog_user_sf_contact)
            val passParam = paramSettingUseCase.findParamSettingByCode(Constants.cog_pass_sf_contact)
            val user = User(email = userParam?.value ?: Constants.USER_DEFAULT, password = passParam?.value ?: Constants.PASS_DEFAULT, provider = ProviderType.Visitor)
            userUseCase.validateUser(
                user.email,
                user.password){ result: CognitoValidUserEntity? ->
                if(result?.SUCCESS == true){
                    userUseCase.saveIdToken(result.TOKEN){
                        userUseCase.saveUID(result.USERDATA?.SUB ?: ""){
                            userUseCase.signInWithCustomToken {
                                Session.setVisitor(it, HotelXcaretApp.mContext)
                                user.provider = ProviderType.Visitor
                                user.success = it
                                userUseCase.saveVisitor(user) {
                                    response(AuthResult(success = true))
                                }
                            }
                        }
                    }
                }else {
                    response(AuthResult(success = false))
                }
            }
        }
    }

    fun getUser(email :String, response :(result :CognitoValidUserEntity?)->Unit){
        doAsync {
            userUseCase.getUserCognito(email) { result: CognitoValidUserEntity? ->
                response(result)
            }

        }
    }

    fun login(user: User, response: (result: AuthResult) -> Unit) {
        //Login con cognito
        userUseCase.login(user) { res ->
            if (res.success) {
                userUseCase.signInWithCustomToken {
                    res.success = it
                    Session.setVisitor(false, HotelXcaretApp.mContext)
                    userUseCase.fetchUserAttributes { _user ->
                        val splitName = _user?.name?.split(" ") ?: emptyList()
                        if (user.firstName.isEmpty()) {
                            if (!_user?.firstName.isNullOrEmpty()) user.firstName =
                                _user?.firstName ?: ""
                            else if (splitName.isNotEmpty()) user.firstName = splitName[0]
                            else user.firstName = "Guest"
                        }
                        if (user.lastName.isEmpty()) {
                            if (!_user?.lastName.isNullOrEmpty()) user.lastName =
                                _user?.lastName ?: ""
                            else if (splitName.size > 1) user.firstName = splitName[1]
                            else user.lastName = "Guest"
                        }
                        if (user.name.isEmpty()) {
                            user.name = _user?.name ?: ""
                            if (user.name.isEmpty()) user.name =
                                "${user.firstName} ${user.lastName}"
                            if (user.name.isEmpty()) user.name = "Guest"
                        }
                        user.cognitoId = Session.getUID(HotelXcaretApp.mContext) ?: ""
                        viewModelScope.launch (Dispatchers.Main){ email.value = user.email }

                        MarketingCloud.registerDataToMKTCloud(user)
                        userUseCase.saveVisitor(user){
                            securityUseCase.startDownload {
                                response(res)
                            }
                        }

//                        salesForceUseCase.getOrCreateProfileSF(user = user) {
//                            if(user.success == true) {
//                                MarketingCloud.registerDataToMKTCloud(user)
//                                userUseCase.saveVisitor(user) {
//                                    securityUseCase.startDownload {
//                                        response(res)
//                                    }
//                                }
//                            }else{
//                                //res.message = "Unexpected error"
//                                res.success = false
//                                response(res)
//                            }
//                        }
                    }
                }
            } else (response(res))
        }
    }

    fun loginWithSocialWebUI(
        provider: AuthProvider,
        callingActivity: Activity,
        response: (result: AuthResult) -> Unit
    ) {
        userUseCase.loginWithSocialWebUI(provider, callingActivity) { loginResult ->
            if (loginResult.success) {
                userUseCase.signInWithCustomToken { loginFbCorrect ->
                    Session.setVisitor(false, HotelXcaretApp.mContext)
                    loginResult.success = loginFbCorrect
                    if (loginFbCorrect) {
                        userUseCase.fetchUserAttributes { userResult ->
                            userResult?.let { user ->
                                user.provider = ProviderType.fromValue(provider)

                                if(user.name.trim().isEmpty()) user.name = "Guest Guest"

                                //if (user.como_se_entero.trim().isEmpty())user.como_se_entero = Constants.COMO_SE_ENTERO_DEFAULT

                                val splitName = user.name.split(" ")
                                if(user.firstName.trim().isEmpty()){
                                    user.firstName = splitName[0]
                                }

                                if(user.lastName.trim().isEmpty()){
                                    var lastName = ""
                                    splitName.forEachIndexed { index, s ->
                                        if(index != 0) lastName += " $s"
                                    }
                                    user.lastName = lastName.trim()
                                }
                                user.cognitoId = Session.getUID(HotelXcaretApp.mContext) ?: ""
                                userUseCase.getFirebaseUser { fUser ->
                                    fUser?.let { u ->
                                        if(u.email.isNotEmpty()) user.email = u.email
                                    }
                                    email.value = user.email

                                    if(user.email.isNotEmpty()){
                                        MarketingCloud.registerDataToMKTCloud(user)
                                        userUseCase.saveVisitor(user) {
                                            securityUseCase.startDownload{
                                                response(loginResult)
                                            }
                                        }
                                    }else {
                                        userUseCase.saveVisitor(user) {
                                            securityUseCase.startDownload{
                                                response(loginResult)
                                            }
                                        }
                                    }
                                }
                            } ?: kotlin.run { response(loginResult) }
                        }
                    } else response(loginResult)
                }
            } else {
                response(loginResult)
            }
        }
    }

    fun confirmSignUp(code: String, email: String, response: (result: AuthResult) -> Unit) =
        userUseCase.confirmSignUp(code, email, response)

    fun resendCode(email: String, response: (result: String) -> Unit) =
        userUseCase.resendCode(email, response)

    fun forgotPassword(email: String, response: (result: Boolean) -> Unit) =
        userUseCase.forgotPassword(email, response)

    fun confirmForgotPassword(pass: String, code: String, response: (result: Boolean) -> Unit) =
        userUseCase.confirmForgotPassword(pass, code, response)

    fun saveRememberDate() = paramSettingUseCase.saveRememberDate()
}