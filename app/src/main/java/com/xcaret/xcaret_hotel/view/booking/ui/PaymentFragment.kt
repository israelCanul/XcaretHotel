package com.xcaret.xcaret_hotel.view.booking.ui

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.entity.BookingEntity
import com.xcaret.xcaret_hotel.data.entity.PaymentBankInstallmentEntity
import com.xcaret.xcaret_hotel.data.entity.PaymentBanksEntity
import com.xcaret.xcaret_hotel.databinding.PaymentFragmentBinding
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.booking.vm.PaymentViewModel
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticType
import com.xcaret.xcaret_hotel.view.config.analytics.beginCheckOutOrPurchase
import com.xcaret.xcaret_hotel.view.config.analytics.logInitCheckoutOrPurchaseFacebook
import com.xcaret.xcaret_hotel.view.config.inputmask.MaskedTextChangedListener
import com.xcaret.xcaret_hotel.view.config.inputview.view.InputView
import com.xcaret.xcaret_hotel.view.general.ui.DialogAlert
import com.xcaret.xcaret_hotel.view.general.ui.LoadingDialogFragment
import com.xcaret.xcaret_hotel.view.menu.ui.LegalDialogFragment
import kotlinx.android.synthetic.main.item_credit_card_info.*
import kotlinx.android.synthetic.main.payment_fragment.*
import kotlinx.android.synthetic.main.toolbar_generic.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.uiThread
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.math.hypot


class PaymentFragment : BaseFragmentDataBinding<PaymentViewModel, PaymentFragmentBinding>() {
    override fun getLayout(): Int = R.layout.payment_fragment
    override fun getViewModelClass(): Class<PaymentViewModel> = PaymentViewModel::class.java
    override fun bindViewToModel() {
        binding.viewModel = viewModel
    }

    private val inputCardAdapter = GenericAdapter<BookingCreditCardInput>(
        R.layout.item_credit_card_info
    )
    //,percentageWith = 0.85f)

    private val msiAdapter = GenericAdapter<PaymentBankInstallmentEntity>(R.layout.item_payment_msi)
    private var testError = true
    private var listener: onItemChangedListener? = null

    private val termsDialog: LegalDialogFragment by lazy {
        LegalDialogFragment.newInstance(Constants.TERMS_CONDITON)
    }

    private val loadingDialog: LoadingDialogFragment by lazy {
        LoadingDialogFragment.newInstance()
    }

