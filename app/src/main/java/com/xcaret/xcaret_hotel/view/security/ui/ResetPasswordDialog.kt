package com.xcaret.xcaret_hotel.view.security.ui

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.UserUseCase
import com.xcaret.xcaret_hotel.data.usecase.validWithRegex
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.InputCodeView
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.inputview.view.toggleVisiblePassword
import com.xcaret.xcaret_hotel.view.config.onClick
import kotlinx.android.synthetic.main.dialog_reset_password.*
import kotlinx.android.synthetic.main.dialog_reset_password.btnAccept
import kotlinx.android.synthetic.main.dialog_reset_password.etConfirmPassword
import kotlinx.android.synthetic.main.dialog_reset_password.etPassword
import kotlinx.android.synthetic.main.dialog_reset_password.statusBar
import kotlinx.android.synthetic.main.dialog_reset_password.txtClose

class ResetPasswordDialog: BaseDialog() {

    private var resetPasswordListener: ResetPasswordListener? = null

    override fun getLayout(): Int = R.layout.dialog_reset_password

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun initComponents() {
        fullScreen()
        setColorNavBar()
        setColorStatusBar()
        //Utils.fixHeightTopView(statusBar)
        Utils.fixHeightBottomView(auxNavigation)
        setListeners()
    }

    fun setResetPasswordListener(resetPasswordListener: ResetPasswordListener){
        this.resetPasswordListener = resetPasswordListener
    }

    private fun setListeners(){
        btnAccept.setOnClickListener {
            if(isValid() && inputCode.isValid()){
                val code = inputCode.getText().trim()
                val pass = etPassword.text.toString().trim()
                resetPasswordListener?.confirmResetPassword(code, pass)
            }
        }

        txtResendCode.setOnClickListener {
            resetPasswordListener?.resendCode()
        }

        txtClose.setOnClickListener {
            dismiss()
        }

        btnShowPassword.onClick {
            etPassword.toggleVisiblePassword(btnShowPassword)
        }
        btnShowConfirmPassword.onClick {
            etConfirmPassword.toggleVisiblePassword(btnShowConfirmPassword)
        }
        inputCode.setOnFinishInputCodeListener(object: InputCodeView.inputCodeViewListener{
            override fun onFinishedInput() {
                etPassword.requestFocus()
            }
        })
    }


    private fun isValid(): Boolean{
        val pass = etPassword.text.toString().trim()
        val confirmPass = etConfirmPassword.text.toString().trim()

        var result = true

        if(pass.isEmpty()){
            etPassword.error = getString(R.string.field_required)
            result = false
        }else etPassword.error = null

        if(!pass.validWithRegex(UserUseCase.REGEX_PASS)){
            etPassword.error = getString(R.string.field_password_error)
            result = false
        }else etPassword.error = null

        if(pass != confirmPass){
            etConfirmPassword.error = getString(R.string.field_pass_not_match)
            result = false
        } else etConfirmPassword.error = null

        return result
    }

    override fun dismiss() {
        clean()
        super.dismiss()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
        inputCode?.initFocus()
    }

    private fun clean(){
        inputCode.clean()
        etPassword.setText("")
        etConfirmPassword.setText("")
    }

    companion object {
        const val TAG = "ResetPasswordDialog"

        fun newInstance(): ResetPasswordDialog = ResetPasswordDialog()
    }

    interface ResetPasswordListener{
        fun confirmResetPassword(code: String, pass: String)
        fun resendCode()
    }

}