package com.xcaret.xcaret_hotel.view.general.ui

import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_loading_fragment.*

class LoadingDialogFragment: BaseDialog(){
    override fun getLayout(): Int = R.layout.dialog_loading_fragment
    override fun initComponents() {
        isCancelable = false
        fullScreen()
        setColorStatusBar()
        setColorNavBar()
    }

    fun setTextLoading(res: Int){
        txtLoading.setText(res)
    }

    fun setTextLoading(text: String){
        txtLoading.text = text
    }

    companion object{
        const val TAG = "LoadingDialogFragment"

        fun newInstance(): LoadingDialogFragment{
            return LoadingDialogFragment()
        }
    }
}