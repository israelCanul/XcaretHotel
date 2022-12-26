package com.xcaret.xcaret_hotel.view.security.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.amplifyframework.auth.AuthProvider.facebook
import com.amplifyframework.auth.AuthProvider.google
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.google.android.material.snackbar.Snackbar
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.validWithRegex
import com.xcaret.xcaret_hotel.databinding.LoginFragmentBinding
import com.xcaret.xcaret_hotel.domain.AuthResult
import com.xcaret.xcaret_hotel.domain.LangLabel
import com.xcaret.xcaret_hotel.domain.ProviderType
import com.xcaret.xcaret_hotel.domain.User
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticType
import com.xcaret.xcaret_hotel.view.config.analytics.loginOrSignUp
import com.xcaret.xcaret_hotel.view.config.inputview.view.toggleVisiblePassword
import com.xcaret.xcaret_hotel.view.general.ui.DialogAlert
import com.xcaret.xcaret_hotel.view.general.ui.LoadingDialogFragment
import com.xcaret.xcaret_hotel.view.security.vm.LoginViewModel
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.login_fragment.etPassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.uiThread
import java.security.ProviderException
import kotlin.math.log

class LoginFragment: BaseFragmentDataBinding<LoginViewModel, LoginFragmentBinding>(){
    override fun getLayout(): Int = R.layout.login_fragment
    override fun getViewModelClass(): Class<LoginViewModel> = LoginViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val dialogEnterCode: DialogEnterCode by lazy {
        DialogEnterCode.newInstance()
    }

    private val forgotPasswordDialog: ForgotPasswordDialog by lazy {
        ForgotPasswordDialog.newInstance()
    }

    private val resetPasswordDialog: ResetPasswordDialog by lazy {
        ResetPasswordDialog.newInstance()
    }

    override fun setupUI() {
        showSystemUI()
        viewModel.isVisitor.value = arguments?.getBoolean(Constants.IS_VISTOR, false) ?: false
        viewModel.redirectDestinationId.value = arguments?.getInt(Constants.REDIRECT_DESTINATION_ID, -1) ?: -1
    }

