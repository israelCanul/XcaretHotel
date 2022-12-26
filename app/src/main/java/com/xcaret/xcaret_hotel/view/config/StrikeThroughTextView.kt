package com.xcaret.xcaret_hotel.view.config

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class StrikeThroughTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    init {
        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }
}