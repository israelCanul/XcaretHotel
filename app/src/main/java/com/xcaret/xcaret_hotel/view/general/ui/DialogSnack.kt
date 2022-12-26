package com.xcaret.xcaret_hotel.view.general.ui

import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.domain.LangLabel
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.general.vm.DialogAlertViewModel
import com.xcaret.xcaret_hotel.view.general.vm.DialogSnackViewModel
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.item_hotel.*
import kotlinx.android.synthetic.main.snack_bar_simulator.*
import kotlinx.coroutines.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.findOptional
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.uiThread
import kotlin.Exception

class DialogSnack: BaseDialog() {

    private var snackType: Int? = 0
    private var extraTitle: String? = null
    private var message: String? = null
    private var messageExtra: String? = ""
    private val viewModel:DialogSnackViewModel by lazy {DialogSnackViewModel()}


    override fun getLayout(): Int = R.layout.snack_bar_simulator

    override fun initComponents() {
        setObservers()
        fullScreen()
        isCancelable = false
        showMessage()

        val timer = object :CountDownTimer(2000,1000){
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {

                dismiss()
            }
        }
        timer.start()
        checkSnacktype()
    }

    private fun checkSnacktype() {
        when(snackType){
            Constants.SNACK_ERROR->{
                bgSnack.backgroundDrawable = context?.let { AppCompatResources.getDrawable(it,R.drawable.snack_bg_error) }
                customColorStatusBar(R.color.color_error_snack)
            }
            Constants.SNACK_SUCCES->{
                bgSnack.backgroundDrawable = context?.let { AppCompatResources.getDrawable(it,R.drawable.snack_bg_succes) }
                customColorStatusBar(R.color.color_sucess_snack)

            }
            Constants.SNACK_INFO->{
                bgSnack.backgroundDrawable = context?.let { AppCompatResources.getDrawable(it,R.drawable.snack_bg) }
                customColorStatusBar(R.color.color9_Snack)

            }
            else->{
                bgSnack.backgroundDrawable = context?.let { AppCompatResources.getDrawable(it,R.drawable.snack_bg) }
                customColorStatusBar(R.color.color9_Snack)
            }

        }
    }


    fun setObservers(){
        viewModel.messageKey.observe(this,{
            viewModel.viewModelScope.launch {
                try {
                    val lbl = withContext(Dispatchers.IO){viewModel.findLabelAlt(it)}
                    LogHX.i("message", "$it : ${lbl?.value}")
                    message = lbl?.value
                    message = message?.replace("@", messageExtra!!)
                    showMessage()
                } catch (e: Exception) {
                }
            }
        })
    }

    fun showMessage(){
        try {
            message?.let {
                txtSnackMessage?.makeVisible()
                txtSnackMessage?.text = it
            }
        }catch (exc:Exception){

        }
    }

    fun setTypeSnack(type: Int): DialogSnack{
        this.snackType = type
        return this
    }

    fun setDinamicMessage(key: String, extraMessage: String? = ""): DialogSnack{
        key.let {
            LogHX.e("message", key)
            this.messageExtra = extraMessage
            viewModel.messageKey.postValue(key)
        }
        return this
    }

    interface onClickListener{
        fun onClick(v: View?, dialog: DialogFragment)
    }

    companion object{
        const val TAG = "DialogSnack"

        fun newInstance() = DialogSnack()
    }
}