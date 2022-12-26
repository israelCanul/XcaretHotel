package com.xcaret.xcaret_hotel.view.config

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import kotlinx.android.synthetic.main.widget_number_picker_secondary.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception

class NumberPickerSecondaryView: ConstraintLayout {

    private val delay: Long = 1000
    private var defaultText = ""
    private var remoteKey = ""
    private var limit = 2
    private var inferiorLimit = 1
    private var defaultNumber = 2
    private var locked = false
    private val labelUseCase: LangLabelUseCase by lazy { LangLabelUseCase() }
    private var numberPickerListener: NumberPickerListener? = null

    init {
        inflate(context, R.layout.widget_number_picker_secondary, this)
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init(attrs)
    }

    fun getNumber() = defaultNumber

    fun setNumberPickerListener(numberPickerListener: NumberPickerListener?){
        this.numberPickerListener = numberPickerListener
    }

    private fun init(attrs: AttributeSet?){
        attrs?.let { attr ->
            val attributes = context.obtainStyledAttributes(attr, R.styleable.NumberPickerSecondaryView)
            try{
                defaultText = attributes.getString(R.styleable.NumberPickerSecondaryView_label) ?: defaultText
                remoteKey = attributes.getString(R.styleable.NumberPickerSecondaryView_remoteLabel) ?: remoteKey
                limit = attributes.getInt(R.styleable.NumberPickerSecondaryView_maxNumber, limit)
                defaultNumber = attributes.getInt(R.styleable.NumberPickerSecondaryView_defaultNumber, 0)
                inferiorLimit = attributes.getInt(R.styleable.NumberPickerSecondaryView_inferiorLimit, inferiorLimit)

                setLocalText(defaultText)
                setRemoteText(remoteKey)
                setDefaultNumber(defaultNumber)
                inferiorLimit(inferiorLimit)
            }
            catch (e: Exception){}
            finally {
                attributes.recycle()
            }
        }
        setListeners()
    }

    private fun setListeners(){
        decrement.setOnClickListener {
            decrement(false)
        }

        decrement.setOnLongClickListener {
            decrement(true)
            true
        }

        increment.setOnClickListener {
            increment(false)
        }

        increment.setOnLongClickListener {
            increment(true)
            true
        }
    }

    private fun increment(addDelay: Boolean){
        if(addDelay){
            if(!locked && defaultNumber < limit) setDefaultNumber(defaultNumber + 1)
            else addDelay()
        }else if((defaultNumber) < limit) {
            locked = false
            setDefaultNumber(defaultNumber + 1)
        }
    }

    private fun decrement(addDelay: Boolean){
        if(addDelay){
            if(!locked && defaultNumber > inferiorLimit) setDefaultNumber(defaultNumber - 1)
            else addDelay()
        }else if(defaultNumber > inferiorLimit) {
            locked = false
            setDefaultNumber(defaultNumber - 1)
        }
    }

    private fun addDelay(){
        if(!locked){
            locked = true
            android.os.Handler().postDelayed({ locked = false }, delay)
        }
    }

    fun setDefaultNumber(default: Int){
        numberPickerListener?.onChangeNumber(default)
        this.defaultNumber = default
        number.text = defaultNumber.toString()
    }

    fun setLocalText(default: String) {
        this.defaultText = default
        label.text = defaultText
    }

    fun setRemoteText(remote: String) {
        this.remoteKey = remote
        loadLabel()
    }

    fun setLimit(limit: Int){
        this.limit = limit
    }

    fun inferiorLimit(inferiorLimit: Int){
        this.inferiorLimit = inferiorLimit
    }

    private fun loadLabel(){
        if(remoteKey.isNotEmpty()){
            doAsync {
                val labelValue = labelUseCase.findLabelOutLive(remoteKey)
                uiThread {
                    labelValue?.let { l ->  label.text = l.value }
                }
            }
        }
    }

    interface NumberPickerListener{
        fun onChangeNumber(number: Int)
    }
}