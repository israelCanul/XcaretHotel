package com.xcaret.xcaret_hotel.data.usecase

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import com.amplifyframework.auth.*
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.api.FunctionsApiService
import com.xcaret.xcaret_hotel.data.entity.CognitoValidUserEntity
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.UserRepository
import com.xcaret.xcaret_hotel.data.room.dao.BaseDao
import com.xcaret.xcaret_hotel.domain.AuthResult
import com.xcaret.xcaret_hotel.domain.User
import com.xcaret.xcaret_hotel.domain.UserValidError
import com.xcaret.xcaret_hotel.view.config.*
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.regex.Pattern
import kotlin.Exception

class UserUseCase: BaseUseCase<User>(){
    private val dao = database.userDao()
    private val repository: UserRepository by lazy {
        UserRepository(Session.getUID(HotelXcaretApp.mContext) ?: "")
    }
    private val settingManager: SettingsManager by lazy { SettingsManager.getInstance(HotelXcaretApp.mContext) }
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val functionApi = FunctionsApiService()
    private val tag = "UserUseCase"
    private val cognitoUseCase: CognitoUseCase by lazy { CognitoUseCase() }

    override fun getDao(): BaseDao<User> = dao
    override fun getRepository(): FirebaseDatabaseRepository<User> {
        repository.updateRootNode(Session.getUID(HotelXcaretApp.mContext) ?: "")
        return repository
    }

    fun validateUser(userName: String, password: String, response: (result: CognitoValidUserEntity?) -> Unit)
        = cognitoUseCase.validateUser(userName, password, response)

    fun getUserCognito(email :String, response: (result: CognitoValidUserEntity?) -> Unit)
    = cognitoUseCase.getuser(email,response)

    fun simpleLogin(user: User, response: (result: AuthResult) -> Unit){
        Amplify.Auth.signIn(user.email, user.password, {
            Log.i("$tag - login", it.toString())
            saveIdToken(""){
                response(AuthResult(it.isSignInComplete))
            }
        }, {
            val result = AuthResult(false, it.localizedMessage ?: "Error, please retry.")
            response(result)
        })
    }

    fun login(user: User, response: (result: AuthResult) -> Unit){
        val validUser = userIsValid(user, true)
        if(!validUser.hasError) {
            Amplify.Auth.signIn(user.email, user.password, {
                Log.i("$tag - login", it.toString())
                saveUID(""){
                    saveIdToken(""){
                        response(AuthResult(it.isSignInComplete))
                    }
                }
            }, {
                Log.i("$tag - login", it.toString())
                val cause = it.cause?.toString() ?: ""
                val result = AuthResult(false, it.localizedMessage ?: "Error, please retry.")
                if(cause.contains("UserNotConfirmedException"))
                    result.errorCode = ErrorCode.EmailNotConfirm
                response(result)
            })
        }else {
            response(AuthResult(false, "", validUser))
        }
    }

    fun loginWithSocialWebUI(provider: AuthProvider, callingActivity: Activity, response: (result: AuthResult) -> Unit){
        try {
            val list = Utils.getCustomTabsPackages(HotelXcaretApp.mContext)
            Amplify.Auth.signInWithSocialWebUI(
                provider,
                callingActivity,
                {
                    LogHX.i("loginWithSocialWebUI", it.toString())
                    saveUID("") {
                        saveIdToken("") {
                            response(AuthResult(true))
                        }
                    }
                },
                {
                    LogHX.e("loginWithSocialWebUI", it.toString())
                    response(AuthResult(false, it.localizedMessage ?: "Error, please retry."))
                }
            )
        }catch (ex:Exception){
            response(AuthResult(false, "Error, please retry."))
        }
    }

