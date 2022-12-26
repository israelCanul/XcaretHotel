package com.xcaret.xcaret_hotel.view.config

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation


fun View.expand(){
    val matchParentMeasureSpec =
        View.MeasureSpec.makeMeasureSpec((parent as View).width, View.MeasureSpec.EXACTLY)
    val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    measure(matchParentMeasureSpec, wrapContentMeasureSpec)
    val targetHeight: Int = measuredHeight

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    layoutParams.height = 1
    visibility = View.VISIBLE
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            layoutParams.height = if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
            requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // Expansion speed of 1dp/ms
    a.duration = (targetHeight / context.resources.displayMetrics.density).toLong()
    startAnimation(a)
}

fun View.collapse(){
    val initialHeight: Int = measuredHeight

    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                makeGone()
            } else {
                layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // Collapse speed of 1dp/ms
    a.duration =(initialHeight / context.resources.displayMetrics.density).toLong()
    startAnimation(a)
}

class ResizeWidthAnimation(private val mView: View, private val mWidth: Int) : Animation() {
    private val mStartWidth: Int = mView.width

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        mView.layoutParams.width = mStartWidth + ((mWidth - mStartWidth) * interpolatedTime).toInt()
        mView.requestLayout()
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}

