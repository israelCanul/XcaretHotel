package com.xcaret.xcaret_hotel.view.general.ui

import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.domain.LangLabel
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.LogHX
import com.xcaret.xcaret_hotel.view.config.makeGone
import com.xcaret.xcaret_hotel.view.config.makeVisible
import com.xcaret.xcaret_hotel.view.config.onClick
import com.xcaret.xcaret_hotel.view.general.vm.DialogAlertViewModel
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.item_hotel.*
import kotlinx.coroutines.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.findOptional
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.uiThread
import kotlin.Exception

class DialogAlert: BaseDialog() {


    private var title: String? = null
    private var extraTitle: String? = null
    private var message: String? = null
    private var messageExtra: String? = null
    private var accept: String? = null
    private var cancel: String? = null
    private var iconResource: Int? = null
    private var onCancelListener: onClickListener? = null
    private var onAcceptListener: onClickListener? = null

    private val labelUseCase: LangLabelUseCase by lazy { LangLabelUseCase() }
    private val viewModel:DialogAlertViewModel by lazy {DialogAlertViewModel()}


    override fun getLayout(): Int = R.layout.dialog_alert

    override fun initComponents() {
        setObservers()
        fullScreen()
        setColorStatusBar()
        setColorNavBar()
        isCancelable = false
        btnCancel.makeGone()
        btnAccept.makeGone()
        txtTitle.makeGone()
        icon.makeGone()

        onCancelListener?.let { listener ->
            btnCancel.makeVisible()
            btnCancel.onClick {
                listener.onClick(it, this)
            }
        }

        onAcceptListener?.let { listener ->
            btnAccept.makeVisible()
            btnAccept.onClick {
                listener.onClick(it, this)
            }
        }

        iconResource?.let {
            icon.makeVisible()
            icon.setImageResource(it)
        }

        lblAccept.text = accept
        lblCancel.text = cancel
        showTitle()
        showMessage()
    }


    fun setObservers(){
        viewModel.titleKey.observe(this,{
            viewModel.viewModelScope.launch{
                val lbl = withContext(Dispatchers.IO){viewModel.findLabelAlt(it)}
                title = lbl?.value
                title =
                    if (extraTitle.isNullOrEmpty()) lbl?.value else "$title\n$extraTitle"
                showTitle()
            }
        })
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
        viewModel.confirmButtonKey.observe(this,{
            viewModel.viewModelScope.launch {
                try {
                    val lbl = withContext(Dispatchers.IO){viewModel.findLabelAlt(it)}
                    LogHX.i("message", "$it : ${lbl?.value}")
                    accept = lbl?.value
                    lblAccept.text = accept
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
        try {
            title?.let { t ->
                val nTitle = title ?: ""
                txtTitle?.makeVisible()
                txtTitle?.text = nTitle
            }
        }catch (exc:Exception){

        }
    }

    fun showMessage(){
        try {
            message?.let {
                txtMessage?.makeVisible()
                txtMessage?.text = it
            }
        }catch (exc:Exception){

        }
    }

    fun setOnCancelListener(onClickListener: onClickListener?): DialogAlert{
        this.onCancelListener = onClickListener
        return this
    }

    fun setOnAcceptListener(onClickListener: onClickListener?): DialogAlert{
        this.onAcceptListener = onClickListener
        return this
    }

    fun setIcon(iconResource: Int?): DialogAlert {
        this.iconResource = iconResource
        return this
    }

    fun setExtraTitle(extraTitle: String?): DialogAlert{
        this.extraTitle = extraTitle
        return this
    }

    fun setDinamicTitle_(key: String?): DialogAlert{
        key?.let {
                findLabel(key) { label ->
                    label?.let {
                        runOnUiThread {
                            try {
                                title = it.value
                                title =
                                    if (extraTitle.isNullOrEmpty()) label.value else "$title\n$extraTitle"
                                showTitle()

                            } catch (e: Exception) {
                            }
                        }
                    }
                }
            } ?: kotlin.run {
                title = key
        }
        return this
    }

    fun setDinamicTitle(key: String?):DialogAlert{
        key?.let {
            LogHX.e("Title", key)
            viewModel.titleKey.postValue(key)
        }
        return this
    }

    fun setDinamicMessage(key: String, extraMessage: String? = ""): DialogAlert{
        key.let {
            LogHX.e("message", key)
            this.messageExtra = extraMessage
            viewModel.messageKey.postValue(key)
        }
        return this
    }


    fun setDinamicMessage_(key: String, extraMessage: String? = ""): DialogAlert{
        LogHX.i("message", "$key")
        findLabel(key){ label ->
            label?.let {
                runOnUiThread {
                try{
                    LogHX.i("message", "$key : ${it.value}")
                    message = it.value
                    message = message?.replace("@",extraMessage!!)
                    showMessage()
                }
                catch (e: Exception){}
            } }
        }
        return this
    }

    fun setDinamicTextButtonConfirm(key: String?): DialogAlert{
        key.let {
            LogHX.e("buttonConfirm", key)
            viewModel.confirmButtonKey.postValue(key)
        }
        return this
    }

    fun setDinamicTextButtonConfirm_(key: String?): DialogAlert{
        if(!key.isNullOrEmpty()) {
                findLabel(key) { label ->
                    label?.let {
                            try {
                                accept = label.value
                                lblAccept.text = accept
                            } catch (e: Exception) {
                            }
                    }
                }
            }
        return this
    }

    fun setDinamicTextButtonCancel(key: String?): DialogAlert{
        key.let {
            LogHX.e("cancelConfirm", key)
            viewModel.cancelButtonKey.postValue(key)
        }
        return this
    }

    fun setDinamicTextButtonCancel_(key: String?): DialogAlert{
        if(!key.isNullOrEmpty()) {
                findLabel(key) { label ->
                    label?.let {
                        runOnUiThread {
                            try {
                                cancel = label.value
                                lblCancel.text = cancel
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
        }
        return this
    }

    fun findLabel(key: String, result: (res: LangLabel?) -> Unit){
        doAsync {
            val label = labelUseCase.findLabelOutLive(key)
            uiThread {
                result(label)
            }
        }

    }

    interface onClickListener{
        fun onClick(v: View?, dialog: DialogFragment)
    }

    companion object{
        const val TAG = "DialogAlert"

        fun newInstance() = DialogAlert()
    }
}