package com.xcaret.xcaret_hotel.view.config

import android.annotation.SuppressLint
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import androidx.annotation.ColorRes
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import com.xcaret.xcaret_hotel.R


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class Shadow private constructor(private val context: Context?) {
    var layersSize: Int
        private set
    var shadowLeft: Int
        private set
    var shadowTop: Int
        private set
    var shadowRight: Int
        private set
    private var shadowBottom: Int

    @ColorRes
    var backgroundColor: Int
        private set

    @ColorRes
    private var resShadowColor: Int
    var cornerRadius: Int
        private set
    var alpha: Int
        private set

    fun set(view: View): LayerDrawable {
        val layerDrawable = create()
        val back = layerDrawable.getDrawable(layersSize - 1) as PaintDrawable
        back.setPadding(
            view.paddingLeft - shadowLeft * (layersSize - 1),
            view.paddingTop - shadowTop * (layersSize - 1),
            view.paddingRight - shadowRight * (layersSize - 1),
            view.paddingBottom - shadowBottom * (layersSize - 1)
        )
        layerDrawable.setDrawableByLayerId(layersSize - 1, back)
        view.background = layerDrawable
        return layerDrawable
    }

    fun create(): LayerDrawable {
        val layers = arrayOfNulls<Drawable>(
            layersSize
        )
        val deltaAlpha = alpha / layersSize
        var alpha = deltaAlpha
        var sd: PaintDrawable
        for (i in 0 until layersSize - 1) {
            sd = PaintDrawable(ContextCompat.getColor(context!!, resShadowColor))
            alpha += deltaAlpha
            sd.alpha = alpha
            sd.setPadding(shadowLeft, shadowTop, shadowRight, shadowBottom)
            if (cornerRadius > 0) sd.setCornerRadius(cornerRadius.toFloat())
            layers[i] = sd
        }
        sd = PaintDrawable(ContextCompat.getColor(context!!, backgroundColor))
        if (cornerRadius > 0) sd.setCornerRadius(cornerRadius.toFloat())
        layers[layersSize - 1] = sd
        return LayerDrawable(layers)
    }

    fun shadowAll(shadow: Int): Shadow {
        shadowLeft = shadow
        shadowTop = shadow
        shadowRight = shadow
        shadowBottom = shadow
        return this
    }

    fun getShadowBottom(): Float {
        return shadowBottom.toFloat()
    }

    fun shadowBottom(shadowBottom: Int): Shadow {
        this.shadowBottom = shadowBottom
        return this
    }

    fun shadowLeft(shadowLeft: Int): Shadow {
        this.shadowLeft = shadowLeft
        return this
    }

    fun shadowRight(shadowRight: Int): Shadow {
        this.shadowRight = shadowRight
        return this
    }

    fun shadowTop(shadowTop: Int): Shadow {
        this.shadowTop = shadowTop
        return this
    }

    fun alpha(@IntRange(from = 1, to = 255) alpha: Int): Shadow {
        if (alpha > 0 && alpha < 256) this.alpha = alpha else Log.e(
            TAG,
            "alpha: $alpha  IS NOT IN RANGE[1..255]"
        )
        return this
    }

    fun radius(radius: Int): Shadow {
        cornerRadius = radius
        return this
    }

    fun backgroundColor(@ColorRes backgroundColor: Int): Shadow {
        this.backgroundColor = backgroundColor
        return this
    }

    fun getshadowColor(): Int {
        return resShadowColor
    }

    fun shadowColor(@ColorRes shadowColor: Int): Shadow {
        resShadowColor = shadowColor
        return this
    }

    fun blur(@IntRange(from = 1) blur: Int): Shadow {
        if (blur > 1) layersSize = blur else Log.e(
            TAG,
            "blur: $blur IS NOT IN RANGE[2..)"
        )
        return this
    }

    object Builder {
        fun init(context: Context): ShadowSize {
            return BuilderImpl().context(context)
        }

        interface ShadowSize {
            fun shadowBottom(shadowBottom: Int): ShadowProperties?
            fun shadowTop(shadowTop: Int): ShadowProperties?
            fun shadowLeft(shadowLeft: Int): ShadowProperties?
            fun shadowRight(shadowRight: Int): ShadowProperties?
            fun shadowAll(shadow: Int): ShadowParameters?
        }

        interface ShadowProperties : ShadowSize, ShadowParam
        interface ShadowParam {
            fun alpha(@IntRange(from = 1, to = 255) alpha: Int): ShadowParameters?
            fun blur(@IntRange(from = 1) blur: Int): ShadowParameters?
            fun defaultParameters(): ShadowParameters?
            fun radius(radius: Int): ShadowParameters?
            fun defaultAll(): ShadowColors?
        }

        interface ShadowParameters : ShadowColor, ShadowParam
        interface ShadowColor {
            fun backgroundColorRes(@ColorRes backgroundColor: Int): ShadowColors?
            fun shadowColorRes(@ColorRes shadowColor: Int): ShadowColors?
            fun defaultColors(): ShadowColors?
        }

        interface ShadowColors : ShadowColor, Build
        interface Build {
            fun build(): Shadow?
        }

        private class BuilderImpl : ShadowProperties, ShadowParameters,
            ShadowColors {
            private var context: Context? = null
            private var blur = 0
            private var radius = 0
            private var alpha = 0
            private var left = 0
            private var right = 0
            private var top = 0
            private var bottom = 0

            @ColorRes
            private var backColor = 0

            @ColorRes
            private var shadowColor = 0
            fun context(context: Context): ShadowSize {
                this.context = context.applicationContext
                return this
            }

            override fun shadowBottom(shadowBottom: Int): ShadowProperties {
                bottom = shadowBottom
                return this
            }

            override fun shadowTop(shadowTop: Int): ShadowProperties {
                top = shadowTop
                return this
            }

            override fun shadowLeft(shadowLeft: Int): ShadowProperties {
                left = shadowLeft
                return this
            }

            override fun shadowRight(shadowRight: Int): ShadowProperties {
                right = shadowRight
                return this
            }

            override fun shadowAll(shadow: Int): ShadowParameters {
                shadowTop(shadow)
                shadowBottom(shadow)
                shadowLeft(shadow)
                shadowRight(shadow)
                return this
            }

            override fun radius(radius: Int): ShadowParameters {
                this.radius = radius
                return this
            }

            override fun defaultAll(): ShadowColors {
                return this
            }

            /**
             * Set the alpha level of the result drawable [0..255].
             * @param alpha the end alpha
             */
            override fun alpha(@IntRange(from = 1, to = 255) alpha: Int): ShadowParameters {
                this.alpha = alpha
                return this
            }

            override fun blur(@IntRange(from = 1) blur: Int): ShadowParameters {
                this.blur = blur
                return this
            }

            override fun defaultParameters(): ShadowParameters {
                return this
            }

            override fun backgroundColorRes(@ColorRes backgroundColor: Int): ShadowColors {
                backColor = backgroundColor
                return this
            }

            override fun shadowColorRes(@ColorRes shadowColor: Int): ShadowColors {
                this.shadowColor = shadowColor
                return this
            }

            override fun defaultColors(): ShadowColors {
                return this
            }

            @SuppressLint("ResourceType")
            override fun build(): Shadow {
                val shadow = Shadow(context)
                if (left > 0) shadow.shadowLeft(left)
                if (right > 0) shadow.shadowRight(right)
                if (top > 0) shadow.shadowTop(top)
                if (bottom > 0) shadow.shadowBottom(bottom)
                if (radius > 0) shadow.radius(radius)
                if (blur > 0) shadow.blur(blur)
                if (alpha > 0) shadow.alpha(alpha)
                if (shadowColor > 0) shadow.shadowColor(shadowColor)
                if (backColor > 0) shadow.backgroundColor(backColor)
                return shadow
            }
        }
    }

    companion object {
        private const val TAG = "Shadow"
        private var DEFAULT_BLUR = 6
        private var DEFAULT_ALPHA = 50

        @ColorRes
        private var DEFAULT_BACK_COLOR = android.R.color.white

        @ColorRes
        private var DEFAULT_SHADOW_COLOR = R.color.colorShadowDark
        fun setDefaultAlpha(@IntRange(from = 1, to = 255) alpha: Int) {
            DEFAULT_ALPHA = alpha
        }

        fun setDefaultBlur(@IntRange(from = 1) blur: Int) {
            DEFAULT_BLUR = blur
        }

        fun setDefaultBackgroundColorRes(@ColorRes backgroundColorRes: Int) {
            DEFAULT_BACK_COLOR = backgroundColorRes
        }

        fun setDefaultShadowColorRes(@ColorRes shadowColorRes: Int) {
            DEFAULT_SHADOW_COLOR = shadowColorRes
        }
    }

    init {
        layersSize = DEFAULT_BLUR
        alpha = DEFAULT_ALPHA
        shadowLeft = 0
        shadowTop = 0
        shadowRight = 0
        shadowBottom = 0
        cornerRadius = 0
        backgroundColor = DEFAULT_BACK_COLOR
        resShadowColor = DEFAULT_SHADOW_COLOR
    }
}