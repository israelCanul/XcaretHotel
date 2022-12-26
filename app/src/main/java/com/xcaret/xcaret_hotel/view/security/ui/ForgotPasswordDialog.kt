package com.xcaret.xcaret_hotel.view.security.ui

import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.validWithRegex
import com.xcaret.xcaret_hotel.domain.LangLabel
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.onClick
import kotlinx.android.synthetic.main.dialog_forgot_password.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.uiThread

class ForgotPasswordDialog: BaseDialog() {

    private var forgotPasswordListener: ForgotPasswordListener? = null

    override fun getLayout(): Int = R.layout.dialog_forgot_password

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    override fun initComponents(){
        fullScreen()
        setColorNavBar()
        setColorStatusBar()
        //Utils.fixHeightTopView(statusBar)
        Utils.fixHeightBottomView(auxNavigation)

        setListeners()
    }

    private fun setListeners() {
        btnSend.setOnClickListener {
            if (isValid()) {
                forgotPasswordListener?.startRecoverPassword(etUser.text.toString().trim())
            }
        }

        txtClose.onClick {
            dismiss()
        }
    }

    private fun isValid(): Boolean{
        val email = etUser.text.toString().trim()
        val result = email.validWithRegex(Patterns.EMAIL_ADDRESS.pattern())
        if(!result) etUser.error = getString(R.string.field_email_error)
        else etUser.error = null
        return result
    }

    fun setForgotPasswordListener(forgotPasswordListener: ForgotPasswordListener){
        this.forgotPasswordListener = forgotPasswordListener
    }

    interface ForgotPasswordListener{
        fun startRecoverPassword(email: String)
    }

    companion object{
        const val TAG = "ForgotPasswordDialog"
        fun newInstance(): ForgotPasswordDialog = ForgotPasswordDialog()
    }
}