    override fun setupUI() {
        viewModel.hotelIdSelected.value =
            _parentActivity?.currentHotel?.idSynxis?.toIntOrNull() ?: 0
        viewModel.currentHotel.value = _parentActivity?._viewModel?.currentHotelLive?.value
        setObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents() {
        toolbarLayout.txtToolbarTitle.text = getString(R.string.lbl_pay_title)
        toolbarLayout.txtToolbarTitle.setKey(getString(R.string.rkey_lbl_pay_title))
        Utils.fixHeightTopView(toolbarLayout)

        inputCardAdapter.addItems(viewModel.getInputs())
        rvMSI.adapter = msiAdapter
        dsvCreditCardInfo.adapter = inputCardAdapter
        setListeners()
        //setupDiscreteScroll()
        //banks()
    }

    override fun onResume() {
        super.onResume()
        Log.e("OnResume", "QuoteFragment")
        if (_parentActivity?._viewModel?.doesRequireReOrderSuites?.value == true) {
            recoverPaymentInfo()
        }
    }

    private fun setObservers() {
        viewModel.hotelLive.value = _parentActivity?._viewModel?.currentHotelLive?.value
        viewModel.getSession { user ->
            viewModel.userLiveData.value = user
        }

        viewModel.findDateByHotelId().observe(this, Observer { dq ->
            viewModel.dateQuotesLiveData.value = dq
            //banks()
        })

        viewModel.tabsQuotes.observe(this, Observer {
            viewModel.completeWithRatePlan {
                runOnUiThread {
                    var pax = 0
                    var rooms = 0
                    var total = 0.0
                    it.forEach { tabs ->
                        rooms++
                        pax += tabs.adults + tabs.children
                        total += tabs.ratePlan?.amount ?: 0.0
                        //viewModel.total.value = total
                    }
                    runOnUiThread {
                        viewModel.paxQuantity.value = pax
                        viewModel.roomQuantity.value = rooms
                        viewModel.total.value = total


                    }
                }
            }
        })

        viewModel.findSuiteQuotes().observe(this, Observer {
            viewModel.tabsQuotes.value = it
            _parentActivity?._viewModel?.bookingData?.value?.suiteQuotes?.clear()
            _parentActivity?._viewModel?.bookingData?.value?.suiteQuotes?.addAll(it)
        })

        viewModel.isEditing.observe(this, Observer {
            if (!it) showMSI()
            else {
                //dsvCreditCardInfo.smoothScrollToPosition(0)
                msiAdapter.addItems(emptyList())
            }
        })

        _parentActivity?._viewModel?.holderName?.observe(this) { holderName ->
            if (!holderName.isNullOrEmpty()) {
                setValueCardInfo(BookingCreditCardInputType.FULL_NAME, holderName)
                _parentActivity?._viewModel?.holderName?.value = ""
            }
        }
        _parentActivity?._viewModel?.expiryDate?.observe(this) { expiryDate ->
            if (!expiryDate.isNullOrEmpty()) {
                setValueCardInfo(BookingCreditCardInputType.EXPIRY_DATE, expiryDate)
                _parentActivity?._viewModel?.expiryDate?.value = ""
            }
        }
        _parentActivity?._viewModel?.cardNumber?.observe(this) { cNumber ->
            if (!cNumber.isNullOrEmpty()) {
                setValueCardInfo(BookingCreditCardInputType.CARD_NUMBER, cNumber)
                try {
                    getBankInfo(cNumber.replace(" ", "").substring(0, 8))
                    runOnUiThread {
                        cardNumber.text = getMaskCardNumber(cNumber)
                        _parentActivity?._viewModel?.cardNumber?.value = ""
                    }
                } catch (e: Exception) {
                }
            }
        }

        viewModel.total.observe(this) {
            val total = it
            if (it.toInt() != 0) {
                banks()
                //Invocacion de evento FireBase
                beginCheckOutOrPurchase(
                    AnalyticType.BEGIN_CHECKOUT,
                    _parentActivity?._viewModel?.bookingData?.value!!
                )
                logInitCheckoutOrPurchaseFacebook(
                    AnalyticType.INITIATE_CHECKOUT_FACEBOOK,
                    _parentActivity?._viewModel?.bookingData?.value!!
                )
            }


        }

    }

    private fun setValueCardInfo(cardType: BookingCreditCardInputType, value: String) {
        inputCardAdapter.getItems().find { it.type == cardType }?.let { input ->
            input.value = value
            val position = inputCardAdapter.getItems().indexOf(input)
            dsvCreditCardInfo.findViewHolderForAdapterPosition(position).let {
                val inputCvv = it?.itemView?.findViewById<InputView>(R.id.etCardInfo)
                inputCvv?.setText(value)
            }
        }

        if (cardType == BookingCreditCardInputType.CVV) {
            viewModel.CvvContainer.value = value
            inputCardAdapter.getItems().find { it.type == BookingCreditCardInputType.EXPIRY_DATE }
                .let { creditCardInput ->
                    val position = inputCardAdapter.getItems().indexOf(creditCardInput)
                    dsvCreditCardInfo.findViewHolderForAdapterPosition(position).let {
                        val inputCvv = it?.itemView?.findViewById<InputView>(R.id.etCardInfo3)
                        inputCvv?.setText(value)
                    }
                }
        }

        viewModel.cardNumberFromCamera.value = value

        if (viewModel.currentPosition.value!! == 0) inputCardAdapter.notifyDataSetChanged()
        else dsvCreditCardInfo.centerItem(0)
    }

//    private fun setupDiscreteScroll(){
//        dsvCreditCardInfo.setOrientation(DSVOrientation.HORIZONTAL)
//        dsvCreditCardInfo.setSlideOnFling(false)
//        dsvCreditCardInfo.setOverScrollEnabled(false)
//        dsvCreditCardInfo.setHasFixedSize(true)
//        dsvCreditCardInfo.setOffscreenItems(2)
//    }

    private fun banks() {
        val payment = Payment(
            Language.getCurrency(HotelXcaretApp.mContext),
            Settings.getParam(Constants.CHANNEL_ID, HotelXcaretApp.mContext).toIntOrNull() ?: 0,
            DateUtil.getDateByFormat(DateUtil.DATE_FORMAT_WEATHER),
            viewModel.dateQuotesLiveData.value?.dateArrival ?: "",
            viewModel.total.value ?: 0.0,
            //_parentActivity?._viewModel?.bookingData?.value?.buyerData?.objCountry?.iSO?.toUpperCase() ?: "",
            Session.getCountryCode(HotelXcaretApp.mContext) ?: "",
            //_parentActivity?._viewModel?.bookingData?.value?.buyerData?.objCountry?.iSO2?.toUpperCase() ?: ""
        )
        LogHX.e("getmsi_request", payment.toString())

        viewModel.banks(payment) { response ->
            LogHX.d("banks", response.toString())
            runOnUiThread { viewModel.listBanks.value = response.data?.banks }
        }
    }

    private fun getBankInfo(bin: String) {
        LogHX.d("bank_info_request", bin)
        //Session.setCounterBankInfoRequest(1,HotelXcaretApp.mContext)
        viewModel.bankInfoV2(bin) { response ->
            LogHX.d("bank_info", response.toString())
            runOnUiThread {
                viewModel.bankInfo.value = response.data
                response.data?.let { bankInfo ->

                    val cartTypCode =
                        if (!bankInfo.getCartTypCode().isNullOrBlank()) bankInfo.getCartTypCode()
                        else Constants.DEFAULT_CARD_BACKGROUND
                    viewModel.isBankFound.value =
                        !cartTypCode.contains(Constants.DEFAULT_CARD_BACKGROUND)
                    Utils.getDrawableId(
                        requireContext(),
                        "card_type_${cartTypCode}"
                    )?.let {
                        cardType.makeVisible()
                        cardType.setImageResource(it)
                    } ?: kotlin.run { cardType.makeInVisible() }
                    val layCard =
                        if (cartTypCode.equals(Constants.DEFAULT_CARD_BACKGROUND)) "card_type_bg"
                        else "card_type_bg_${cartTypCode}"

                    Utils.getDrawableId(
                        requireContext(),
                        layCard
                    )?.let {
                        layoutCard.setBackgroundResource(it)
                    }
                    Utils.getDrawableId(requireContext(), "bank_${bankInfo.IdBank}")?.let {
                        bankImage.makeVisible()
                        bankImage.setImageResource(it)
                    } ?: kotlin.run { bankImage.makeInVisible() }

                    if (bankInfo.Type.equals(Constants.CREDIT_CARD)) {
                        viewModel.findBank(bankInfo.IdBank?.toIntOrNull() ?: 0) { bank ->
                            if (bank != null) viewModel.bankMSISelected.value = bank
                            else viewModel.bankMSISelected.value =
                                PaymentBanksEntity(bankInstallments = emptyList<PaymentBankInstallmentEntity>())

                        }
                    } else {
                        viewModel.bankMSISelected.value =
                            PaymentBanksEntity(bankInstallments = emptyList<PaymentBankInstallmentEntity>())
                    }
                }
            }
        }
    }

    private fun showMSI() {
        viewModel.bankMSISelected.value?.let { bankMSI ->
            if (bankMSI.bankInstallments?.isNotEmpty() == true)
                bankMSI.bankInstallments?.get(0)?.isSelected = true
            msiAdapter.addItems(bankMSI.bankInstallments?.sortedByDescending { it.installments }?: emptyList())
        } ?: kotlin.run { msiAdapter.addItems(emptyList()) }
    }

    private fun beforeIsValid(position: Int, res: (ok: Boolean) -> Unit) {
        var isValid = true
        if (position == -1) res(isValid)
        else {
            inputCardAdapter.getItem(position)?.let { cardInput ->
                cardInput.isValid = true
                cardInput.message = null
                LogHX.d("value", cardInput.value)
                when (cardInput.type) {
                    BookingCreditCardInputType.CARD_NUMBER -> {
//                        cardInput.isValid = Utils.isValidCardNumber(cardInput.value.replace(" ", "")) &&
//                                viewModel.bankInfo.value != null
//                        cardInput.isValid =
//                            Utils.isValidCardNumber(cardInput.value.replace(" ", ""))
//                        cardInput.message =
//                            (if (Language.getLangCode(HotelXcaretApp.mContext) == "es") HotelXcaretApp.mContext.getString(
//                                R.string.error_card_number_es
//                            ) else HotelXcaretApp.mContext.getString(R.string.error_card_number))
                    }
                    BookingCreditCardInputType.EXPIRY_DATE -> {
                        cardInput.isValid = cardInput.value.length == 5
                        if (cardInput.isValid) {
                            val splitDate = cardInput.value.split("/")
                            val month = splitDate[0]
                            val year = splitDate[1]
                            cardInput.isValid = month.toIntOrNull() in 1..12
                            val currentYear = DateUtil.getDateByFormat(DateUtil.YEAR_SHORT)
                            cardInput.isValid =
                                year.toIntOrNull() ?: 0 >= currentYear.toIntOrNull() ?: 0
                        }
                        cardInput.message = getString(R.string.error_card_date)
                    }
                    BookingCreditCardInputType.CVV -> {
                        cardInput.isValid = cardInput.value.length >= 3
                        cardInput.message =
                            (if (Language.getLangCode(HotelXcaretApp.mContext) == "es") HotelXcaretApp.mContext.getString(
                                R.string.error_card_cvv_es
                            ) else HotelXcaretApp.mContext.getString(R.string.error_card_cvv))
                    }
                    else -> {
                        //cardInput.isValid = cardInput.value.isNotEmpty()
                        cardInput.message =
                            (if (Language.getLangCode(HotelXcaretApp.mContext) == "es") HotelXcaretApp.mContext.getString(
                                R.string.error_card_name_es
                            ) else HotelXcaretApp.mContext.getString(R.string.error_card_name))
                    }
                }
                isValid = cardInput.isValid
            }
            res(isValid)
        }
    }

    private fun testLoading() {
        hideSystemUI(true, true)
        animLoadingPayment {
            Handler().postDelayed({
                runOnUiThread {
                    showErrorOrSuccessAnim(testError) {
                        btnCloseAnim.makeVisible()
                        testError = true
                    }
                }
            }, 3500)
        }
    }

    private fun setListeners() {
        btnCloseAnim.onClick {
            animLoadingPayment {
                showSystemUI()
            }
        }

        btnGoMyReservations.onClick {
            animLoadingPayment {
                showSystemUI()
                navigate(R.id.action_paymentFragment_to_myReservationsFragment)
            }
        }

        btnGoQuotes.onClick {

            _parentActivity?._viewModel?.bookingData?.value?.bookingAttempt = null

            animLoadingPayment {
                showSystemUI()
                navigate(R.id.action_paymentFragment_to_quoteFragment)
            }
        }

        lblAcceptTermsConditions.onClick {
            termsDialog.show(childFragmentManager, LegalDialogFragment.TAG_TERMS)
        }

        toolbarLayout.btnToolbarBack.onClick {
            _parentActivity?.onBackPressed()
        }

        swAcceptnTerms.onCheckedChange { _, isChecked ->
            viewModel.iAcceptTermsConditions.value = isChecked
        }

        btnPay.onClick {
            //testLoading()
            if (!isCreditCardInfoComplete()) {
                showWarning(getString(R.string.rkey_lbl_mandatory_field))
                return@onClick
            }

            if (viewModel.iAcceptTermsConditions.value == false) {
                _parentActivity?.showInfoAlert(
                    getString(R.string.rkey_msg_accept_terms_conditions),
                    object : DialogAlert.onClickListener {
                        override fun onClick(v: View?, dialog: DialogFragment) {
                            dialog.dismiss()
                        }
                    }
                )
                return@onClick
            }
            if (viewModel.bankInfo.value?.IdBank == null || viewModel.bankInfo.value?.IdBank.equals("0")) {
                val codeServer = viewModel.bankInfo.value?.error?.code ?: 0
                when {
                    Utils.analizeServerCode(codeServer) == Constants.SERVICE_OFFLINE -> {
                        _parentActivity?.showAlert(R.drawable.ic_conexion_error,
                            message = getString(R.string.rkey_lbl_services_offline),
                            accept = getString(R.string.rkey_lbl_ok),
                            acceptListener = object : DialogAlert.onClickListener {
                                override fun onClick(v: View?, dialog: DialogFragment) {
                                    viewModel.isEditing.value = true
                                    dialog.dismiss()
                                }

                            }
                        )
                        return@onClick

                    }
                    Utils.analizeServerCode(codeServer) == Constants.CARD_NOT_FOUND -> {
                        _parentActivity?.showAlert(R.drawable.ic_card_error,
                            message = getString(R.string.rkey_lbl_card_info_not_available),
                            accept = getString(R.string.rkey_lbl_ok),
                            acceptListener = object : DialogAlert.onClickListener {
                                override fun onClick(v: View?, dialog: DialogFragment) {
                                    viewModel.isEditing.value = true
                                    dialog.dismiss()
                                }

                            }
                        )
                        return@onClick

                    }
                }
            }else{
                getPaymentCard()
                continuePayment()
            }

        }

        lblEditCard.onClick {
            viewModel.isEditing.value = true
            viewModel.isBankFound.value = false
            dsvCreditCardInfo.centerItem(0)
        }

        msiAdapter.setOnListItemViewClickListener(object :
            GenericAdapter.OnListItemViewClickListener {
            override fun onClick(view: View, position: Int) {
                msiAdapter.getItems().find { it.isSelected }?.isSelected = false
                msiAdapter.getItem(position)?.isSelected = true
                msiAdapter.notifyDataSetChanged()
            }

        })

        inputCardAdapter.setOnListItemViewClickListener(object :
            GenericAdapter.OnListItemViewClickListener {
            override fun onClick(view: View, position: Int) {
                if (view.id == R.id.btnShowInfo) {
                    val dialog = CVVHelDialog.newInstance()
                    dialog.show(childFragmentManager, CVVHelDialog.TAG)
                }
            }

        })

        listener =
            onItemChangedListener { viewHolder, adapterPosition ->
                runOnUiThread {
                    val inputCvv = viewHolder?.itemView?.findViewById<InputView>(R.id.etCardInfo3)
                    inputCvv?.doOnTextChanged { text, start, before, count ->
                        viewModel.CvvContainer.value = inputCvv?.text.toString()
                    }

                    if (inputCardAdapter.getItem(adapterPosition)?.type == BookingCreditCardInputType.EXPIRY_DATE) {
                        val inputField = viewModel.getCvvInput()
                        inputCvv?.hint = inputField?.defaultHint
                        inputCvv?.imeOptions = inputField?.actionId!!
                        inputCvv?.filters =
                            arrayOf<InputFilter>(InputFilter.LengthFilter(inputField.maxLenght))


                    }

                    viewHolder?.itemView?.findViewById<InputView>(R.id.etCardInfo)
                        ?.let { inputView ->

                            if (viewModel.currentPosition.value == adapterPosition) return@runOnUiThread//return@let
                            val item = inputCardAdapter.getItem(adapterPosition)

                            LogHX.d("position", "$adapterPosition")
                            inputView.requestFocus()
                            inputView.error = item?.message

                            LogHX.d(
                                "InputType",
                                "${inputView.inputType}, text: ${InputType.TYPE_CLASS_TEXT}, number: ${InputType.TYPE_CLASS_NUMBER}"
                            )
                            val inputMethodManager =
                                requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.showSoftInput(
                                inputView,
                                InputMethodManager.SHOW_IMPLICIT
                            )

                            val beforePosition =
                                if (adapterPosition == 0) -1 else if (viewModel.currentPosition.value!! > adapterPosition) adapterPosition else adapterPosition - 1

                            viewModel.currentPosition.value = adapterPosition
                            beforeIsValid(beforePosition) {
                                LogHX.d("beforeIsValid", it.toString())
                                if (it) {
                                    viewModel.currentInputCardInfo.value = item
                                    val isInputNumberCard: Boolean
                                    viewModel.currentInputCardInfo.value?.let { input ->
                                        runOnUiThread { inputView.setText(input.value) }
                                        if (input.type == BookingCreditCardInputType.CARD_NUMBER) {
                                            var hasSomeText = input.value
                                            if (viewModel.cardNumberFromCamera.value?.length!! > 0) hasSomeText =
                                                viewModel.cardNumberFromCamera.value!!
                                            hasSomeText = hasSomeText.replace(" ", "")
                                            hasSomeText =
                                                Utils.formatStringToCardFormat(hasSomeText)
                                            setPosition(inputView, hasSomeText)
                                            //viewModel.cardNumberFromCamera.value = ""
                                        } else {
                                            setPosition(inputView, input.value)
                                            //inputView.setSelection(input.value.length)
                                        }

                                        isInputNumberCard =
                                            input.type == BookingCreditCardInputType.CARD_NUMBER
                                        var mask = ""
                                        var digitsAccepteds = ""
                                        val affineFormats: MutableList<String> = ArrayList()
                                        when (input.type) {
                                            BookingCreditCardInputType.CARD_NUMBER -> {
                                                affineFormats.add("[0000] [000000] [00000]") // Extra Amex format
                                                affineFormats.add("[0000] [000000] [0000]")// Extra Diners format
                                                mask =
                                                    "[0000] [0000] [0000] [0000]"// MasterCard Primary Format
                                                digitsAccepteds = "0123456789 "
                                            }
                                            BookingCreditCardInputType.EXPIRY_DATE -> {
                                                mask = "[00]{/}[00]"
                                                digitsAccepteds = "0123456789/"
                                            }
                                            BookingCreditCardInputType.CVV -> {
                                                mask = "[0009]"
                                                digitsAccepteds = "0123456789"
                                            }
                                            else -> {
                                                mask =
                                                    "[A][--------------------]{ }[--------------------]{ }[--------------------]{ }[--------------------]"
                                                digitsAccepteds =
                                                    "qwertyuiopasdfghjklmnbvcxzQWERTYUIOPLKJHGFDSAZXCVBNM 1234567890"

                                            }
                                        }
                                        try {
                                            LogHX.d("digitsAccepted", digitsAccepteds)
                                            //inputView.keyListener = null
                                            inputView.keyListener =
                                                DigitsKeyListener.getInstance(digitsAccepteds)
                                            if (input.type == BookingCreditCardInputType.FULL_NAME) {
                                                inputView.inputType =
                                                    item?.inputType ?: InputType.TYPE_CLASS_TEXT
                                                inputView.filters = arrayOf(InputFilter.AllCaps())
                                            }
                                            if (input.type == BookingCreditCardInputType.CVV) inputView.transformationMethod =
                                                PasswordTransformationMethod()
                                            else inputView.transformationMethod = null
                                            //inputView.addTextChangedListener(null)
                                            //MaskedTextChangedListener.installOn(inputView, mask, null)
                                            MaskedTextChangedListener.installOn(
                                                inputView,
                                                mask,
                                                object : MaskedTextChangedListener.ValueListener {
                                                    override fun onTextChanged(
                                                        maskFilled: Boolean,
                                                        extractedValue: String,
                                                        formattedValue: String
                                                    ) {
                                                        LogHX.d("TAG", extractedValue)
                                                        LogHX.d("TAG", maskFilled.toString())
                                                        input.value = extractedValue
                                                        //viewModel.CvvContainer.value = inputCvv?.text.toString()
                                                        if (input.type == BookingCreditCardInputType.CARD_NUMBER) {

                                                            if (isInputNumberCard) cardNumber.text =
                                                                getMaskCardNumber(formattedValue)
                                                            if (isInputNumberCard && extractedValue.length == 6) {
                                                                if (input.type == BookingCreditCardInputType.CARD_NUMBER) {
                                                                    if (!isBinRepeated(
                                                                            extractedValue
                                                                        )
                                                                    ) {
                                                                        getBankInfo(extractedValue)
                                                                    }

                                                                }
                                                            } else if (isInputNumberCard && extractedValue.length in 14..19) {
                                                                if (!viewModel.isBankFound.value!!) {
                                                                    if (!isBinRepeated(
                                                                            extractedValue
                                                                        )
                                                                    ) getBankInfo(extractedValue)
                                                                }
//                                                        else {
//                                                            layoutCard.backgroundResource =
//                                                                R.drawable.card_type_bg
//                                                            bankImage.makeGone()
//                                                            cardType.makeGone()
//                                                        }
                                                            } else if (isInputNumberCard && extractedValue.length < 6) {
                                                                layoutCard.backgroundResource =
                                                                    R.drawable.card_type_bg
                                                                bankImage.makeGone()
                                                                cardType.makeGone()
                                                                viewModel.isBankFound.value = false
                                                            }
                                                        }
                                                    }

                                                }
                                            )
                                            //if (input.type != BookingCreditCardInputType.FULL_NAME) inputView.hint = listener.placeholder()
                                            //else inputView.hint = input.defaultHint
                                            inputView.setOnEditorActionListener(null)
                                            inputView.setOnEditorActionListener { _, actionId, _ ->
                                                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                                                    if (adapterPosition != inputCardAdapter.itemCount - 1) {
                                                        dsvCreditCardInfo.centerItem(adapterPosition + 1)
                                                    }

                                                    true
                                                } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                                                    if (viewModel.isBankFound.value!!) viewModel.isEditing.value =
                                                        false
                                                    true
                                                }
                                                false
                                            }
                                            inputCvv?.setOnEditorActionListener { _, actionId, _ ->
                                                if (actionId == EditorInfo.IME_ACTION_DONE) {

                                                    if (!viewModel.isBankFound.value!!) {
                                                        LogHX.d("Pre Request", "DONE Action")
                                                        getBankInfo(getCreditCardnumber())
                                                        viewModel.isEditing.value = false
                                                    } else {
                                                        viewModel.isEditing.value = false
                                                    }
                                                    true
                                                }
                                                false
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                } else {
                                    LogHX.d("return", "$beforePosition")
                                    dsvCreditCardInfo.centerItem(beforePosition)
                                }
                            }
                        }
                }
            }


        dsvCreditCardInfo.addOnScrollListener(scrollListner)

        lblParticipatingCards.onClick {
            val fragment = CreditCardsInfoDialog.newInstance()
            fragment.show(childFragmentManager, CreditCardsInfoDialog.TAG)
        }

        onBackPressed {
            if (loadingView.visibility == View.VISIBLE) return@onBackPressed
            if (viewModel.isEditing.value == true) {
                val currentItem = viewModel.currentPosition.value!!
                //val currentItem = dsvCreditCardInfo.currentItem
                if (currentItem > 0)
                    dsvCreditCardInfo.centerItem(currentItem - 1)
                else _parentActivity?.popBackStack()
            } else {
                _parentActivity?.showAlert(
                    title = getString(R.string.rkey_confirm_cancel_payment_title),
                    message = getString(R.string.rkey_confirm_cancel_payment_msg),
                    accept = getString(R.string.rkey_lbl_continue),
                    cancel = getString(R.string.rkey_btn_cancel),
                    acceptListener = object : DialogAlert.onClickListener {
                        override fun onClick(v: View?, dialog: DialogFragment) {
                            lifecycleScope.launch{
                                withContext(Dispatchers.IO){viewModel.clearAttemptsInfo()}
                            }
                            dialog.dismiss()
                            _parentActivity?.popBackStack()
                        }
                    },
                    cancelListener = object : DialogAlert.onClickListener {
                        override fun onClick(v: View?, dialog: DialogFragment) {
                            dialog.dismiss()
                        }
                    }
                )
            }
        }

        btnScan.onClick {
            _parentActivity?.showScanCreditCard()
        }
    }

    private fun isCreditCardInfoComplete(): Boolean {
        var spaceChecker: String = ""
        inputCardAdapter.getItems().forEach { input ->
            when (input.type) {
                BookingCreditCardInputType.FULL_NAME -> spaceChecker = input.value
                BookingCreditCardInputType.EXPIRY_DATE -> spaceChecker = input.value
                BookingCreditCardInputType.CARD_NUMBER -> spaceChecker = input.value
                //BookingCreditCardInputType.CVV -> paymentCard.cvv = input.value
            }
            if (spaceChecker.isEmpty()) return false
            if (viewModel.CvvContainer.value.isNullOrEmpty()) return false

        }
        return true
    }

    fun findLabel(key: String, result: (res: LangLabel?) -> Unit) {
        doAsync {
            val label = viewModel.labelUseCase.findLabelOutLive(key)
            uiThread {
                result(label)
            }
        }
    }

    fun showWarning(key: String) {
        findLabel(key) { label ->
            label?.let {
                runOnUiThread {
                    val snack = showSnackBar(label.value, true)
                    snack?.addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            if (!isNightEnabled()) {
                                setColorStatusBar(R.color.colorBackgroundTop)
                            } else {
                                setColorStatusBar(R.color.colorSnackStatusBar)
                            }
                        }

                        override fun onShown(sb: Snackbar?) {
                            super.onShown(sb)
                            if (!isNightEnabled()) {
                                setColorStatusBar(R.color.color9_Snack)
                            } else {
                                setColorStatusBar(R.color.colorSnackNight)
                            }
                        }
                    })
                    snack?.show()
                }
            }
        }

    }


    private fun cleanHotelInfo() {
        Session.setHotelInfoCleaned(true, HotelXcaretApp.mContext)
        Session.setPaymentInfo("",HotelXcaretApp.mContext)
        Session.setPickUpDeparture("",HotelXcaretApp.mContext)
        Session.setPickUpDeparture("",HotelXcaretApp.mContext)
        this.viewModel.deleteHotelReservation()

    }

    fun showErrorOrSuccessAnim(success: Boolean, end: () -> Unit) {
        if (success) {
            animation_view.setMinAndMaxFrame(Constants.LOADING + 1, Constants.SUCCESS)
            cleanHotelInfo()


        } else {
            animation_view.setMinAndMaxFrame(Constants.SUCCESS + 1, Constants.ERROR)

        }
        animation_view.repeatCount = 0
        animation_view.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                end()
            }

        })
    }

    fun animLoadingPayment(end: () -> Unit) {
        val show = !viewModel.isLoading.value!!
        if (show) {
            txtStatusPayment.setKey(getString(R.string.lbl_loading))
            btnCloseAnim.makeGone()
            btnGoMyReservations.makeGone()
            btnGoQuotes.makeGone()
        }
        animWidthBtnPlay(show) {
            animShowLoading(show) {
                runOnUiThread {
                    if (!show) {
                        animation_view.pauseAnimation()
                        loadingView.makeInVisible()
                    } else {
                        animation_view.setMinAndMaxFrame(0, Constants.LOADING)
                        animation_view.repeatCount = ValueAnimator.INFINITE
                        animation_view.playAnimation()
                    }
                    viewModel.isLoading.value = show
                    end()
                }
            }
        }
    }

    fun animWidthBtnPlay(contract: Boolean = true, end: () -> Unit) {
        val width =
            if (contract) resources.getDimensionPixelSize(R.dimen.resize_with_anim_pay_btn) else viewModel.widthDefaultBtnPay.value
                ?: -1
        LogHX.e("width btn pay", width.toString())
        val anim = ResizeWidthAnimation(btnPay, width)
        anim.duration = 300
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                end()
            }
        })
        btnPay.startAnimation(anim)
    }

    fun animShowLoading(show: Boolean = true, end: () -> Unit) {
        val x = btnPay.x + (btnPay.width / 2)
        val y = btnPay.y + (btnPay.height / 2)

        val startRadius = if (show) 0 else hypot(
            loadingView.width.toDouble(),
            loadingView.height.toDouble()
        ).toInt()
        val endRadius = if (show) hypot(
            loadingView.width.toDouble(),
            loadingView.height.toDouble()
        ).toInt() else 0

        val anim = ViewAnimationUtils.createCircularReveal(
            loadingView,
            x.toInt(),
            y.toInt(),
            startRadius.toFloat(),
            endRadius.toFloat()
        )
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                txtStatusPayment.setText(R.string.lbl_loading)
                loadingView.makeVisible()
            }

            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                end()
            }
        })
        anim.duration = 500
        anim.start()
    }

    fun getMaskCardNumber(formattedValue: String): String {
        var mask = ""
        for (i in formattedValue.indices) {
            val char = formattedValue[i]
            if (i <= 15) {
                if (char.isDigit()) mask += "*"
                else mask += char
            } else mask += char
        }
        return mask
    }

    fun isBinRepeated(extractedValue: String): Boolean {
        var result = false
        try {
            if (viewModel.bankInfo.value != null) {
                val bin = extractedValue.substring(0, 6)
                result = viewModel.bankInfo?.value?.Bin!!.contains(bin)
            }
        } catch (exce: Exception) {

        }
        return result
    }

    fun getCreditCardnumber(): String {
        var card = ""
        inputCardAdapter.getItems().forEach { input ->
            when (input.type) {
                BookingCreditCardInputType.CARD_NUMBER -> card = input.value
                //BookingCreditCardInputType.CVV -> paymentCard.cvv = input.value
            }
        }
        return card
    }

    fun setPosition(v: InputView, text: String) {
        try {
            v.setSelection(text.length)
        } catch (ex: java.lang.Exception) {

        }

    }

    //region updateCreditCardInfo

    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    var shouldPaginate = false
    var previousItem = -1

    val scrollListner = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }

        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndIsNotLastPage = !isLoading && !isLastPage
            val isAtLasItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBegining = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_SIZE_PAGE
            shouldPaginate = isNotLoadingAndIsNotLastPage && isAtLasItem && isNotAtBegining &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate == true) {

                isScrolling = false
            }
            //Log.e("Recylcer","Visible item $firstVisibleItemPosition")
            //Log.e("Recylcer","findFirstCompletelyVisibleItemPosition ${layoutManager.findFirstCompletelyVisibleItemPosition()}")
            if (layoutManager.findFirstCompletelyVisibleItemPosition() != -1) {
                if (previousItem != layoutManager.findFirstCompletelyVisibleItemPosition()) {
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                    listener?.itemChanged(
                        recyclerView.findViewHolderForAdapterPosition(position),
                        position
                    )
                    previousItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                }

            }
        }
    }

    fun interface onItemChangedListener {
        fun itemChanged(viewHolder: RecyclerView.ViewHolder?, position: Int)
    }


    //endregion

    //region Livecheck and Payment

    private fun liveCheck(position: Int = 0) {
        _parentActivity?._viewModel?.bookingData?.value?.suiteQuotes?.let { listSQ ->
            val sq = listSQ[position]
//            sq.suiteCodeSelected = "ff"
//            sq.ratePlanCode ="DDDDD"
            viewModel.liveCheck(sq) { res: ResponseLiveCheck ->

                runOnUiThread {
                    when (res.errorCode) {
                        StatusResponse.SERVER -> { //error server
                            Toast.makeText(
                                requireContext(),
                                "Error, verify your connection and retry",
                                Toast.LENGTH_LONG
                            ).show()
                            showErrorOrSuccessAnim(false) {
                                //btnCloseAnim.makeVisible()
                                btnGoQuotes.makeVisible()
                                txtStatusPayment.setKey(getString(R.string.rkey_lbl_error_availability))
                                _parentActivity?._viewModel?.bookingData?.value?.paymentCard?.toJson()
                                _parentActivity?._viewModel?.doesRequireReOrderSuites?.value = true
                            }
                        }
                        StatusResponse.EMPTY -> { //error livecheck
                            showErrorOrSuccessAnim(false) {
                                btnGoQuotes.makeVisible()
                                txtStatusPayment.setKey(getString(R.string.rkey_lbl_error_availability))
                                _parentActivity?._viewModel?.bookingData?.value?.paymentCard?.toJson()
                                _parentActivity?._viewModel?.doesRequireReOrderSuites?.value = true
                            }
                        }
                        else -> { //success
                            if (_parentActivity?._viewModel?.bookingData?.value?.suiteQuotes?.size ?: 1 == position + 1) {
                                makeBookingRequest()
                            } else {
                                liveCheck(position + 1)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun continuePayment() {
        if (viewModel.widthDefaultBtnPay.value == 0)
            viewModel.widthDefaultBtnPay.value = btnPay.width
        hideSystemUI(true, true)
        animLoadingPayment {
            liveCheck()

        }
    }

    private fun getPaymentCard() {
        val paymentCard = PaymentCard()
        inputCardAdapter.getItems().forEach { input ->
            when (input.type) {
                BookingCreditCardInputType.FULL_NAME -> paymentCard.holderName = input.value
                BookingCreditCardInputType.EXPIRY_DATE -> paymentCard.expireDate = input.value
                BookingCreditCardInputType.CARD_NUMBER -> paymentCard.cardNumber = input.value
                //BookingCreditCardInputType.CVV -> paymentCard.cvv = input.value
            }
            paymentCard.cvv = viewModel.CvvContainer.value!!
        }
        paymentCard.objBank = viewModel.bankInfo.value

        if (msiAdapter.getItems().isNotEmpty()) {
            msiAdapter.getItems().find { it.isSelected }?.let { msi ->
                paymentCard.msi = msi.installments ?: 0
            }
        }
        _parentActivity?._viewModel?.bookingData?.value?.paymentCard = paymentCard
    }

    private fun recoverPaymentInfo() {
        try {
            val paymentInfo = Session.getPaymentInfo(HotelXcaretApp.mContext)
            Log.e("OnResume", paymentInfo)
            if (paymentInfo.isNotEmpty()) {
                val payment: PaymentCard? = Gson().fromJson(paymentInfo, PaymentCard::class.java)
                Log.e("OnResume", "payment")
                payment?.let {
                    try {
                        setValueCardInfo(BookingCreditCardInputType.FULL_NAME, payment.holderName)
                        setValueCardInfo(BookingCreditCardInputType.EXPIRY_DATE, payment.expireDate)
                        setValueCardInfo(BookingCreditCardInputType.CARD_NUMBER, payment.cardNumber)

                        getBankInfo(payment.cardNumber.replace(" ", "").substring(0, 8))
                        runOnUiThread {
                            cardNumber.text = getMaskCardNumber(payment.cardNumber)
                            _parentActivity?._viewModel?.cardNumber?.value = ""
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        } catch (exc: Exception) {
            Log.e("OnResume", exc.toString())
        }
    }

    private fun makeBookingRequest() {
        val bookingData = _parentActivity?._viewModel?.bookingData?.value
        //val request = _parentActivity?._viewModel?.bookingData?.value?.toRequest()
        //LogHX.e("request booking", request.toString())

        val request = checkIfAttempIsNecessary()

        LogHX.e("request booking", request.toString())
        viewModel.booking(request!!) { response ->
            if (response.success) {
                response.data?.let { bookingResponse ->
                    val statusPayment = BookingCardStatus.from(
                        bookingResponse.payments?.cards?.get(0)?.status ?: -1
                    )
                    val statusHotel = BookingHotelStatus.from(
                        bookingResponse.services?.hotels?.get(0)?.status ?: -1
                    )

                    val strStatusPayment =
                        when (statusPayment) {
                            BookingCardStatus.PAYMENT_PLAN -> getString(R.string.rkey_purchase_correct)
                            BookingCardStatus.IN_PROGRESS -> getString(R.string.rkey_validating_purchase)
                            BookingCardStatus.REJECTED -> getString(R.string.rkey_error_denied_transactions)
                            BookingCardStatus.ACCEPTED -> getString(R.string.rkey_purchase_correct)
                            BookingCardStatus.DECLINED -> getString(R.string.rkey_error_denied_transactions)
                            else -> getString(R.string.rkey_lbl_error_general_error)
                        }

                    val strStatusHotel =
                        when (statusHotel) {
                            BookingHotelStatus.IN_PROGRESS -> "In progress"
                            BookingHotelStatus.CANCELLED -> "Cancelled"
                            BookingHotelStatus.CONFIRMED -> "Confirmed"
                            else -> "No confirmed"
                        }

                    val strStatus =
                        "PaymentStatus: $strStatusPayment - HotelStatus: $strStatusHotel"
                    val ok =
                        statusPayment == BookingCardStatus.ACCEPTED || statusPayment == BookingCardStatus.IN_PROGRESS || statusPayment == BookingCardStatus.PAYMENT_PLAN
                    LogHX.e("status", strStatus)

                    processAttempt(response,ok)

                    runOnUiThread {
                        bookingResponse.salesId?.let { saleId ->
                            bookingResponse.dsSalesId?.let { dsSaleId ->
                                _parentActivity?._viewModel?.bookingData?.value?.reservation =
                                    ReservationBook(dsSaleId, saleId)
                            }
                        }
                        txtStatusPayment.apply {
                            setKey(strStatusPayment)
                        }
                        showErrorOrSuccessAnim(ok) {
                            runOnUiThread {
                                if (ok) {
                                    btnCloseAnim.makeGone()
                                    btnGoMyReservations.makeVisible()
                                    beginCheckOutOrPurchase(
                                        AnalyticType.PURCHASE,
                                        _parentActivity?._viewModel?.bookingData?.value
                                            ?: Booking()
                                    )
                                    logInitCheckoutOrPurchaseFacebook(
                                        AnalyticType.PURCHASE_FACEBOOK,
                                        _parentActivity?._viewModel?.bookingData?.value!!
                                    )

                                } else {

                                    btnGoMyReservations.makeGone()
                                    //btnGoMyReservations.makeVisible()
                                    btnCloseAnim.makeVisible()
                                }

                            }
                        }
                    }
                }
            } else {
                runOnUiThread {
                    txtStatusPayment.setKey(getString(R.string.rkey_lbl_error_general_error))
                    showErrorOrSuccessAnim(false) {
                        runOnUiThread { btnCloseAnim.makeVisible() }
                    }
                }
            }
        }


    }

    private fun checkIfAttempIsNecessary(): JsonObject?{
        var request = _parentActivity?._viewModel?.bookingData?.value?.toRequest()
        val itemWithAttempt = _parentActivity?._viewModel?.bookingData?.value?.bookingAttempt
        if (itemWithAttempt != null) {
            request = _parentActivity?._viewModel?.bookingData?.value?.checkRequestAndAddAttemptIfNecessary(request!!,itemWithAttempt)
        }
        return request
    }

    private fun processAttempt(response:PaymentGenericResponse<BookingEntity>,status:Boolean){
        if (!status) {
            if(_parentActivity?._viewModel?.bookingData?.value?.bookingAttempt == null){
                val attemptInfo = BookingAttemptInfo(
                    dsSalesId = response.data?.dsSalesId,
                    dsSaleIdInsure = response.data?.dsSaleIdInsure,
                    salesId = response.data?.salesId,
                    saleIdInsure = response.data?.saleIdInsure ?: 0
                )
                _parentActivity?._viewModel?.bookingData?.value?.bookingAttempt = attemptInfo
            }

        }else
        {
            _parentActivity?._viewModel?.bookingData?.value?.bookingAttempt = null
        }
    }


    //endregion

}