    fun signUp(user: User, isRequired: Boolean = false, respose: (result: AuthResult) -> Unit){
        val validUser = userIsValid(user, isLogin = false, isSignUp = true)
        if(isRequired){
            val extraValid = userExtraInfoIsValid(user)
            if(!validUser.hasError) validUser.hasError = extraValid.hasError
            validUser.phone = extraValid.phone
            validUser.cp = extraValid.cp
            validUser.city = extraValid.city
            validUser.state = extraValid.state
            validUser.country = extraValid.country
            validUser.address = extraValid.address
        }
        if(!validUser.hasError){
            val attributes: ArrayList<AuthUserAttribute> = ArrayList()
            attributes.add(AuthUserAttribute(AuthUserAttributeKey.email(), user.email))
            attributes.add(AuthUserAttribute(AuthUserAttributeKey.name(), user.name))
            attributes.add(AuthUserAttribute(AuthUserAttributeKey.familyName(), user.lastName))
            attributes.add(AuthUserAttribute(AuthUserAttributeKey.middleName(), user.firstName))
            //attributes.add(AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), user.phone))
            attributes.add(AuthUserAttribute(AuthUserAttributeKey.picture(), ""))
            attributes.add(AuthUserAttribute(AuthUserAttributeKey.address(), user.address))

            Amplify.Auth.signUp(
                user.email,
                user.password,
                AuthSignUpOptions.builder().userAttributes(attributes).build(),
                {
                    respose(AuthResult(true))
                },
                {error ->
                    respose(AuthResult(false, error.localizedMessage ?: ""))
                }
            )
        }else {
            respose(AuthResult(false, "", validUser))
        }
    }

    fun resendCode(email: String, response: (result: String) -> Unit){
        Amplify.Auth.resendSignUpCode(email,
            { response("true") },
            { error ->
                val ex = error.javaClass.canonicalName
                if (ex.contains("LimitExceededException")){
                    response("limitExceed")
                }else{
                    response("false")
                }

                Log.e("resendCode",ex)
            }
        )
    }

    fun forgotPassword(email: String, response: (result: Boolean) -> Unit){
        Amplify.Auth.resetPassword(email,
            {
                response(true)
            },
            {
                response(false)
            }
        )
    }

    fun confirmForgotPassword(pass: String, code: String, response: (result: Boolean) -> Unit){
        Amplify.Auth.confirmResetPassword(pass, code,
            { response(true) },
            { response(false) }
        )
    }

    fun updatePassword(currentPassword: String, newPassword: String, response: (result: Boolean) -> Unit){
        Amplify.Auth.updatePassword(currentPassword, newPassword,
            { response(true) },
            {
                LogHX.e("updatePassword", it.localizedMessage)
                response(false)
            }
        )
    }

    fun confirmSignUp(code: String, email: String, response: (result: AuthResult) -> Unit){
        if(!code.isEmpty()){
            Amplify.Auth.confirmSignUp(
                email,
                code,
                { success ->
                    LogHX.d(tag, success.toString())
                    response(AuthResult(success.isSignUpComplete))
                },
                { error ->
                    LogHX.d(tag, error.toString())
                    response(AuthResult(false, error.localizedMessage ?: "Error, please retry."))
                }
            )
        }else{
            val userValidError = UserValidError()
            userValidError.hasError = true
            userValidError.code = R.string.field_required
            response(AuthResult(false, "", userValidError))
        }
    }

    fun fetchUserAttributes(response: (result: User?) -> Unit){
        Amplify.Auth.fetchUserAttributes(
            {attributes ->
                val user = User()

                attributes.forEach { attr ->
                    when(attr.key){
                        AuthUserAttributeKey.familyName() -> user.lastName = attr.value
                        AuthUserAttributeKey.name() -> user.name = attr.value
                        AuthUserAttributeKey.nickname() -> { if(user.name.isNullOrEmpty()) user.name = attr.value }
                        AuthUserAttributeKey.email() -> user.email = attr.value
                        AuthUserAttributeKey.phoneNumber() -> user.phone = attr.value
                        AuthUserAttributeKey.middleName() -> user.firstName = attr.value
                        //AuthUserAttributeKey.picture() -> user.picture = getUrlImageFromJson(attr.value)
                    }
                }
                LogHX.i(TAG, "User attributes = $attributes")
                response(user)
            },
            {error ->
                LogHX.e(TAG, error.localizedMessage)
                response(null)
            }
        )
    }

