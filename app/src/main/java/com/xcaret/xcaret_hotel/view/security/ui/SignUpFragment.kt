package com.xcaret.xcaret_hotel.view.security.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.SignUpFragmentBinding
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticType
import com.xcaret.xcaret_hotel.view.config.analytics.loginOrSignUp
import com.xcaret.xcaret_hotel.view.config.inputview.view.toggleVisiblePassword
import com.xcaret.xcaret_hotel.view.general.ui.LoadingDialogFragment
import com.xcaret.xcaret_hotel.view.menu.ui.DropDownCountryFragment
import com.xcaret.xcaret_hotel.view.menu.ui.DropDownStateFragment
import com.xcaret.xcaret_hotel.view.menu.ui.DropDownTitleFragment
import com.xcaret.xcaret_hotel.view.security.vm.SignUpViewModel
import kotlinx.android.synthetic.main.payment_fragment.*
import kotlinx.android.synthetic.main.sign_up_fragment.*
import kotlinx.android.synthetic.main.toolbar_generic.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.uiThread

class SignUpFragment: BaseFragmentDataBinding<SignUpViewModel, SignUpFragmentBinding>(){
    override fun getLayout(): Int = R.layout.sign_up_fragment
    override fun getViewModelClass(): Class<SignUpViewModel> = SignUpViewModel::class.java
    override fun bindViewToModel() {
        binding.viewModel = viewModel
    }

    private val dialogEnterCode: DialogEnterCode by lazy {
        DialogEnterCode.newInstance()
    }

    private val loadingDialog: LoadingDialogFragment by lazy {
        LoadingDialogFragment.newInstance()
    }

    private val titleDialog: DropDownTitleFragment<Title> by lazy {
        DropDownTitleFragment.newInstance("")
    }

    private val countryDialog: DropDownCountryFragment<Country> by lazy {
        DropDownCountryFragment.newInstance("")
    }

