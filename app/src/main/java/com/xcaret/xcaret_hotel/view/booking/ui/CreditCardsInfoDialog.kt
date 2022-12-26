package com.xcaret.xcaret_hotel.view.booking.ui

import android.os.Bundle
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.onClick
import kotlinx.android.synthetic.main.dialog_credits_card_info.*

class CreditCardsInfoDialog: BaseDialog() {
    override fun getLayout(): Int = R.layout.dialog_credits_card_info

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setTransitionStyle(R.style.DialogAnimation)
    }

    override fun initComponents() {
        fullScreen()
        setColorNavBar()
        setColorStatusBar()
        //Utils.fixHeightTopView(auxStatusBar)
        Utils.fixHeightBottomView(auxNavigation)
        setListeners()
    }

    private fun setListeners(){
        txtClose.onClick { dismiss() }
    }

    companion object{
        const val TAG = "CreditCardsInfoDialog"

        fun newInstance() = CreditCardsInfoDialog()
    }
}