package com.xcaret.xcaret_hotel.view.booking.vm

import android.text.InputType
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.entity.*
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PaymentViewModel: ViewModel() {

    private val paymentUseCase: PaymentUseCase by lazy { PaymentUseCase() }
    private val dateQuotesUseCase = DateQuotesUseCase()
    private val suiteQuotesUseCase = SuiteQuotesUseCase()
    private val ratePlansUseCase = SuiteRatePlansUseCase()
    val labelUseCase = LangLabelUseCase()
    private val userUseCase = UserUseCase()
    private val suiteRatePlansUseCase = SuiteRatePlansUseCase()
    private val bookingUseCase:BookingUseCase by lazy { BookingUseCase() }


    val userLiveData = MutableLiveData<User?>()
    val currentInputCardInfo = MutableLiveData<BookingCreditCardInput>()
    val listBanks = MutableLiveData<List<PaymentBanksEntity>>()
    val bankMSISelected = MutableLiveData<PaymentBanksEntity>()
    val bankInfo = MutableLiveData<PaymentBankInfoEntity>()
    val isEditing = MutableLiveData(true)
    val exit = MutableLiveData(false)
    val currentPosition = MutableLiveData(-1)
    val dateQuotesLiveData = MutableLiveData<DateQuotes?>()
    val hotelIdSelected = MutableLiveData<Int?>()
    val tabsQuotes = MutableLiveData<List<SuiteQuotes>>()
    val roomQuantity = MutableLiveData(0)
    val paxQuantity = MutableLiveData(0)
    val total = MutableLiveData(0.0)
    val widthDefaultBtnPay = MutableLiveData(0)
    val isLoading = MutableLiveData(false)
    val iAcceptTermsConditions = MutableLiveData(false)
    val hotelLive = MutableLiveData<Hotel>()
    var currentHotel = MutableLiveData<Hotel>()
    var CvvContainer = MutableLiveData<String?>("")
    var isBankFound = MutableLiveData<Boolean?>(false)
    var cardNumberFromCamera = MutableLiveData("")

    fun findDateByHotelId() = dateQuotesUseCase.allByHotelId(hotelIdSelected.value ?: 0)
    fun findSuiteQuotes(): LiveData<List<SuiteQuotes>> = suiteQuotesUseCase.allByHotelId(hotelIdSelected.value ?: 0)
    fun getSession(result: (user: User?) -> Unit){
        doAsync {
            val user = userUseCase.getUser()
            uiThread {
                result(user)
            }
        }
    }
    fun completeWithRatePlan(ready:() -> Unit){
        doAsync {
            tabsQuotes.value?.forEach { tab ->
                val rp = ratePlansUseCase.findByRoomIdRoomCodeAndHotel(tab.id, tab.suiteCodeSelected, tab.hodelCode, tab.ratePlanCode)
                rp?.let { tab.ratePlan = it }
            }
            uiThread {
                ready()
            }
        }
    }
    fun partOfThePrice(amount: Double?, decimal: Boolean): String{
        val currency = Language.getCurrency(HotelXcaretApp.mContext)
        return Utils.partOfThePrice(amount, currency, decimal)
    }

    fun banks(payment: Payment, response: (result: PaymentGenericResponse<PaymentBankEntity>) -> Unit) =
        paymentUseCase.banks(payment, response)

    fun bankInfo(bin: String, response: (result: PaymentGenericResponse<PaymentBankInfoEntity>) -> Unit) =
        paymentUseCase.bankInfo(bin, response)

    fun bankInfoV2(bin: String, response: (result: PaymentGenericResponse<PaymentBankInfoEntity>) -> Unit) =
        paymentUseCase.bankInfoV2(bin, response)

    fun booking(request: JsonObject, response: (result: PaymentGenericResponse<BookingEntity>) -> Unit) =
        paymentUseCase.booking(request, response)

    fun liveCheck(suiteQuotes: SuiteQuotes, result: (res: ResponseLiveCheck) -> Unit) =
        suiteRatePlansUseCase.liveCheck(suiteQuotes, result = result)

    fun findBank(idBank: Int, response: (bank: PaymentBanksEntity?) -> Unit){
        doAsync {
            val bank = listBanks.value?.find { it.idBank == idBank }
            bank?.let { b ->
                if(!b.bankInstallments.isNullOrEmpty()){
                    if(b.bankInstallments?.size == 1 && b.bankInstallments?.get(0)?.installments == 1)
                        bank.bankInstallments = emptyList()
                }
            }
            uiThread {
                response(bank)
            }
        }
    }

    fun getInputs(): MutableList<BookingCreditCardInput>{
        val inputs = mutableListOf<BookingCreditCardInput>()

        val input1 = BookingCreditCardInput(
            inputType = InputType.TYPE_CLASS_TEXT,
            defaultHint = (if (Language.getLangCode(HotelXcaretApp.mContext) == "es") HotelXcaretApp.mContext.getString(
                R.string.lbl_holder_name_es
            ) else HotelXcaretApp.mContext.getString(R.string.lbl_holder_name)),
            type = BookingCreditCardInputType.FULL_NAME,
            maxLenght = 100,
            actionId = EditorInfo.IME_ACTION_NEXT
        )

        val input2 = BookingCreditCardInput(
            inputType = InputType.TYPE_CLASS_NUMBER,
            defaultHint = (if (Language.getLangCode(HotelXcaretApp.mContext) == "es") HotelXcaretApp.mContext.getString(
                R.string.lbl_card_number_es
            ) else HotelXcaretApp.mContext.getString(R.string.lbl_card_number)),
            type = BookingCreditCardInputType.CARD_NUMBER,
            maxLenght = 20,
            actionId = EditorInfo.IME_ACTION_NEXT
        )

        val input3 = BookingCreditCardInput(
            inputType = InputType.TYPE_CLASS_NUMBER,
            defaultHint = HotelXcaretApp.mContext.getString(R.string.lbl_expiry_date),
            type = BookingCreditCardInputType.EXPIRY_DATE,
            maxLenght = 5,
            actionId = EditorInfo.IME_ACTION_NEXT
        )

//        val input4 = BookingCreditCardInput(
//            inputType = InputType.TYPE_CLASS_NUMBER,
//            defaultHint = HotelXcaretApp.mContext.getString(R.string.lbl_cvv),
//            type = BookingCreditCardInputType.CVV,
//            maxLenght = 4,
//            actionId = EditorInfo.IME_ACTION_DONE
//        )

        inputs.add(input2)
        inputs.add(input1)
        inputs.add(input3)
        ///inputs.add(input4)
        return inputs
    }

    fun getCvvInput ():BookingCreditCardInput{
        val input4 = BookingCreditCardInput(
            inputType = InputType.TYPE_CLASS_NUMBER,
            defaultHint = HotelXcaretApp.mContext.getString(R.string.lbl_cvv),
            type = BookingCreditCardInputType.CVV,
            maxLenght = 4,
            actionId = EditorInfo.IME_ACTION_DONE
        )
        return input4
    }

    fun deleteHotelReservation() {
        doAsync {
            val res = suiteQuotesUseCase.deleteByHotel(hotelIdSelected.value)
            val res2 = dateQuotesUseCase.deleteByHotel(hotelIdSelected.value!!)
            Log.e("Patment ViewModel", res.toString())
        }

    }

    suspend fun saveBookingAttempt(bookingAttemptInfo: BookingAttemptInfo) = withContext(Dispatchers.IO){
        bookingUseCase.saveAttemptInfo(bookingAttemptInfo)
    }

    suspend fun findAttempBySalesId(salesId: Int) :BookingAttemptInfo{
        return bookingUseCase.findAttemptBySalesId(salesId)

    }

    suspend fun clearAttemptsInfo()  = withContext(Dispatchers.IO){bookingUseCase.deleteAttempts()}
}