    fun addEmail(email: String, res: (success: Boolean) -> Unit){
        Amplify.Auth.updateUserAttribute(
            AuthUserAttribute(AuthUserAttributeKey.email(), email),
            { res( true)},
            { res(false)}
        )
    }

    fun confirmEmail(code: String, res: (success: Boolean) -> Unit){
        Amplify.Auth.confirmUserAttribute(
            AuthUserAttributeKey.email(), code,
            { res( true)},
            { res(false)}
        )
    }

    fun resendConfirmEmail(res: (success: Boolean) -> Unit){
        Amplify.Auth.resendUserAttributeConfirmationCode(
            AuthUserAttributeKey.email(),
            { res(true) },
            { res(false) }
        )
    }

    fun updateProfileImage(pathImage: String, response: (result: Boolean) -> Unit){
        val userPicture = AuthUserAttribute(AuthUserAttributeKey.picture(), pathImage)
        Amplify.Auth.updateUserAttribute(userPicture,
            {success ->
                if(success.isUpdated){
                    getRepository().getReference().updateChildren(mapOf("picture" to pathImage))
                        .addOnSuccessListener {
                            doAsync {
                                dao.updateProfilePicture(url = pathImage)
                                uiThread { response(true) }
                            }
                        }
                        .addOnFailureListener { response(false) }
                }
            },
            { response(false)}
        )
    }

    private fun saveIdToken(response: (accessToken: String) -> Unit) {
        Amplify.Auth.fetchAuthSession({success ->
            val cognitoAuthSession = success as AWSCognitoAuthSession
            val token = cognitoAuthSession.userPoolTokens.value?.idToken ?: ""
            LogHX.i("idToken", token)
            response(token)
        }, { _ ->
            response("") }
        )
    }

    private fun getAmplifyUserId(response: (userId: String) -> Unit){
        Amplify.Auth.fetchAuthSession({success ->
            val cognitoAuthSession = success as AWSCognitoAuthSession
            val token = cognitoAuthSession.userSub.value ?: ""
            response(token)
        }, {
            response(Session.getUID(HotelXcaretApp.mContext) ?: "") }
        )
    }

    fun saveIdToken(token: String = "", ok: () -> Unit){
        if(token.isEmpty()){
            saveIdToken{ _token ->
                Session.setToken(_token, HotelXcaretApp.mContext)
                ok()
            }
        }else {
            Session.setToken(token, HotelXcaretApp.mContext)
            ok()
        }
    }

    fun saveUID(uid: String = "", ok: () -> Unit){
        if(uid.isEmpty()){
            getAmplifyUserId { _uid ->
                Session.setUID(_uid, HotelXcaretApp.mContext)
                runBlocking { launch { settingManager.setUID(_uid) } }
                ok()
            }
        }else {
            Session.setUID(uid, HotelXcaretApp.mContext)
            runBlocking { launch { settingManager.setUID(uid) } }
            ok()
        }
    }

    fun signInWithCustomToken(response: (result: Boolean) -> Unit){
        Session.getUID(HotelXcaretApp.mContext)?.let{ userId ->
            if(userId.isEmpty()) response(false)
            else{
                getFirebaseCustomToken(userId){token ->
                    if(token.isEmpty()) response(false)
                    else{
                        auth.signInWithCustomToken(token)
                            .addOnCompleteListener {task ->
                                response(task.isSuccessful)
                            }
                    }
                }
            }
        }
    }

    fun signOut(result: (status: Boolean) -> Unit){
        try {
            Amplify.Auth.signOut(
                AuthSignOutOptions.builder().globalSignOut(true).build(),
                {
                    auth.signOut()
                    Session.setUID("", HotelXcaretApp.mContext)
                    runBlocking { launch { settingManager.setUID("") } }
                    result(true)
                }, {
                    result(false)
            })
        }catch (e: Exception){
            result(false)
        }
    }

