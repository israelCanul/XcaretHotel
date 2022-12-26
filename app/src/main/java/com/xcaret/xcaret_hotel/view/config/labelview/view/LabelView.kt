package com.xcaret.xcaret_hotel.view.config.labelview.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.config.labelview.vm.LabelViewModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class LabelView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    private val viewModel: LabelViewModel = LabelViewModel()
    private val lifecycleOwner: LifecycleOwner? by lazy {
        context as? LifecycleOwner
    }

    init {
        init(attrs)
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(attrs: AttributeSet?){
        attrs?.let { attr ->
            val attributes = context.obtainStyledAttributes(attr, R.styleable.LabelViewStyle)
            try{
                viewModel.concatText.value = attributes.getString(R.styleable.LabelViewStyle_concatText) ?: ""
                viewModel.key.value = attributes.getString(R.styleable.LabelViewStyle_key) ?: ""
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
                label?.let {
                    val value = it.value?.replace("@", viewModel.concatText.value ?: "")
                    text = value
                }
            })
        } ?: kotlin.run {
            doAsync {
                val label = viewModel.findLabelOutLive()
                uiThread {
                    label?.let {
                        val value = it.value?.replace("@", viewModel.concatText.value ?: "")
                        text = value
                    }
                }
            }
        }
    }

    fun setKey(key: String){
        viewModel.key.value = key
        setObserver()
    }

    fun setConcatText(concat: String){
        viewModel.concatText.value = concat
        val currentText = text
        if(!currentText.isNullOrEmpty())
            text = currentText.toString().trim().replace("@", concat)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setObserver()
    }
}

@BindingAdapter("app:key")
fun key(labelView: LabelView, key: Int?){
    key?.let {
        labelView.setKey(labelView.context.getString(key))
    }
}

@BindingAdapter("app:key")
fun key(labelView: LabelView, key: String?){
    key?.let {
        labelView.setKey(key)
    }
}

@BindingAdapter("app:concat")
fun concat(labelView: LabelView, concat: String?){
    concat?.let {
        labelView.setConcatText(it)
    }
}

