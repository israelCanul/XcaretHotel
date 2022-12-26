package com.xcaret.xcaret_hotel.view.booking.ui

import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.onClick
import kotlinx.android.synthetic.main.dialog_help_cvv.*

class CVVHelDialog: BaseDialog() {
    override fun getLayout(): Int = R.layout.dialog_help_cvv

    override fun initComponents() {
        fullScreen()
        setColorNavBar()
        //setColorStatusBar()
        Utils.fixHeightBottomView(auxNavigation)
        setListeners()
    }

    private fun setListeners(){
        txtClose.onClick {
            dismiss()
        }
    }

    companion object{
        const val TAG = "CVVHelDialog"
        fun newInstance() = CVVHelDialog()
    }
}