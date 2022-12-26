package com.xcaret.xcaret_hotel.view.config.neumorphism.internal.shape

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import com.xcaret.xcaret_hotel.view.config.neumorphism.NeumorphShapeDrawable

internal class BasinShape(drawableState: NeumorphShapeDrawable.NeumorphShapeDrawableState) : Shape {

    private val shadows = listOf(
        FlatShape(drawableState),
        PressedShape(drawableState)
    )

    override fun setDrawableState(newDrawableState: NeumorphShapeDrawable.NeumorphShapeDrawableState) {
        shadows.forEach { it.setDrawableState(newDrawableState) }
    }

    override fun draw(canvas: Canvas, outlinePath: Path) {
        shadows.forEach { it.draw(canvas, outlinePath) }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        shadows.forEach { it.updateShadowBitmap(bounds) }
    }
}
