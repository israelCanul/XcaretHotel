package com.xcaret.xcaret_hotel.view.config.neumorphism.internal.shape

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import com.xcaret.xcaret_hotel.view.config.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState

internal interface Shape {
    fun setDrawableState(newDrawableState: NeumorphShapeDrawableState)
    fun draw(canvas: Canvas, outlinePath: Path)
    fun updateShadowBitmap(bounds: Rect)
}
