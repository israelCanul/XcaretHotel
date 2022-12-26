package com.xcaret.xcaret_hotel.view.general.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.salesforce.marketingcloud.analytics.e
import com.xcaret.xcaret_hotel.BuildConfig
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.entity.SalesForceGetProfileEntity
import com.xcaret.xcaret_hotel.data.usecase.SalesForceUseCase
import com.xcaret.xcaret_hotel.data.usecase.SplashUseCase
import com.xcaret.xcaret_hotel.data.usecase.UserUseCase
import com.xcaret.xcaret_hotel.domain.User
import com.xcaret.xcaret_hotel.view.config.DateUtil
import com.xcaret.xcaret_hotel.view.config.Session
import com.xcaret.xcaret_hotel.view.config.SettingsManager
import kotlinx.coroutines.flow.Flow
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SplashViewModel: ViewModel() {

    private val userUseCase = UserUseCase()
    private val settingsManager = SettingsManager.getInstance(HotelXcaretApp.mContext)
    private val splashUseCase: SplashUseCase by lazy { SplashUseCase(downloadListener) }
    private val salesForceUseCase :SalesForceUseCase by lazy {SalesForceUseCase()}

    private val downloadListener = object: SplashUseCase.ResultListener{
        override fun onResult(result: SplashUseCase.ResultDownload) {
            resultDownload.postValue(result)
        }
    }

    val bgImage = "bg_splash"
    val txtFooter: String by lazy {
        HotelXcaretApp.mContext.resources?.getString(R.string.footer, DateUtil.getDateByFormat(DateUtil.YEAR),  BuildConfig.VERSION_NAME) ?: ""
    }

    val splashAnimEnd = MutableLiveData(false)
    val resultDownload = MutableLiveData<SplashUseCase.ResultDownload?>()
    val isSessionActive = MutableLiveData(false)
    val resultAppUpdate: Flow<Int> = settingsManager.resultAppUpdate

    fun isSessionActive(result: (status: Boolean) -> Unit){
        userUseCase.isSessionActive(result)
    }

    fun getUser(result: (user: User?) -> Unit){
        doAsync {
            val user = userUseCase.getUser()
            uiThread {
                result(user)
            }
        }
    }

    fun getPaxProfileExternalId(externalId: String,
                                res: (success: SalesForceGetProfileEntity) -> Unit) = salesForceUseCase.
    getpaxprofilerqExternalId(request = externalId,
        result = res
    )

    fun createPaxProfileByExternalId(user: User?,
                                res: (success: User?) -> Unit) = salesForceUseCase.
    getOrCreateProfileSFByExternalId(user = user, response = res)

    fun createPaxProfile(user: User,
                                     res: (success: User?) -> Unit) = salesForceUseCase.
    getOrCreateProfileSF(user = user, response = res)

    fun load(basic: Boolean = false){
        SplashUseCase.cleanListener()
        splashUseCase.startDownload(basic)
    }


}