    override fun setupUI() {
        viewModel.isRequired.value = arguments?.getBoolean(Constants.IS_VISTOR, false) ?: false
        viewModel.redirectDestinationId.value = arguments?.getInt(Constants.REDIRECT_DESTINATION_ID, -1) ?: -1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents(){
        toolbar.txtToolbarTitle.text = getString(R.string.signup_title)
        toolbar.txtToolbarTitle.setKey(getString(R.string.rkey_signup_title))
        Utils.fixHeightTopView(toolbar)
        setObservers()
        setListeners()
    }

    private fun setObservers(){
        viewModel.isSessionActive {
            runOnUiThread { viewModel.isSessionActive.value = it }
        }
        viewModel.titleSelected.observe(viewLifecycleOwner, Observer {
            it?.let { title -> txtTitle.setText(title.name) }
        })

        viewModel.countrySelected.observe(viewLifecycleOwner, Observer {
            it?.let { country ->
                etCountry.setText(country.name)
                loadPhoneCode()
            }
        })

        viewModel.stateSelected.observe(viewLifecycleOwner, Observer {
            it?.let { state ->  etState.setText(state.name) }
        })

        viewModel.firstTitle().observe(viewLifecycleOwner, Observer {
            it?.let { title -> viewModel.titleSelected.value = title }
        })
    }

    private fun loadPhoneCode(){
        doAsync {
            val phoneCode = viewModel.findPhoneCodeByCountry()
            uiThread {
                phoneCode?.let { pc ->
                    pc.code?.let { txtPhoneCode.text = "+$it" }
                }
            }
        }
    }

    private fun login(){
        val user = getUser()
        viewModel.login(user){
            analyzeLoginResponse(it)
        }
    }

    private fun goToLogin(){
        val args = bundleOf(
            Constants.CLEAR_STACK to (viewModel.isSessionActive.value == true || Session.isVisitor(HotelXcaretApp.mContext))
        )
        navigate(R.id.action_signUpFragment_to_loginFragment, args)
    }

    fun setListeners(){
        toolbar.btnToolbarBack.onClick {
            _parentActivity?.onBackPressed()
        }


        btnCancel.onClick {
            goToLogin()
        }

        layoutTitle.onClick {
            titleDialog.show(childFragmentManager, DropDownTitleFragment.TAG)
        }

        layoutCountry.onClick {
            countryDialog.show(childFragmentManager, DropDownCountryFragment.TAG)
        }

        btnShowPassword.onClick {
            etPassword.toggleVisiblePassword(btnShowPassword)
        }

        btnShowConfirmPassword.onClick {
            etConfirmPassword.toggleVisiblePassword(btnShowConfirmPassword)
        }

        layoutState.onClick {
            viewModel.countrySelected.value?.let { country ->
                val dialogState = DropDownStateFragment.newInstance("", country.id ?: -1)
                dialogState.addListener(object: DropDownListener<State>{
                    override fun onSelectedItem(item: State) {
                        dialogState.dismiss()
                        viewModel.stateSelected.value = item
                    }
                })
                dialogState.show(childFragmentManager, DropDownStateFragment.TAG)
            }
        }

        titleDialog.addListener(object: DropDownListener<Title> {
            override fun onSelectedItem(item: Title) {
                titleDialog.dismiss()
                viewModel.titleSelected.value = item
            }
        })

        countryDialog.addListener(object: DropDownListener<Country>{
            override fun onSelectedItem(item: Country) {
                countryDialog.dismiss()
                viewModel.countrySelected.value = item
            }
        })

        dialogEnterCode.setEnterCodeListener(object: DialogEnterCode.EnterCodeListener{
            override fun onConfirmCode(code: String) {
                loadingDialog.show(childFragmentManager, LoadingDialogFragment.TAG)
                viewModel.confirmSignUp(code, viewModel.email.value ?: ""){ result ->
                    runOnUiThread {
                        if(result.success){
                            dialogEnterCode.dismiss()
                            login()
                        } else {
                            loadingDialog.dismiss()
                            Toast.makeText(context, "Error, please retry.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            override fun resendCode() {
                loadingDialog.show(childFragmentManager, LoadingDialogFragment.TAG)
                viewModel.email.value?.let { email ->
                    viewModel.resendCode(email){
                        runOnUiThread {
                            loadingDialog.dismiss()
                            val status = if(it == "true") "success" else "fail"
                            Toast.makeText(requireContext(), "Send code: $status", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun closeDialog(dialog: DialogFragment) {
                dialog.dismiss()
            }
        })

        btnSignUp?.onClick {
            val user = getUser()

            LogHX.i("SignUp-User", user.toString())

            etFirstName.error = null
            etLastName.error = null
            etEmail.error = null
            etPassword.error = null
            etConfirmPassword.error = null

            viewModel.email.value = user.email

            //analyzeLoginResponse(AuthResult(success = true))
            loadingDialog.show(childFragmentManager, LoadingDialogFragment.TAG)
            viewModel.signUp(user, viewModel.isRequired.value == true){ authResult ->
                runOnUiThread { loadingDialog.dismiss() }
                if(authResult.success){
                    runOnUiThread {
                        showEnterCodeDialog()
                    }
                }else {
                    authResult.userValidError?.let {userValid ->
                        if(userValid.firstName != 0) etFirstName.error = getString(userValid.firstName)
                        if(userValid.lastName != 0) etLastName.error = getString(userValid.lastName)
                        if(userValid.email != 0) etEmail.error = getString(userValid.email)
                        if(userValid.password != 0) etPassword.error = getString(userValid.password)
                        if(userValid.confirmPassword != 0) etConfirmPassword.error = getString(userValid.confirmPassword)
                    } ?: kotlin.run {
                        runOnUiThread {  Toast.makeText(context, authResult.message, Toast.LENGTH_LONG).show() }
                    }
                }
            }
        }
    }

    private fun getUser(): User {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val cp = etCP.text.toString().trim()
        var phone = etPhone.text.toString().trim()
        val phoneCode = txtPhoneCode.text.toString()
        if(phoneCode != getString(R.string.default_code_phone) && phone.isNotEmpty()){
           phone = "$phoneCode$phone"
        }

        return User(
            firstName = firstName,
            lastName = lastName,
            name = "${firstName.trim()} ${lastName.trim()}",
            email = etEmail.text.toString(),
            password = etPassword.text.toString(),
            confirmPassword = etConfirmPassword.text.toString(),
            title_code = viewModel.titleSelected.value?.value ?: "",
            country_code = viewModel.countrySelected.value?.iSO2 ?: "",
            state_code = viewModel.stateSelected.value?.abbreviation ?: "",
            city = etCity.text.toString().trim(),
            address = address,
            cp = cp,
            phone = phone.replace("+", ""),
            cognitoId = Session.getUID(requireContext()) ?: "",
            notify_promotions = stReceivePromotions.isChecked,
            como_se_entero = Constants.COMO_SE_ENTERO_DEFAULT
        )
    }

    private fun showEnterCodeDialog(){
        dialogEnterCode.show(childFragmentManager, DialogEnterCode.TAG)
    }

    private fun analyzeLoginResponse(result: AuthResult){
        runOnUiThread {
            if(loadingDialog.dialog?.isShowing == true) loadingDialog.dismissAllowingStateLoss()
            loginOrSignUp(AnalyticType.SIGN_UP,ProviderType.Default,result.success)
        }
        if(result.success){
            runOnUiThread {

                viewModel.saveRememberDate()
                Session.setShowWelcomeAlert(true, requireContext())
                if(viewModel.redirectDestinationId.value != -1)
                    navigate(viewModel.redirectDestinationId.value!!)
                else {
                    _parentActivity?._viewModel?.activeNextStep?.value = true
                    _parentActivity?.onBackPressed()
                }
            }
        }
        else{
            result.userValidError?.let {userValid ->
                if(userValid.hasError){
                    if(userValid.email != 0) etEmail.error = getString(userValid.email)
                    if(userValid.password != 0) etPassword.error = getString(userValid.password)
                    if(userValid.firstName != 0) etFirstName.error = getString(userValid.firstName)
                    if(userValid.lastName != 0) etLastName.error = getString(userValid.lastName)
                }
            } ?: kotlin.run {
                runOnUiThread {
                    when (result.errorCode) {
                        ErrorCode.EmailNotConfirm -> {
                            dialogEnterCode.show(childFragmentManager, DialogEnterCode.TAG)
                        }
                        else -> Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}