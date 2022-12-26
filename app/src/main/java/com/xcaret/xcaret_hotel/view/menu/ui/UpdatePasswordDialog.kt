package com.xcaret.xcaret_hotel.view.menu.ui

import android.os.Bundle
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.UserUseCase
import com.xcaret.xcaret_hotel.data.usecase.validWithRegex
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.inputview.view.toggleVisiblePassword
import com.xcaret.xcaret_hotel.view.config.onClick
import kotlinx.android.synthetic.main.dialog_update_password.*

class UpdatePasswordDialog: BaseDialog() {
    override fun getLayout(): Int = R.layout.dialog_update_password

    private var udpatePasswordListener: UpdatePasswordListener? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun initComponents() {
        fullScreen()
        Utils.fixHeightTopView(statusBar)
        Utils.fixHeightBottomView(auxNavigation)
        setListeners()
    }

    fun setUpdatePasswordListener(updatePasswordListener: UpdatePasswordListener){
        this.udpatePasswordListener = updatePasswordListener
    }

    private fun setListeners(){
        txtClose.onClick { dismiss() }

        btnAccept.onClick {
            if(isValid()) udpatePasswordListener?.onUpdatePassword(etCurrentPassword.text.toString(), etPassword.text.toString())
        }

        btnCurrentPassword.onClick {
            etCurrentPassword.toggleVisiblePassword(btnCurrentPassword)
        }

        btnNewPassword.onClick {
            etPassword.toggleVisiblePassword(btnNewPassword)
        }

        btnConfirmNewPassword.onClick {
            etConfirmPassword.toggleVisiblePassword(btnConfirmNewPassword)
        }
    }

    override fun dismiss() {
        super.dismiss()
        clean()
    }

    private fun clean(){
        etCurrentPassword.setText("")
        etPassword.setText("")
        etConfirmPassword.setText("")
    }

    private fun isValid(): Boolean{
        val currentPass = etCurrentPassword.text.toString().trim()
        val pass = etPassword.text.toString().trim()
        val confirmPass = etConfirmPassword.text.toString().trim()

        var result = true

        if(!currentPass.validWithRegex(UserUseCase.REGEX_PASS)){
            etCurrentPassword.error = getString(R.string.field_password_error)
            result = false
        }else etCurrentPassword.error = null

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

    interface UpdatePasswordListener{
        fun onUpdatePassword(currenPassword: String, newPassword: String)
    }

    companion object{
        const val TAG = "UpdatePasswordDialog"
        fun newInstance() = UpdatePasswordDialog()
    }
}