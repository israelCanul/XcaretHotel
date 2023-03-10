package com.xcaret.xcaret_hotel.view.config.autofittextview

import android.content.res.Resources
import android.os.Build
import android.text.*
import android.text.method.SingleLineTransformationMethod
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.xcaret.xcaret_hotel.R
import java.util.*


/**
 * A helper class to enable automatically resizing [TextView]`s `textSize` to fit
 * within its bounds.
 *
 * @attr ref R.styleable.AutofitTextView_sizeToFit
 * @attr ref R.styleable.AutofitTextView_minTextSize
 * @attr ref R.styleable.AutofitTextView_precision
 */
class AutofitHelper private constructor(view: TextView) {
    // Attributes
    private val mTextView: TextView
    private val mPaint: TextPaint

    /**
     * Original textSize of the TextView.
     */
    private var mTextSize = 0f

    /**
     * @see TextView.getMaxLines
     */
    var maxLines: Int
        private set

    /**
     * Returns the minimum size (in pixels) of the text.
     */
    var minTextSize: Float
        private set

    /**
     * Returns the maximum size (in pixels) of the text.
     */
    var maxTextSize: Float
        private set

    /**
     * Returns whether or not automatically resizing text is enabled.
     */
    var isEnabled = false
        private set
    private var mIsAutofitting = false
    private var mListeners: MutableList<OnTextSizeChangeListener>? = null
    private val mTextWatcher: TextWatcher = AutofitTextWatcher()
    private val mOnLayoutChangeListener: View.OnLayoutChangeListener =
        AutofitOnLayoutChangeListener()

    /**
     * Adds an [OnTextSizeChangeListener] to the list of those whose methods are called
     * whenever the [TextView]'s `textSize` changes.
     */
    fun addOnTextSizeChangeListener(listener: OnTextSizeChangeListener): AutofitHelper {
        if (mListeners == null) {
            mListeners = ArrayList()
        }
        mListeners!!.add(listener)
        return this
    }

    /**
     * Removes the specified [OnTextSizeChangeListener] from the list of those whose methods
     * are called whenever the [TextView]'s `textSize` changes.
     */
    fun removeOnTextSizeChangeListener(listener: OnTextSizeChangeListener?): AutofitHelper {
        if (mListeners != null) {
            mListeners!!.remove(listener)
        }
        return this
    }

    /**
     * Returns the amount of precision used to calculate the correct text size to fit within its
     * bounds.
     */
    fun getPrecision(): Float {
        return precision
    }

    /**
     * Set the amount of precision used to calculate the correct text size to fit within its
     * bounds. Lower precision is more precise and takes more time.
     *
     * @param precision The amount of precision.
     */
    fun setPrecision(precision: Float): AutofitHelper {
        if (Companion.precision.compareTo(precision) != 0) {
            Companion.precision = precision
            autofit()
        }
        return this
    }