    fun getFirebaseUser(): FirebaseUser? {
        return auth.currentUser
    }

    @SuppressLint("CheckResult")
    fun getFirebaseCustomToken(userId: String, response: (token: String) -> Unit){
        val request = JsonObject()
        request.addProperty("uid", userId)

        try {
            functionApi
                .createCustomToken(request)
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.computation())
                .subscribeWith(object: DisposableSingleObserver<JsonObject>(){
                    override fun onError(e: Throwable) {
                        LogHX.e(tag, e.toString())
                        response("")
                    }

                    override fun onSuccess(t: JsonObject) {
                        LogHX.d(tag, t.toString())
                        response(t.get("token").asString)
                    }
                })
        }catch (e: Exception){
            LogHX.e(tag, e.toString())
            response("")
        }
    }

    fun saveVisitor(user: User, response: (success: Boolean) -> Unit){
        try {
            getRepository().addSingleListener(object :
                FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<User> {
                override fun onSuccess(result: MutableList<User>) {
                    val fUser = if(result[0].email.isEmpty()) null else result[0]
                    completeInfo(fUser, user) { u ->
                        LogHX.i("useToSave", u.toMap().toString())
                        getRepository().add(u.toMap())
                        response(true)
                    }
                }

                override fun onError(e: Exception) {
                    response(true)
                }
            })
        }catch (e: Exception) {
            response(true)
        }
    }

    fun getFirebaseUser(response: (user: User?) -> Unit){
        try {
            getRepository().addSingleListener(object :
                FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<User> {
                override fun onError(e: Exception) {
                    LogHX.e("getFirebaseUser", e.toString())
                    response(null)
                }

                override fun onSuccess(result: MutableList<User>) {
                    if(result.isNotEmpty()) response(result[0])
                    else response(null)
                }
            })
        }catch (e: Exception){
            response(null)
        }
    }

    fun updateEmailFB(email: String, response: (result: Boolean) -> Unit){
        getRepository().getReference().child("email").setValue(email)
            .addOnSuccessListener { response(true) }
            .addOnFailureListener { response(false) }
    }

    fun updateVisitorFromSF(user: User, response: (result: Boolean) -> Unit){
        LogHX.e("updateFirebase", user.toMapUpdate().toString())
        getRepository().getReference().updateChildren(user.toMapUpdate())
            .addOnSuccessListener { response(true) }
            .addOnFailureListener { response(false) }
    }

    fun completeInfo(fUser: User?, newUser: User, complete: (u: User) -> Unit){
        newUser.platform = "Android"
        newUser.registered = DateUtil.getDateByFormat(DateUtil.DATE_TIME_FORMAT)
        newUser.device = "${Build.MANUFACTURER} ${Build.MODEL}"
        newUser.lang = Language.getLangCode(HotelXcaretApp.mContext)
        newUser.macAddress = Utils.macAddr
        newUser.version = Build.VERSION.RELEASE
        newUser.cognitoId = Session.getUID(HotelXcaretApp.mContext) ?: ""
        newUser.language = Language.getLangNameSF(HotelXcaretApp.mContext)

        fUser?.let { u ->
            newUser.registered = u.registered
            newUser.platform = u.platform
            newUser.device = u.device
            newUser.version = u.version
            if(newUser.state_code.isEmpty()) newUser.state_code = u.state_code
            if(newUser.country_code.isEmpty()) newUser.country_code = u.country_code
            if(newUser.title_code.isEmpty()) newUser.title_code = u.title_code
            if(newUser.city.isEmpty()) newUser.city = u.city
            if(newUser.cp.isEmpty()) newUser.cp = u.cp
            if(newUser.picture.isEmpty()) newUser.picture = u.picture
            if(newUser.salesForceId.isEmpty()) newUser.salesForceId = u.salesForceId
            if(u.language.isEmpty()) newUser.language = Language.getLangNameSF(HotelXcaretApp.mContext)
            else newUser.language = u.language
        }

        repository.getToken {
            newUser.token = it ?: ""
            complete(newUser)
        }
    }

    fun isLoginActiveCognito(response: (result: Boolean) -> Unit){
        getAmplifyUserId { userId ->
            response(userId.trim().isNotEmpty())
        }
    }

    fun isLoginActiveFirebase(response: (result: Boolean) -> Unit){
        response(getFirebaseUser() != null)
    }

    fun isSessionActive(response: (result: Boolean) -> Unit){
        isLoginActiveCognito { isLoginCognito ->
            var result = false
            if(isLoginCognito){
                isLoginActiveFirebase {isLoginFirebase ->
                    result = isLoginFirebase
                    saveIdToken { idToken ->
                        Session.setToken(idToken, HotelXcaretApp.mContext)
                        response(result)
                    }
                }
            }else response(result)
        }
    }

    fun userExtraInfoIsValid(user: User): UserValidError{
        val userValidError = UserValidError()

        if(user.address.isEmpty()){
            userValidError.hasError = true
            userValidError.address = R.string.field_required
        }

        if(user.country_code.isEmpty()){
            userValidError.hasError = true
            userValidError.country = R.string.field_required
        }

        if(user.state_code.isEmpty()){
            userValidError.hasError = true
            userValidError.state = R.string.field_required
        }

        if(user.city.isEmpty()){
            userValidError.hasError = true
            userValidError.city = R.string.field_required
        }

        if(user.cp.isEmpty()){
            userValidError.hasError = true
            userValidError.cp = R.string.field_required
        }

        if(user.phone.isEmpty()){
            userValidError.hasError = true
            userValidError.phone = R.string.field_required
        }

        return userValidError
    }

    fun userIsValid(user: User, isLogin: Boolean, isSignUp: Boolean = false): UserValidError{
        val userValidError = UserValidError()

        if(user.firstName.isEmpty() && !isLogin){
            userValidError.hasError = true
            userValidError.firstName = R.string.field_required
        }

        if(user.lastName.isEmpty() && !isLogin){
            userValidError.hasError = true
            userValidError.lastName = R.string.field_required
        }

        if(!user.email.validWithRegex(Patterns.EMAIL_ADDRESS.pattern()) && (isLogin || isSignUp)){
            userValidError.hasError = true
            userValidError.email = R.string.field_email_error
        }

        if(!user.password.validWithRegex(REGEX_PASS) && (isLogin || isSignUp)){
            userValidError.hasError = true
            userValidError.password = R.string.field_password_error
        }

        if((user.confirmPassword.trim().isEmpty() || user.confirmPassword.trim() != user.password.trim()) && isSignUp){
            userValidError.hasError = true
            userValidError.confirmPassword = R.string.field_confirm_password_error
        }
        return userValidError
    }

    fun isValidAddEmail(email: String, confirmEmail: String): UserValidError{
        val userValidError = UserValidError()

        if(!email.validWithRegex(Patterns.EMAIL_ADDRESS.pattern())){
            userValidError.hasError = true
            userValidError.email = R.string.field_email_error
        }

        if(email != confirmEmail){
            userValidError.hasError = true
            userValidError.confirmEmail = R.string.field_confirm_email_error
        }

        return userValidError
    }

    //region room
    fun getSession(uid: String = Session.getUID(HotelXcaretApp.mContext) ?: ""): LiveData<User?> = dao.getSession()
    fun getUser(uid: String = Session.getUID(HotelXcaretApp.mContext) ?: ""): User? = dao.getUser()
    fun getUserLive(uid: String = Session.getUID(HotelXcaretApp.mContext) ?: ""): LiveData<User?> = dao.getUserLive()

    //endregion

    companion object{
        const val TAG = "UserUseCase"
        const val REGEX_PASS = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=-_])(?=\\S+$).{6,}$"
    }
}

fun String.validWithRegex(regex: String): Boolean{
    if(isNullOrEmpty()) return false
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}