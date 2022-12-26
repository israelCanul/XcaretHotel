package com.xcaret.xcaret_hotel.view.config.inputview.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.config.inputview.vm.InputViewModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class InputView(context: Context, attrs: AttributeSet): AppCompatEditText(context, attrs) {
    private val viewModel: InputViewModel = InputViewModel()
    private val lifecycleOwner: LifecycleOwner? by lazy {
        context as? LifecycleOwner
    }

    init {
        init(attrs)
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(attrs: AttributeSet?){
        attrs?.let { attr ->
            val attributes = context.obtainStyledAttributes(attr, R.styleable.InputViewStyle)
            try{
                viewModel.key.value = attributes.getString(R.styleable.InputViewStyle_key_hint) ?: ""
            }catch (e: Exception){}
            finally {
                attributes.recycle()
            }
        }
    }

    private fun setObserver(){
        lifecycleOwner?.let { mLifecycleOwner ->
            viewModel.findLabel().removeObservers(mLifecycleOwner)
            viewModel.findLabel().observe(mLifecycleOwner, Observer { label ->
                label?.let { hint = it.value }
            })
        } ?: kotlin.run {
            doAsync {
                val label = viewModel.findLabelOutLive()
                uiThread {
                    label?.let { hint = it.value }
                }
            }
        }
    }

    fun setKey(key: String){
        viewModel.key.value = key
        setObserver()
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        try {
            removeTextChangedListener(viewModel.hasTextWatcher.value)

        viewModel.hasTextWatcher.value = watcher
        }catch (e: java.lang.Exception){ e.printStackTrace() }
        super.addTextChangedListener(watcher)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setObserver()
    }
}

@BindingAdapter("android:inputType")
fun inputType(editText: AppCompatEditText, inputType: Int?){
    inputType?.let { editText.inputType = it }
}

@BindingAdapter("android:keyHint")
fun setKey(inputView: InputView, key: String?){
    key?.let{ inputView.setKey(it) }
}

@BindingAdapter("android:defaultHint")
fun defaultHint(inputView: InputView, key: String?){
    inputView.hint = key
}

@BindingAdapter("android:maxLength")
fun maxLength(inputView: InputView, maxLength: Int?){
    maxLength?.let { max ->
        inputView.filters = arrayOf(InputFilter.LengthFilter(max))
    }
}

fun InputView.toggleVisiblePassword(icon: ImageView){
    transformationMethod = if(transformationMethod == null) PasswordTransformationMethod()
    else null
    val result = transformationMethod != null
    if(result) icon.setImageResource(R.drawable.ic_hidden_password)
    else icon.setImageResource(R.drawable.ic_show_password)
    setSelection(text.toString().length)
}