    /**
     * Set the minimum text size to the given value, interpreted as "scaled pixel" units. This size
     * is adjusted based on the current density and user font size preference.
     *
     * @param size The scaled pixel size.
     * @attr ref me.grantland.R.styleable#AutofitTextView_minTextSize
     */
    fun setMinTextSize(size: Float): AutofitHelper {
        return setMinTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    /**
     * Set the minimum text size to a given unit and value. See TypedValue for the possible
     * dimension units.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     * @attr ref me.grantland.R.styleable#AutofitTextView_minTextSize
     */
    fun setMinTextSize(unit: Int, size: Float): AutofitHelper {
        val context = mTextView.context
        var r = Resources.getSystem()
        if (context != null) {
            r = context.resources
        }
        setRawMinTextSize(TypedValue.applyDimension(unit, size, r.displayMetrics))
        return this
    }

    private fun setRawMinTextSize(size: Float) {
        if (java.lang.Float.compare(size, minTextSize) != 0) {
            minTextSize = size
            autofit()
        }
    }

    /**
     * Set the maximum text size to the given value, interpreted as "scaled pixel" units. This size
     * is adjusted based on the current density and user font size preference.
     *
     * @param size The scaled pixel size.
     * @attr ref android.R.styleable#TextView_textSize
     */
    fun setMaxTextSize(size: Float): AutofitHelper {
        return setMaxTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    /**
     * Set the maximum text size to a given unit and value. See TypedValue for the possible
     * dimension units.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     * @attr ref android.R.styleable#TextView_textSize
     */
    fun setMaxTextSize(unit: Int, size: Float): AutofitHelper {
        val context = mTextView.context
        var r = Resources.getSystem()
        if (context != null) {
            r = context.resources
        }
        setRawMaxTextSize(TypedValue.applyDimension(unit, size, r.displayMetrics))
        return this
    }

    private fun setRawMaxTextSize(size: Float) {
        if (java.lang.Float.compare(size, maxTextSize) != 0) {
            maxTextSize = size
            autofit()
        }
    }

    /**
     * @see TextView.setMaxLines
     */
    fun setMaxLines(lines: Int): AutofitHelper {
        if (maxLines != lines) {
            maxLines = lines
            autofit()
        }
        return this
    }

    /**
     * Set the enabled state of automatically resizing text.
     */
    fun setEnabled(enabled: Boolean): AutofitHelper {
        if (isEnabled != enabled) {
            isEnabled = enabled
            if (enabled) {
                mTextView.addTextChangedListener(mTextWatcher)
                mTextView.addOnLayoutChangeListener(mOnLayoutChangeListener)
                autofit()
            } else {
                mTextView.removeTextChangedListener(mTextWatcher)
                mTextView.removeOnLayoutChangeListener(mOnLayoutChangeListener)
                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize)
            }
        }
        return this
    }

    /**
     * Returns the original text size of the View.
     *
     * @see TextView.getTextSize
     */
    /**
     * Set the original text size of the View.
     *
     * @see TextView.setTextSize
     */
    var textSize: Float
        get() = mTextSize
        set(size) {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
        }

    /**
     * Set the original text size of the View.
     *
     * @see TextView.setTextSize
     */
    fun setTextSize(unit: Int, size: Float) {
        if (mIsAutofitting) {
            // We don't want to update the TextView's actual textSize while we're autofitting
            // since it'd get set to the autofitTextSize
            return
        }
        val context = mTextView.context
        var r = Resources.getSystem()
        if (context != null) {
            r = context.resources
        }
        setRawTextSize(TypedValue.applyDimension(unit, size, r.displayMetrics))
    }

    private fun setRawTextSize(size: Float) {
        if (java.lang.Float.compare(size, mTextSize) != 0) {
            mTextSize = size
        }
    }

    private fun autofit() {
        val oldTextSize = mTextView.textSize
        val textSize: Float
        mIsAutofitting = true
        autofit(mTextView, mPaint, minTextSize, maxTextSize, maxLines)
        mIsAutofitting = false
        textSize = mTextView.textSize
        if (java.lang.Float.compare(textSize, oldTextSize) != 0) {
            sendTextSizeChange(textSize, oldTextSize)
        }
    }

    private fun sendTextSizeChange(textSize: Float, oldTextSize: Float) {
        if (mListeners == null) {
            return
        }
        for (listener in mListeners!!) {
            listener.onTextSizeChange(textSize, oldTextSize)
        }
    }

    private inner class AutofitTextWatcher : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
            // do nothing
        }

        override fun onTextChanged(
            charSequence: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
            autofit()
        }

