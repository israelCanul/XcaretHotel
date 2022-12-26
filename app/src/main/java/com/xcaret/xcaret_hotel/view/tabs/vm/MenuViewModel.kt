package com.xcaret.xcaret_hotel.view.tabs.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.LogHX
import com.xcaret.xcaret_hotel.view.config.Settings
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MenuViewModel: ViewModel(){

    private val userUseCase = UserUseCase()
    private val contactUseCase = ContactUseCase()
    private val suiteQuotesUseCase: SuiteQuotesUseCase by lazy { SuiteQuotesUseCase() }
    private val dateQuotesUseCase: DateQuotesUseCase by lazy { DateQuotesUseCase() }
    private val ratePlansUseCase: SuiteRatePlansUseCase by lazy { SuiteRatePlansUseCase() }
    private val labelUseCase = LangLabelUseCase()

    val contactLiveData = MutableLiveData<Contact?>()
    val userLiveData = MutableLiveData<User>()
    val uidLiveData = MutableLiveData<String>()
    val changedTheme = MutableLiveData(false)
    val xcaretAppIsInstalled = MutableLiveData(false)
    var previousText =  MutableLiveData("")
    val hotelLive = MutableLiveData<Hotel?>()

    fun signOut(result: (status: Boolean) -> Unit){
        userUseCase.signOut { success ->
            doAsync {
                suiteQuotesUseCase.truncate()
                dateQuotesUseCase.truncate()
                ratePlansUseCase.truncate()
                uiThread { result(success) }
            }
        }
    }

    fun findContactWriteUse(): LiveData<Contact?> = contactUseCase.findByHotelAndType(Settings.getHotelUIDSelected(HotelXcaretApp.mContext), ContactType.EMAIL.value)
    fun getSession() = userUseCase.getSession(uidLiveData.value ?: "")
    fun findAboutUs(): LiveData<LangLabel?> = labelUseCase.findLabel(
        HotelXcaretApp.mContext.getString(
        R.string.rkey_profile_about))
}