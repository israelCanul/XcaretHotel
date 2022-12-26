package com.xcaret.xcaret_hotel.view.general.ui

import android.opengl.Visibility
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.domain.LangLabel
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.LogHX
import com.xcaret.xcaret_hotel.view.config.makeGone
import com.xcaret.xcaret_hotel.view.config.makeVisible
import com.xcaret.xcaret_hotel.view.config.onClick
import com.xcaret.xcaret_hotel.view.general.vm.DialogAlertCancelViewModel
import com.xcaret.xcaret_hotel.view.general.vm.DialogAlertViewModel
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.dialog_alert.icon
import kotlinx.android.synthetic.main.dialog_alert.lblCancel
import kotlinx.android.synthetic.main.dialog_alert.txtTitle
import kotlinx.android.synthetic.main.dialog_alert_cancel.*
import kotlinx.android.synthetic.main.dialog_detail_room.*
import kotlinx.android.synthetic.main.item_reservations.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.uiThread
import java.lang.Exception

class DialogAlertCancel: BaseDialog() {

    private var title: String? = null
    private var extraTitle: String? = null
    private var message: String? = null
    private var h2: String? = null
    private var days: String? = null
    private var accept: String? = null
    private var cancel: String? = null
    private var isCallRequired: Boolean? = false
    private var iconResource: Int? = null
    private var onCallListener: onClickListener? = null
    private var onCloseListener: onClickListener? = null

    private val labelUseCase: LangLabelUseCase by lazy { LangLabelUseCase() }
    val viewModel by lazy { DialogAlertCancelViewModel() }

    override fun getLayout(): Int = R.layout.dialog_alert_cancel

    override fun initComponents() {
        setObservers()
        fullScreen()
        setColorStatusBar()
        setColorNavBar()
        isCancelable = false
        txtH3.makeGone()
        txtH2.makeGone()
        txtH4.makeGone()
        //txtTitleCancelReservation.makeGone()
        btnCancelReservation
        onCloseListener?.let { listener ->
            btnCloseDialogCancel.makeVisible()
            btnCloseDialogCancel.onClick {
                listener.onClick(it, this)
            }
        }

        onCallListener?.let { listener ->
            //btnCall.makeVisible()
            btnCall.onClick {
                listener.onClick(it, this)
            }
        }

        iconResource?.let {
            icon.makeVisible()
            icon.setImageResource(it)
        }

        lblCall.text = accept
        showTitle()
        showH2()
        showH4()

    }

