package com.xcaret.xcaret_hotel.view.config

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.transition.TransitionManager
import com.xcaret.xcaret_hotel.R
import kotlinx.android.synthetic.main.switch_neumorphism_view.view.*
import org.jetbrains.anko.textColor
import kotlin.Exception

class SwitchNeumorphisView: ConstraintLayout {

    private val constraintSetOrigin = ConstraintSet()
    private val constraintSetDestination = ConstraintSet()
    private var onChangeListener: OnChangeListener? = null

    private val lifecycleOwner: LifecycleOwner? by lazy {
        context as? LifecycleOwner
    }
    private val isOriginPosLiveData = MutableLiveData<Boolean>(true)

    init {
        inflate(context, R.layout.switch_neumorphism_view, this)
        constraintSetOrigin.clone(layoutContent)
        constraintSetDestination.clone(layoutContent)

        constraintSetDestination.clear(R.id.bgAnim2, ConstraintSet.LEFT)
        constraintSetDestination.connect(R.id.bgAnim2, ConstraintSet.RIGHT, R.id.bg, ConstraintSet.RIGHT)
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?){
        setObservers()
        setListeners()

    }

    fun setTextValues(textLeft:String, textRight:String){
        try {
            txtLeftText.setKey(textLeft)
            txtRightText.setKey(textRight)
        }catch (e:Exception){

        }
    }

    fun setOnChangeListener(onChangeListener: OnChangeListener){
        this.onChangeListener = onChangeListener
    }

    private fun setObservers(){
        lifecycleOwner?.let {
            isOriginPosLiveData.observe(it, Observer {
                animSwitch()
            })
        }
    }

    private fun setListeners(){
        txtLeftText.setOnClickListener {
            val current = isOriginPosLiveData.value ?: true
            isOriginPosLiveData.value = !current
        }

        txtRightText.setOnClickListener {
            val current = isOriginPosLiveData.value ?: true
            isOriginPosLiveData.value = !current
        }
    }

    private fun animSwitch(){
        TransitionManager.beginDelayedTransition(layoutContent)
        val constraint = if (isOriginPosLiveData.value == true) constraintSetOrigin else constraintSetDestination
        if(isOriginPosLiveData.value == true){
            animTextColor(R.color.colorTextPrimary, R.color.colorSwitchText, txtLeftText)
            animTextColor(R.color.colorSwitchText, R.color.colorTextPrimary, txtRightText)
        }else{
            animTextColor(R.color.colorTextPrimary, R.color.colorSwitchText, txtRightText)
            animTextColor(R.color.colorSwitchText, R.color.colorTextPrimary, txtLeftText)
        }
        constraint.applyTo(layoutContent)
        onChangeListener?.onChange(isOriginPosLiveData.value!!)
    }

    private fun animTextColor(from: Int, to: Int, view: TextView){
        val colorFrom = ContextCompat.getColor(context, from)
        val colorTo = ContextCompat.getColor(context, to)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.addUpdateListener {
            view.textColor = it.animatedValue as Int
        }
        colorAnimation.start();
    }

    interface OnChangeListener{
        fun onChange(leftActive: Boolean)
    }
}