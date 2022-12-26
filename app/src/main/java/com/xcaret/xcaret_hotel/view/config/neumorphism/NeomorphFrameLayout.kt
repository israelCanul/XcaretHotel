package com.xcaret.xcaret_hotel.view.config.neumorphism

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.xcaret.xcaret_hotel.R


class NeomorphFrameLayout: FrameLayout {
    //attributes
    private var SHAPE_TYPE: String? = null
    private var SHADOW_TYPE: String? = null
    private var CORNER_RADIUS = 0
    private var ELEVATION = 0
    private var HIGHLIGHT_COLOR = 0
    private var SHADOW_COLOR = 0
    private var BACKGROUND_COLOR = 0
    private var LAYER_TYPE = 0
    private var SHADOW_VISIBLE = false

    //global variables
    private var SHAPE_PADDING = 0

    //constants
    private val SHAPE_TYPE_RECTANGLE = "1"
    private val SHAPE_TYPE_CIRCLE = "2"
    private val SHADOW_TYPE_OUTER = "1"
    private val SHADOW_TYPE_INNER = "2"

    //global objects
    private lateinit var basePaint: Paint
    private lateinit var paintShadow: Paint
    private lateinit var paintHighLight: Paint
    private lateinit var basePath: Path
    private lateinit var pathShadow: Path
    private lateinit var pathHighlight: Path
    private lateinit var rectangle: RectF