        override fun afterTextChanged(editable: Editable) {
            // do nothing
        }
    }

    /**
     * Sonar is showing an error for onLayoutChange but onLayoutChange is Android Default method , we cannot change the parameters.
     */
    private inner class AutofitOnLayoutChangeListener :
        View.OnLayoutChangeListener {
        override fun onLayoutChange(
            view: View, left: Int, top: Int, right: Int, bottom: Int,
            oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
        ) {
            autofit()
        }
    }

    /**
     * When an object of a type is attached to an `AutofitHelper`, its methods will be called
     * when the `textSize` is changed.
     */
    interface OnTextSizeChangeListener {
        /**
         * This method is called to notify you that the size of the text has changed to
         * `textSize` from `oldTextSize`.
         */
        fun onTextSizeChange(textSize: Float, oldTextSize: Float)
    }

    companion object {
        /**
         * Returns the amount of precision used to calculate the correct text size to fit within its
         * bounds.
         */
        var precision: Float = 0f

        // Minimum size of the text in pixels
        private const val DEFAULT_MIN_TEXT_SIZE = 8 //sp

        // How precise we want to be when reaching the target textWidth size
        private const val DEFAULT_PRECISION = 0.5f
        /**
         * Creates a new instance of `AutofitHelper` that wraps a [TextView] and enables
         * automatically sizing the text to fit.
         */
        /**
         * Creates a new instance of `AutofitHelper` that wraps a [TextView] and enables
         * automatically sizing the text to fit.
         */
        /**
         * Creates a new instance of `AutofitHelper` that wraps a [TextView] and enables
         * automatically sizing the text to fit.
         */
        @JvmOverloads
        fun create(
            view: TextView,
            attrs: AttributeSet? = null,
            defStyle: Int = 0
        ): AutofitHelper {
            val helper = AutofitHelper(view)
            var sizeToFit = true
            if (attrs != null) {
                val context = view.context
                var minTextSize = helper.minTextSize.toInt()
                var precision: Float = Companion.precision
                val ta = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.AutofitTextView,
                    defStyle,
                    0
                )
                sizeToFit = ta.getBoolean(R.styleable.AutofitTextView_sizeToFit, sizeToFit)
                minTextSize = ta.getDimensionPixelSize(
                    R.styleable.AutofitTextView_minTextSize,
                    minTextSize
                )
                precision = ta.getFloat(R.styleable.AutofitTextView_precision, precision)
                ta.recycle()
                helper.setMinTextSize(TypedValue.COMPLEX_UNIT_PX, minTextSize.toFloat())
                    .setPrecision(precision)
            }
            helper.setEnabled(sizeToFit)
            return helper
        }

        /**
         * Re-sizes the textSize of the TextView so that the text fits within the bounds of the View.
         */
        private fun autofit(
            view: TextView, paint: TextPaint, minTextSize: Float, maxTextSize: Float,
            maxLines: Int
        ) {
            val targetWidth = view.width - view.paddingLeft - view.paddingRight
            if (targetWidth <= 0 || maxLines <= 0 || maxLines == Int.MAX_VALUE) {
                return
            }
            var text = view.text
            val method = view.transformationMethod
            if (method != null) {
                text = method.getTransformation(text, view)
            }
            val context = view.context
            var r = Resources.getSystem()
            val displayMetrics: DisplayMetrics
            var size = maxTextSize
            val high = size
            val low = 0f
            if (context != null) {
                r = context.resources
            }
            displayMetrics = r.displayMetrics
            paint.set(view.paint)
            paint.textSize = size
            if (maxLines == 1 && paint.measureText(text, 0, text.length) > targetWidth
                || getLineCount(
                    text,
                    paint,
                    size,
                    targetWidth.toFloat(),
                    displayMetrics
                ) > maxLines
            ) {
                size = getAutofitTextSize(
                    text,
                    paint,
                    targetWidth.toFloat(),
                    maxLines,
                    low,
                    high,
                    displayMetrics
                )
            }
            setSizeToView(view, size, minTextSize)
        }

        private fun setSizeToView(
            view: TextView,
            size: Float,
            mTextSize: Float
        ) {
            var mSize = size
            if (mSize < mTextSize) {
                mSize = mTextSize
            }
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSize)
        }

        /**
         * Recursive binary search to find the best size for the text.
         */
        private fun getAutofitTextSize(
            text: CharSequence?, paint: TextPaint?,
            targetWidth: Float, maxLines: Int, low: Float, high: Float,
            displayMetrics: DisplayMetrics?
        ): Float {
            val mid = (low + high) / 2.0f
            var lineCount = 1
            var layout: StaticLayout? = null
            paint!!.textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX, mid,
                displayMetrics
            )
            if (maxLines != 1) {
                layout = StaticLayout(
                    text, paint, targetWidth.toInt(), Layout.Alignment.ALIGN_NORMAL,
                    1.0f, 0.0f, true
                )
                lineCount = layout.lineCount
            }
            return if (lineCount > maxLines) {
                // For the case that `text` has more newline characters than `maxLines`.
                if (high - low < precision) {
                    low
                } else getAutofitTextSize(
                    text, paint, targetWidth, maxLines, low, mid,
                    displayMetrics
                )
            } else if (lineCount < maxLines) {
                getAutofitTextSize(
                    text, paint, targetWidth, maxLines, mid, high,
                    displayMetrics
                )
            } else {
                val data = mutableMapOf<String, Any?>()
                data["maxLines"] = maxLines
                data["paint"] = paint
                data["text"] = text
                data["layout"] = layout
                data["lineCount"] = lineCount
                data["low"] = low
                data["high"] = high
                data["displayMetrics"] = displayMetrics
                data["targetWidth"] = targetWidth
                size(data)
            }
        }

        private fun size(data: Map<*, *>): Float {
            var maxLineWidth = 0f
            val maxlines = data["maxLines"] as Int
            val paint = data["paint"] as TextPaint?
            val text = data["text"] as CharSequence?
            val layout = data["layout"] as StaticLayout?
            val lineCount = data["lineCount"] as Int
            val low = data["low"] as Float
            val high = data["high"] as Float
            val displayMetrics = data["displayMetrics"] as DisplayMetrics?
            val targetWidth = data["targetWidth"] as Float
            maxLineWidth = getMaxWidth(
                maxlines,
                maxLineWidth,
                paint,
                text,
                layout,
                lineCount
            )
            val mid = (low + high) / 2.0f
            return if (high - low < precision) {
                low
            } else if (maxLineWidth > targetWidth) {
                getAutofitTextSize(
                    text, paint, targetWidth, maxlines, low, mid,
                    displayMetrics
                )
            } else if (maxLineWidth < targetWidth) {
                getAutofitTextSize(
                    text, paint, targetWidth, maxlines, mid, high,
                    displayMetrics
                )
            } else {
                mid
            }
        }

        private fun getMaxWidth(
            maxLines: Int,
            maxLineWidth: Float,
            paint: TextPaint?,
            text: CharSequence?,
            layout: StaticLayout?,
            lineCount: Int
        ): Float {
            var width = maxLineWidth
            width = if (maxLines == 1) {
                paint!!.measureText(text, 0, text!!.length)
            } else {
                calMaxWidth(lineCount, width, layout)
            }
            return width
        }

        /**
         * To calculate maxLinewidth when max lines greater than one.
         */
        private fun calMaxWidth(
            lineCount: Int,
            maxLineWidth: Float,
            layout: StaticLayout?
        ): Float {
            var calWidth = 0f
            for (i in 0 until lineCount) {
                if (layout!!.getLineWidth(i) > maxLineWidth) {
                    calWidth = layout.getLineWidth(i)
                }
            }
            return calWidth
        }

        private fun getLineCount(
            text: CharSequence, paint: TextPaint, size: Float, width: Float,
            displayMetrics: DisplayMetrics
        ): Int {
            paint.textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX, size,
                displayMetrics
            )
            val layout = StaticLayout(
                text, paint, width.toInt(),
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true
            )
            return layout.lineCount
        }

        private fun getMaxLines(view: TextView): Int {
            var maxLines = -1 // No limit (Integer.MAX_VALUE also means no limit)
            val method = view.transformationMethod
            if (method != null && method is SingleLineTransformationMethod) {
                maxLines = 1
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                // setMaxLines() and getMaxLines() are only available on android-16+
                maxLines = view.maxLines
            }
            return maxLines
        }
    }

    init {
        val context = view.context
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        mTextView = view
        mPaint = TextPaint()
        setRawTextSize(view.textSize)
        maxLines = getMaxLines(view)
        minTextSize = scaledDensity * DEFAULT_MIN_TEXT_SIZE
        maxTextSize = mTextSize
        precision = DEFAULT_PRECISION
    }
}