package com.xcaret.xcaret_hotel.view.config.navbottombar.util

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.StateListAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.config.neumorphism.CornerFamily
import com.xcaret.xcaret_hotel.view.config.neumorphism.NeumorphShapeAppearanceModel
import com.xcaret.xcaret_hotel.view.config.neumorphism.NeumorphShapeDrawable
import com.xcaret.xcaret_hotel.view.config.neumorphism.ShapeType
import org.jetbrains.anko.backgroundDrawable


const val ICON_STATE_ANIMATOR_DURATION: Long = 350

@SuppressLint("RestrictedApi")
internal fun ImageView.colorAnimator(
    @ColorInt from: Int,
    @ColorInt to: Int,
    durationInMillis: Long
): Animator = ValueAnimator.ofObject(ArgbEvaluator(), from, to).apply {
    duration = durationInMillis
    addUpdateListener { animator ->
        val color = animator.animatedValue as Int
        run { setColorFilter(color) }
    }
}

internal fun ImageView.setColorStateListAnimator(
    @ColorInt color: Int,
    @ColorInt unselectedColor: Int
) {
    val stateList = StateListAnimator().apply {
        addState(
            intArrayOf(android.R.attr.state_selected),
            colorAnimator(unselectedColor, color, ICON_STATE_ANIMATOR_DURATION)
        )
        addState(
            intArrayOf(),
            colorAnimator(color, unselectedColor, ICON_STATE_ANIMATOR_DURATION)
        )
    }

    stateListAnimator = stateList

    // Refresh the drawable state to avoid the unselected animation on view creation
    refreshDrawableState()
}


var DURATION = 350L
var ALPHA = 0.15f
internal fun TextView.expand(container: LinearLayout, iconColor: Int) {
    val bounds = Rect()
    //container.setCustomBackground(iconColor, ALPHA)
    container.setNeumorphisBackground()
    paint.apply {

        getTextBounds(text.toString(), 0, text.length, bounds)
        ValueAnimator.ofInt(0, bounds.width() + paddingLeft + 10).apply {
            addUpdateListener {
                if (it.animatedFraction == (0.0f)) {
                    visibility = View.INVISIBLE
                }
                layoutParams.apply {
                    width = it.animatedValue as Int
                }

                if (it.animatedFraction == (1.0f)) {
                    visibility = View.VISIBLE
                }
                requestLayout()
            }
            interpolator = LinearInterpolator()

            duration = DURATION
        }.start()


    }

}


internal fun TextView.collapse(
    container: LinearLayout,
    iconColor: Int
) {
    animate().alpha(0f).apply {
        setUpdateListener {
            layoutParams.apply {
                width = (width - (width * it.animatedFraction)).toInt()
            }
            if (it.animatedFraction == 1.0f) {
                visibility = View.GONE
                alpha = 1.0f
            }
            interpolator = LinearInterpolator()
            duration = DURATION
            container.setCustomBackground(iconColor, ALPHA - (ALPHA * it.animatedFraction))
            requestLayout()
        }
    }.start()

}

internal fun LinearLayout.setCustomBackground(color: Int, alpha: Float) {
    val containerBackground = GradientDrawable().apply {
        cornerRadius = 100f
        setTint(
            Color.argb(
                (Color.alpha(color) * alpha).toInt(),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
            )
        )
    }
    background = containerBackground
}

internal fun LinearLayout.setNeumorphisBackground(){
    val shappeAppareance = NeumorphShapeAppearanceModel.builder()
    shappeAppareance.setAllCorners(CornerFamily.ROUNDED, HotelXcaretApp.mContext.resources.getDimensionPixelSize(R.dimen.radius_default).toFloat())

    val colorLight = ContextCompat.getColor(context, R.color.colorBottomMenuLight)
    val colorDark = ContextCompat.getColor(context, R.color.colorBottomMenuDark)

    val neumorphisBackground = NeumorphShapeDrawable(context).apply {
        setInEditMode(false)
        setShapeType(ShapeType.PRESSED)
        setShadowElevation(context.resources.getDimension(R.dimen.elevation_mini))
        setShadowColorLight(colorLight)
        setShadowColorDark(colorDark)
        setTranslationZ(translationZ)
        setStrokeWidth(10f)
        setShapeAppearanceModel(shappeAppareance.build())
    }

    backgroundDrawable = neumorphisBackground
}

fun spToPx(sp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp,
        context.resources.displayMetrics
    ).toInt()
}