    fun setObservers(){
        viewModel.titleKey.observe(this,{
            viewModel.viewModelScope.launch{
                val lbl = withContext(Dispatchers.IO){viewModel.findLabelAlt(it)}
                if (lbl!= null) {
                    title = lbl?.value
                    title =
                        if (extraTitle.isNullOrEmpty()) lbl?.value else "$title\n$extraTitle"
                    showTitle()
                }else{
                    title = getString(R.string.lbl_cancellation_policies)
                    showTitle()
                }
            }
        })
        viewModel.dayBeforeCancelKey.observe(this,{
            viewModel.viewModelScope.launch {
                try {
                    val lbl = withContext(Dispatchers.IO){viewModel.findLabelAlt(it)}
                    if (lbl!= null) {
                        days = lbl.value
                        showDays()
                    }else{
                        days = getString(R.string.lbl_politic_days)
                        showDays()
                    }

                } catch (e: Exception) {
                }
            }
        })
        viewModel.h2Key.observe(this,{
            viewModel.viewModelScope.launch {
                try {
//                    val lbl = withContext(Dispatchers.IO){viewModel.findLabelAlt(it)}
//                    if (lbl!= null) {
//                        h2 = lbl.value
//                        h2 = it
//                        showH2()
//                    }else{
//                        h2 = getString(R.string.lbl_politics_h1)
//                        showH2()
//                    }

                    h2 = it
                    showH2()

                } catch (e: Exception) {
                }
            }
        })
        viewModel.h4Key.observe(this,{
            viewModel.viewModelScope.launch {
                try {
                    val lbl = withContext(Dispatchers.IO){viewModel.findLabelAlt(it)}
                    if (lbl!= null) {
                        message = lbl.value
                        showH4()
                    }else{
                        message = getString(R.string.lbl_message_politic)
                        showH4()
                    }

                } catch (e: Exception) {
                }
            }
        })

        viewModel.callButtonKey.observe(this,{
            viewModel.viewModelScope.launch {
                try {
                    val lbl = withContext(Dispatchers.IO){viewModel.findLabelAlt(it)}
                    if (lbl!=null){
                        LogHX.i("message", "$it : ${lbl?.value}")
                        accept = lbl?.value
                        lblCall.text = accept
                    }else{
                        accept = getString(R.string.lbl_call_to_cancel)
                        lblCall.text = accept
                    }

                } catch (e: Exception) {
                }
            }
        })
        viewModel.cancelButtonKey.observe(this,{
            viewModel.viewModelScope.launch {
                try {
                    val lbl = withContext(Dispatchers.IO){viewModel.findLabelAlt(it)}
                    LogHX.i("message", "$it : ${lbl?.value}")
                    cancel = lbl?.value
                    lblCancel.text = cancel
                } catch (e: Exception) {
                }
            }

        })

    }

    fun showTitle(){
        title?.let { t ->
            txtTitleCancelReservation.makeVisible()
            txtTitleCancelReservation.text = t
        }
    }

    fun showDays(){
        days?.let {
            //txtH3.makeVisible()
            txtH3.text = it
        }
    }


    fun showH2(){
        h2?.let {
            //if (!it.contains("N/A")) btnCall.visibility = View.VISIBLE
            if (isCallRequired == true) {
                btnCall.visibility = View.VISIBLE
                btnCloseDialogCancel.visibility = View.VISIBLE
            }
            else {
                btnCloseDialogCancel.visibility = View.VISIBLE
                btnCall.visibility = View.GONE
            }
            txtH2.makeVisible()
            txtH2.text = it
        }
    }

    fun showH4(){
        message?.let {
            //txtH4.makeVisible()
            txtH4.text = it
        }
    }

    fun setOnCallListener(onClickListener: onClickListener?): DialogAlertCancel{
        this.onCallListener = onClickListener
        return this
    }

    fun setOnCloseListener(onClickListener: onClickListener): DialogAlertCancel{
        this.onCloseListener = onClickListener
        return this
    }

    fun setIcon(iconResource: Int?): DialogAlertCancel {
        this.iconResource = iconResource
        return this
    }

    fun setDinamicTitle(key: String?):DialogAlertCancel{
        key?.let {
            LogHX.e("Title", key)
            viewModel.titleKey.postValue(key)
        }
        return this
    }

    fun setDaysBeforeCancel(key: String?): DialogAlertCancel{
        key?.let {
            viewModel.dayBeforeCancelKey.postValue(key)
        }
        return this
    }

    fun setH2(key: String?): DialogAlertCancel{
        key?.let {
            viewModel.h2Key.postValue(key)
        }
        return this
    }

    fun setH4(key: String?): DialogAlertCancel{
        key?.let {
            viewModel.h4Key.postValue(key)
        }
        return this
    }

    fun setDinamicTextButtonCall(key: String?): DialogAlertCancel{
        key?.let {
            viewModel.callButtonKey.postValue(key)
        }
        return this
    }
    fun setCallRequired(weCall: Boolean?): DialogAlertCancel{
        isCallRequired = weCall
        return this
    }

    interface onClickListener{
        fun onClick(v: View?, dialog: DialogFragment)
    }

    companion object{
        const val TAG = "DialogAlertCancel"

        fun newInstance() = DialogAlertCancel()
    }
}