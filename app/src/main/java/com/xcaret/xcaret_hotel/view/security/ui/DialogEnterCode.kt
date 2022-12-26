package com.xcaret.xcaret_hotel.view.security.ui

import androidx.fragment.app.DialogFragment
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.Utils
import kotlinx.android.synthetic.main.dialog_enter_code.*

class DialogEnterCode: BaseDialog(){
    private var enterCodeListener: EnterCodeListener? = null

    override fun getLayout(): Int = R.layout.dialog_enter_code

    override fun initComponents() {
        fullScreen()
        setColorNavBar()
        setColorStatusBar()
        //Utils.fixHeightTopView(statusBar)
        Utils.fixHeightBottomView(auxNavigation)
        setListeners()
    }

    fun setEnterCodeListener(enterCodeListener: EnterCodeListener?){
        this.enterCodeListener = enterCodeListener
    }

    fun setListeners(){
        btnVerify.setOnClickListener {
            if(inputCode.isValid()){
                enterCodeListener?.onConfirmCode(inputCode.getText())
            }
        }

        txtClose.setOnClickListener {
            enterCodeListener?.closeDialog(this)
        }

        txtSendCodeAgain.setOnClickListener { enterCodeListener?.resendCode() }
    }

    companion object {
        const val TAG = "DialogEnterCode"

        fun newInstance(): DialogEnterCode {
            return DialogEnterCode()
        }
    }

    interface EnterCodeListener{
        fun onConfirmCode(code: String)
        fun resendCode()
        fun closeDialog(dialog: DialogFragment)
    }
}