    constructor(context: Context): super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        getAttrs(context, attrs)
        initPaints()
        rectangle = RectF(
            SHAPE_PADDING.toFloat(),
            SHAPE_PADDING.toFloat(),
            (this.width - SHAPE_PADDING).toFloat(),
            (this.height - SHAPE_PADDING).toFloat()
        )
    }

    fun getAttrs(context: Context, attrs: AttributeSet?) {
        val defaultElevation = context.resources.getDimension(R.dimen.neomorph_view_elevation).toInt()
        val defaultCornerRadius = context.resources.getDimension(R.dimen.neomorph_view_corner_radius).toInt()
        if (attrs != null) {
            //get attrs array
            val a: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.NeomorphFrameLayout)
            //get all attributes
            SHAPE_TYPE = a.getString(R.styleable.NeomorphFrameLayout_neomorph_view_type)
            if (SHAPE_TYPE == null) {
                SHAPE_TYPE = SHAPE_TYPE_RECTANGLE
            }
            SHADOW_TYPE = a.getString(R.styleable.NeomorphFrameLayout_neomorph_shadow_type)
            if (SHADOW_TYPE == null) {
                SHADOW_TYPE = SHADOW_TYPE_OUTER
            }
            ELEVATION = a.getDimensionPixelSize(
                R.styleable.NeomorphFrameLayout_neomorph_elevation,
                defaultElevation
            )
            CORNER_RADIUS = a.getDimensionPixelSize(
                R.styleable.NeomorphFrameLayout_neomorph_corner_radius,
                defaultCornerRadius
            )
            BACKGROUND_COLOR = a.getColor(
                R.styleable.NeomorphFrameLayout_neomorph_background_color,
                ContextCompat.getColor(context, R.color.colorBackground2)
            )
            SHADOW_COLOR = a.getColor(
                R.styleable.NeomorphFrameLayout_neomorph_shadow_color,
                ContextCompat.getColor(context, R.color.colorShadowDark)
            )
            HIGHLIGHT_COLOR = a.getColor(
                R.styleable.NeomorphFrameLayout_neomorph_highlight_color,
                ContextCompat.getColor(context, R.color.colorShadowLight)
            )
            SHADOW_VISIBLE =
                a.getBoolean(R.styleable.NeomorphFrameLayout_neomorph_shadow_visible, true)
            val layerType =
                a.getString(R.styleable.NeomorphFrameLayout_neomorph_layer_type)
            LAYER_TYPE = if (layerType == null || layerType == "1") {
                View.LAYER_TYPE_SOFTWARE //SW by default
            } else View.LAYER_TYPE_HARDWARE
            a.recycle()
        } else {
            SHAPE_TYPE = "rectangle"
            ELEVATION = defaultElevation
            CORNER_RADIUS = defaultCornerRadius
            BACKGROUND_COLOR = ContextCompat.getColor(context, R.color.colorBackground2)
            SHADOW_COLOR = ContextCompat.getColor(context, R.color.colorShadowDark)
            HIGHLIGHT_COLOR = ContextCompat.getColor(context, R.color.colorShadowLight)
            LAYER_TYPE = View.LAYER_TYPE_SOFTWARE
            SHADOW_VISIBLE = true
            SHADOW_TYPE = SHADOW_TYPE_OUTER
        }
    }

    private fun initPaints() {
        basePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        paintShadow = Paint(Paint.ANTI_ALIAS_FLAG)
        paintHighLight = Paint(Paint.ANTI_ALIAS_FLAG)
        basePaint.color = BACKGROUND_COLOR
        paintShadow.color = BACKGROUND_COLOR
        paintHighLight.color = BACKGROUND_COLOR
        if (SHADOW_VISIBLE) {
            paintShadow.setShadowLayer(ELEVATION.toFloat(), ELEVATION.toFloat(), ELEVATION.toFloat(), SHADOW_COLOR)
            paintHighLight.setShadowLayer(ELEVATION.toFloat(), -ELEVATION.toFloat(), -ELEVATION.toFloat(), HIGHLIGHT_COLOR)
        }
        basePath = Path()
        pathHighlight = Path()
        pathShadow = Path()

        //TODO: make SHAPE_PADDING dynamic
        SHAPE_PADDING = ELEVATION * 2
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE, null)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectangle = RectF(
            SHAPE_PADDING.toFloat(),
            SHAPE_PADDING.toFloat(),
            (this.width - SHAPE_PADDING).toFloat(),
            (this.height - SHAPE_PADDING).toFloat()
        )
        resetPath(w, h)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setPadding(SHAPE_PADDING, SHAPE_PADDING, SHAPE_PADDING, SHAPE_PADDING)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (SHADOW_TYPE) {
            SHADOW_TYPE_INNER -> canvas.clipPath(basePath)
            SHADOW_TYPE_OUTER -> {
            }
            else -> {
            }
        }
        if (SHADOW_VISIBLE) {
            paintShadow.alpha = 155
            paintHighLight.alpha = 155
        } else {
            paintShadow.alpha = 0
            paintHighLight.alpha = 0
        }

        if(SHADOW_TYPE == SHADOW_TYPE_INNER) {
            canvas.drawPath(basePath, basePaint)
            canvas.drawPath(pathShadow, paintShadow)
            canvas.drawPath(pathHighlight, paintHighLight)
        }else {
            canvas.drawPath(pathShadow, paintShadow)
            canvas.drawPath(pathHighlight, paintHighLight)
            canvas.drawPath(basePath, basePaint)
        }
    }

    private fun resetPath(w: Int, h: Int) {
        basePath.reset()
        pathHighlight.reset()
        pathShadow.reset()
        when (SHAPE_TYPE) {
            SHAPE_TYPE_CIRCLE -> {
                //get max suitable diameter, which is the smallest dimension
                val maxDiameter =
                    if (this.width < this.height) this.width else this.height
                val radius = maxDiameter / 2 - SHAPE_PADDING
                basePath.addCircle((w / 2).toFloat(), (h / 2).toFloat(), radius.toFloat(), Path.Direction.CW)
                pathHighlight.addCircle((w / 2).toFloat(), (h / 2).toFloat(), radius.toFloat(), Path.Direction.CW)
                pathShadow.addCircle((w / 2).toFloat(), (h / 2).toFloat(), radius.toFloat(), Path.Direction.CW)
            }
            SHAPE_TYPE_RECTANGLE -> {
                basePath.addRoundRect(rectangle, CORNER_RADIUS.toFloat(),
                    CORNER_RADIUS.toFloat(), Path.Direction.CW)
                pathHighlight.addRoundRect(
                    rectangle,
                    CORNER_RADIUS.toFloat(),
                    CORNER_RADIUS.toFloat(),
                    Path.Direction.CW
                )
                pathShadow.addRoundRect(rectangle, CORNER_RADIUS.toFloat(),
                    CORNER_RADIUS.toFloat(), Path.Direction.CW)
            }
            else -> {
                basePath.addRoundRect(rectangle, CORNER_RADIUS.toFloat(),
                    CORNER_RADIUS.toFloat(), Path.Direction.CW)
                pathHighlight.addRoundRect(
                    rectangle,
                    CORNER_RADIUS.toFloat(),
                    CORNER_RADIUS.toFloat(),
                    Path.Direction.CW
                )
                pathShadow.addRoundRect(rectangle, CORNER_RADIUS.toFloat(),
                    CORNER_RADIUS.toFloat(), Path.Direction.CW)
            }
        }
        if (SHADOW_TYPE == SHADOW_TYPE_INNER) {
            if (!pathHighlight.isInverseFillType) {
                pathHighlight.toggleInverseFillType()
            }
            if (!pathShadow.isInverseFillType) {
                pathShadow.toggleInverseFillType()
            }
        }
        basePath.close()
        pathHighlight.close()
        pathShadow.close()
    }

    fun setNeumorphBackgroundColor(backgroundColor: Int){
        BACKGROUND_COLOR = backgroundColor
        initPaints()
        resetPath(width, height)
        invalidate()
    }

    fun setShadowInner() {
        SHADOW_VISIBLE = true
        SHADOW_TYPE = SHADOW_TYPE_INNER
        initPaints()
        resetPath(width, height)
        invalidate()
    }

    fun setShadowOuter() {
        SHADOW_VISIBLE = true
        SHADOW_TYPE = SHADOW_TYPE_OUTER
        initPaints()
        resetPath(width, height)
        invalidate()
    }

    fun switchShadowType() {
        SHADOW_VISIBLE = true
        SHADOW_TYPE = if (SHADOW_TYPE == SHADOW_TYPE_INNER) {
            SHADOW_TYPE_OUTER
        } else SHADOW_TYPE_INNER
        initPaints()
        resetPath(width, height)
        invalidate()
    }

    fun setShadowNone() {
        SHADOW_VISIBLE = false
        initPaints()
        resetPath(width, height)
        invalidate()
    }
}