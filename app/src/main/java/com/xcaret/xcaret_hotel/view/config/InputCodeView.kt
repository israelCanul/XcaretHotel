package com.xcaret.xcaret_hotel.view.config

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.config.shapeofview.shapes.RoundRectView
import kotlinx.android.synthetic.main.widget_input_code.view.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange

class InputCodeView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    interface inputCodeViewListener {
        fun onFinishedInput()
    }

    var listener: inputCodeViewListener? = null

    init {
        inflate(context, R.layout.widget_input_code, this)
        init()
    }

    private fun init(){
        etCode1.tag = CODE1
        etCode2.tag = CODE2
        etCode3.tag = CODE3
        etCode4.tag = CODE4
        etCode5.tag = CODE5
        etCode6.tag = CODE6

        addListeners(etCode1)
        addListeners(etCode2)
        addListeners(etCode3)
        addListeners(etCode4)
        addListeners(etCode5)
        addListeners(etCode6)
    }

    private fun addListeners(_view: EditText){

        val textChangeListener = object: TextWatcher{
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.isNullOrEmpty()) return
                val strClean = s.toString().trim()
                if(strClean.isNotEmpty()) {
                    _view.removeTextChangedListener(this)
                    _view.setText(strClean.last().toString())
                    changeFocus(_view.tag.toString())
                }
            }
        }

        _view.onFocusChange { v, hasFocus ->
            val parent = v.parent as RoundRectView
            if (hasFocus) {
                _view.addTextChangedListener(textChangeListener)
                activeBorder(parent, R.color.colorTextPrimary)
            }
            else {
                _view.removeTextChangedListener(textChangeListener)
                if(_view.text.toString().isNotEmpty()) activeBorder(parent, R.color.colorTextPrimary)
                else activeBorder(parent, R.color.colorInputOutline)
            }
        }
    }

    private fun activeBorder(_view: RoundRectView, colorRes: Int){
        _view.setBorderColor(ContextCompat.getColor(context, colorRes))
    }

    private fun changeFocus(tag: String){
        when(tag){
            CODE1 -> etCode2.requestFocus()
            CODE2 -> etCode3.requestFocus()
            CODE3 -> etCode4.requestFocus()
            CODE4 -> etCode5.requestFocus()
            CODE5 -> etCode6.requestFocus()
            CODE6 -> {

                Utils.hideKeyboardFrom(context, rootView)
                if(listener != null) listener?.onFinishedInput()

            }
        }
    }

    fun isValid(): Boolean{
        if(etCode6.text.isNullOrEmpty()) {
            activeBorder(layoutCode6, android.R.color.holo_red_light)
        }
        if(etCode5.text.isNullOrEmpty()) {
            activeBorder(layoutCode5, android.R.color.holo_red_light)
        }
        if(etCode4.text.isNullOrEmpty()) {
            activeBorder(layoutCode4, android.R.color.holo_red_light)
        }
        if(etCode3.text.isNullOrEmpty()) {
            activeBorder(layoutCode3, android.R.color.holo_red_light)
        }
        if(etCode2.text.isNullOrEmpty()) {
            activeBorder(layoutCode2, android.R.color.holo_red_light)
        }
        if(etCode1.text.isNullOrEmpty()) {
            activeBorder(layoutCode1, android.R.color.holo_red_light)
        }
        return getText().length == 6
    }

    fun initFocus(){
        etCode1.requestFocus()
    }

    fun clean(){
        etCode1.setText("")
        etCode2.setText("")
        etCode3.setText("")
        etCode4.setText("")
        etCode5.setText("")
        etCode6.setText("")
    }

    fun getText(): String{
        return "${etCode1.text}${etCode2.text}${etCode3.text}${etCode4.text}${etCode5.text}${etCode6.text}"
    }

    fun setOnFinishInputCodeListener(listener:inputCodeViewListener){
        this.listener = listener
    }

    companion object{
        const val CODE1 = "CODE1"
        const val CODE2 = "CODE2"
        const val CODE3 = "CODE3"
        const val CODE4 = "CODE4"
        const val CODE5 = "CODE5"
        const val CODE6 = "CODE6"
    }

}