    private val loadingDialog: LoadingDialogFragment by lazy {
        LoadingDialogFragment.newInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    override fun onResume() {
        super.onResume()
        if(_parentActivity?._viewModel?.activeNextStep?.value == true && viewModel.isVisitor.value == true) {
            _parentActivity?._viewModel?.activeNextStep?.value = false
            _parentActivity?.onBackPressed()
        }
    }

    private fun initComponents(){
        Utils.fixHeightTopView(fixStatusBar)
        setListener()
    }

    private fun setListener(){
        
        headerLayout.addOnLayoutChangeListener { v, left, top, right, bottom,
                                                 oldLeft, oldTop, oldRight, oldBottom ->
            val height = v.height
            if(height!=0) adjustShadow(height)
        }
        
        btnBack.onClick {
            _parentActivity?.onBackPressed()
        }

        btnShowPassword.onClick {
            etPassword.toggleVisiblePassword(btnShowPassword)
        }

        txtSignUp?.onClick {
            loginOrSignUp(AnalyticType.LOGIN,ProviderType.Visitor,true)
            Session.setLoginType(ProviderType.Visitor.value,HotelXcaretApp.mContext)

            val args = bundleOf(
                Constants.IS_VISTOR to (viewModel.isVisitor.value == true),
                Constants.BEFORE_DESTINATION to FragmentTags.Login.value
            )
            if(viewModel.isVisitor.value == false)
                args.putInt(Constants.REDIRECT_DESTINATION_ID, R.id.action_signUpFragment_to_splashFragment)
            try {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment, args)
            }catch (exc:IllegalArgumentException){

            }
        }

        dialogEnterCode.setEnterCodeListener(object: DialogEnterCode.EnterCodeListener{
            override fun onConfirmCode(code: String) {
                viewModel.confirmSignUp(code, viewModel.email.value ?: ""){result ->
                    runOnUiThread {
                        if(result.success) {
                            dialogEnterCode.dismiss()
                            if (!viewModel.emailIsConfirming.value!!)
                                login()
                            else
                                loadingDialog.show(childFragmentManager, LoadingDialogFragment.TAG)
                                viewModel.forgotPassword(viewModel.email.value!!){ success ->
                                    runOnUiThread {
                                        loadingDialog.dismiss()
                                        if (success) {
                                            forgotPasswordDialog.dismiss()
                                            resetPasswordDialog.show(
                                                childFragmentManager,
                                                ResetPasswordDialog.TAG
                                            )
                                        }else Toast.makeText(
                                            requireContext(),
                                            "Check your internet connection!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                        else Toast.makeText(context, "Error, please retry.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun resendCode() {
                context?.let {ctx ->
                    val email = etUser.text.toString().trim()
                    if(!email.isNullOrEmpty()){
                        viewModel.resendCode(email){
                            var status = "fail"
                            when (it) {
                                "true" -> {
                                    status = "success" }
                                "limitExceed" -> {
                                    showWarning(getString(R.string.rkey_lbl_limit_of_attempts))
                                    //showSnackBar_("Ups!! Limite de intentos Excedido")
                                }
                                else -> {
                                    status = "fail"
                                }
                            }

                            runOnUiThread {
                                Toast.makeText(ctx, "Send code: $status", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun closeDialog(dialog: DialogFragment) {
                dialog.dismiss()
            }
        })

        btnLogin?.onClick {
            login()
        }

        fabFacebook.onClick {
            _parentActivity?.let {act ->
                runOnUiThread { loadingDialog.show(parentFragmentManager, LoadingDialogFragment.TAG) }
                viewModel.loginWithSocialWebUI(facebook(), act){ result ->
                    analyzeLoginResponse(result, ProviderType.Facebook)
                }
            }
        }

        fabGoogle.onClick {
            _parentActivity?.let {act ->
                runOnUiThread { loadingDialog.show(parentFragmentManager, LoadingDialogFragment.TAG) }
                viewModel.loginWithSocialWebUI(google(), act){result ->
                    analyzeLoginResponse(result, ProviderType.Google)
                }
            }
        }

        txtSingInVisitor.onClick {
            loadingDialog.show(childFragmentManager, LoadingDialogFragment.TAG)
            viewModel.loginVisitor { result ->
                analyzeLoginResponse(result, ProviderType.Visitor)
            }
        }

        txtForgotPassword.onClick {
            forgotPasswordDialog.show(childFragmentManager, ForgotPasswordDialog.TAG)
        }

        forgotPasswordDialog.setForgotPasswordListener(object: ForgotPasswordDialog.ForgotPasswordListener{
            override fun startRecoverPassword(email: String) {
                LogHX.i("Email to reset", email)
                viewModel.email.value = email
                loadingDialog.show(childFragmentManager, LoadingDialogFragment.TAG)

                viewModel.getUser(email){result->
                    if (result != null) {
                        if (result.UserData != null) {
                            if (result.UserData.Status.equals(Constants.UNCONFIRMED, true)) {
                                loadingDialog.dismiss()
                                showEmailUncofirmedAlert(email)
                                return@getUser
                            } else {
                                viewModel.forgotPassword(email) { success ->
                                    runOnUiThread {
                                        loadingDialog.dismiss()
                                        if (success) {
                                            forgotPasswordDialog.dismiss()
                                            resetPasswordDialog.show(
                                                childFragmentManager,
                                                ResetPasswordDialog.TAG
                                            )
                                        } else Toast.makeText(
                                            requireContext(),
                                            "Check your internet connection!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            }
                        }else{
                            loadingDialog.dismiss()
                            showUserDoesntExist(getString(R.string.rkey_lbl_email_dont_exist))
                        }
                    } else {
                        loadingDialog.dismiss()
                        showUserDoesntExist(getString(R.string.rkey_lbl_email_dont_exist))
                    }

                }
            }
        })

        resetPasswordDialog.setResetPasswordListener(object: ResetPasswordDialog.ResetPasswordListener{
            override fun confirmResetPassword(code: String, pass: String) {
                loadingDialog.show(childFragmentManager, LoadingDialogFragment.TAG)
                viewModel.confirmForgotPassword(pass, code){
                    val message = if(it) "Success change password" else "Error to change password"
                    runOnUiThread {
                        loadingDialog.dismiss()
                        if(it) resetPasswordDialog.dismiss()
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun resendCode() {
                loadingDialog.show(childFragmentManager, LoadingDialogFragment.TAG)
                viewModel.forgotPassword(viewModel.email.value ?: ""){
                    runOnUiThread {
                        loadingDialog.dismiss()
                        val message = if(it) "Re-send code success" else "Error to re-send code"
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun login(){
        val email = etUser.text.toString()
        val pass = etPassword.text.toString()

        val user = User(email = email, userName = email, password = pass, provider = ProviderType.Default)

        etUser.error = null
        etPassword.error = null
        viewModel.email.value = email

        loadingDialog.show(parentFragmentManager, LoadingDialogFragment.TAG)
        viewModel.login(user){result: AuthResult ->
            analyzeLoginResponse(result)
        }
    }

    private fun analyzeLoginResponse(result: AuthResult, type: ProviderType = ProviderType.Default){
        runOnUiThread {
            try{ loadingDialog.dismiss() }catch (e: Exception){e.printStackTrace()}
            loginOrSignUp(AnalyticType.LOGIN, type, result.success)
            Session.setLoginType(type.value,HotelXcaretApp.mContext)
        }
        if(result.success){
            runOnUiThread {
                viewModel.saveRememberDate()
                if(viewModel.redirectDestinationId.value != -1) {
                    Session.setShowWelcomeAlert(true, requireContext())
                    navigate(viewModel.redirectDestinationId.value!!)
                }
                else {
                    if(!viewModel.email.value.isNullOrEmpty())
                        MarketingCloud.setManualContactKey(viewModel.email.value ?: Session.getDeviceUID(requireContext()))
                    _parentActivity?._viewModel?.activeNextStep?.value = false
                    _parentActivity?._viewModel?.currentFragment?.value = viewModel.redirectDestinationId.value
                    _parentActivity?.onBackPressed()
                }
            }
        }
        else{
            result.userValidError?.let { userValid ->
                if(userValid.hasError){
                    if(userValid.email != 0) etUser.error = getString(userValid.email)
                    if(userValid.password != 0) etPassword.error = getString(userValid.password)
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

    private fun adjustShadow(actualHeight :Int){
        val additionalHeight = Utils.convertDpToPixels(8f)
        binding.fakeShadow.updateHeight(actualHeight+additionalHeight)

    }

    fun findLabel(key: String, result: (res: LangLabel?) -> Unit){
        doAsync {
            val label = viewModel.labelUseCase.findLabelOutLive(key)
            uiThread {
                result(label)
            }
        }
    }
    fun showWarning(key: String,isDialog:Boolean=false){
        findLabel(key){label ->
            label?.let {
                runOnUiThread {
                    if (isDialog){
                        val snack = forgotPasswordDialog.showSnackBar(label.value, false)
                        snack?.addCallback(object : Snackbar.Callback() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                super.onDismissed(transientBottomBar, event)
                                if(!isNightEnabled()){
                                    setColorStatusBar(R.color.colorBackgroundTop)
                                }else{
                                    setColorStatusBar(R.color.colorSnackStatusBar)
                                }
                            }

                            override fun onShown(sb: Snackbar?) {
                                super.onShown(sb)
                                if(!isNightEnabled()){
                                    setColorStatusBar(R.color.color9_Snack)
                                }else{
                                    setColorStatusBar(R.color.colorSnackNight)
                                }
                            }
                        })
                        snack?.show()
                    }
                    else{
                        val snack = showSnackBar(label.value)
                        snack?.addCallback(object : Snackbar.Callback() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                super.onDismissed(transientBottomBar, event)
                                if(!isNightEnabled()){
                                    setColorStatusBar(R.color.colorBackgroundTop)
                                }else{
                                    setColorStatusBar(R.color.colorSnackStatusBar)
                                }
                            }

                            override fun onShown(sb: Snackbar?) {
                                super.onShown(sb)
                                if(!isNightEnabled()){
                                    setColorStatusBar(R.color.color9_Snack)
                                }else{
                                    setColorStatusBar(R.color.colorSnackNight)
                                }
                            }
                        })
                        snack?.show()
                    }
                }
            }
        }

    }

    fun showUserDoesntExist(key: String){
        showAlert(
            icon = R.drawable.ic_information,
            message = key,
            accept = getString(R.string.rkey_btn_accept),
            cancel = getString(R.string.rkey_btn_cancel),
            cancelListener = null,
            acceptListener = object : DialogAlert.onClickListener {
                override fun onClick(v: View?, dialog: DialogFragment) {
                    dialog.dismiss()

                }

            }
        )

    }

    fun showEmailUncofirmedAlert(email :String){
        showAlert(
            icon = R.drawable.ic_information,
            message = getString(R.string.rkey_lbl_confirm_email),
            accept = getString(R.string.rkey_btn_accept),
            cancel = getString(R.string.rkey_btn_cancel),
            cancelListener = object : DialogAlert.onClickListener {
                override fun onClick(v: View?, dialog: DialogFragment) {
                    dialog.dismiss()

                }
            },
            acceptListener = object : DialogAlert.onClickListener {
                override fun onClick(v: View?, dialog: DialogFragment) {
                    dialog.dismiss()
                    viewModel.resendCode(email){
                        if (it =="true"){
                            viewModel.emailIsConfirming.value = true
                            forgotPasswordDialog.dismiss()
                            dialogEnterCode.showSecure(parentFragmentManager, DialogEnterCode.TAG)
                        }else if(it == "limitExceed"){
                            if (forgotPasswordDialog.isVisible)
                                showWarning(getString(R.string.rkey_lbl_limit_of_attempts),true)
                                //forgotPasswordDialog.showSnackBar_( "Ups Limite de intentos Excedido")
                        }
                    }
                }

            }
        )

    }

    fun showAlert(
        icon: Int? = null,
        title: String? = null,
        extraTitle: String? = null,
        message: String,
        accept: String? = null,
        cancel: String? = null,
        acceptListener:DialogAlert.onClickListener?,
        cancelListener:DialogAlert.onClickListener?){

        //GlobalScope.launch(Dispatchers.Main) {
            try {

                val dialogAlert = DialogAlert.newInstance()
                    .setIcon(icon)
                    .setExtraTitle(extraTitle)
                    .setDinamicTitle(title)
                    .setDinamicMessage(message)
                    .setDinamicTextButtonConfirm(accept)
                    .setDinamicTextButtonCancel(cancel)
                    .setOnAcceptListener(acceptListener)
                    .setOnCancelListener(cancelListener)
                dialogAlert.showSecure(parentFragmentManager, DialogAlert.TAG)
            }catch (e: Exception){
                Log.e(DialogAlert.TAG, e.toString())
            }
        //}
    }

}