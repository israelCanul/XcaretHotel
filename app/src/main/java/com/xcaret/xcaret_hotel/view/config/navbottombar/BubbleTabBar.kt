package com.xcaret.xcaret_hotel.view.config.navbottombar

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.viewpager.widget.ViewPager
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.config.LogHX
import com.xcaret.xcaret_hotel.view.config.navbottombar.parser.MenuParser

class BubbleTabBar : LinearLayout {
    private var onBubbleClickListener: OnBubbleClickListener? = null
    private var disabled_icon_colorParam: Int = Color.GRAY
    private var horizontal_paddingParam: Float = 0F
    private var icon_paddingParam: Float = 0F
    private var vertical_paddingParam: Float = 0F
    private var icon_sizeParam: Float = 0F
    private var title_sizeParam: Float = 0F
    private var custom_fontParam: Int = 0

    val bubbles = mutableListOf<Bubble>()

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    fun addBubbleListener(onBubbleClickListener: OnBubbleClickListener) {
        this.onBubbleClickListener = onBubbleClickListener
    }

    fun setupBubbleTabBar(viewPager: ViewPager) {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                setSelected(position)
            }
        })
    }

    fun setSelected(position: Int, callListener: Boolean = true) {
        var it = (this@BubbleTabBar.getChildAt(position) as Bubble)
        if(it.id == oldBubble?.id) return


        oldBubble?.isSelected = false
        it.isSelected = true

        /*var b = it.id
        if (oldBubble != null && oldBubble!!.id != b) {
            it.isSelected = !it.isSelected
            oldBubble!!.isSelected = false
        }*/
        oldBubble = it
        if (onBubbleClickListener != null && callListener) {
            onBubbleClickListener!!.onBubbleClick(it.id)
        }
    }

    private fun toggleSelected(position: Int){
        (this@BubbleTabBar.children).forEach {  }
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        if (attrs != null) {
            val attributes =
                context.theme.obtainStyledAttributes(attrs, R.styleable.BubbleTabBar, 0, 0)
            try {
                val menuResource =
                    attributes.getResourceId(R.styleable.BubbleTabBar_bubbletab_menuResource, -1)
                disabled_icon_colorParam = attributes.getColor(
                    R.styleable.BubbleTabBar_bubbletab_disabled_icon_color,
                    Color.GRAY
                )
                custom_fontParam =
                    attributes.getResourceId(R.styleable.BubbleTabBar_bubbletab_custom_font, 0)

                icon_paddingParam = attributes.getDimension(
                    R.styleable.BubbleTabBar_bubbletab_icon_padding,
                    resources.getDimension(R.dimen.bubble_icon_padding)
                )
                horizontal_paddingParam = attributes.getDimension(
                    R.styleable.BubbleTabBar_bubbletab_horizontal_padding,
                    resources.getDimension(R.dimen.bubble_horizontal_padding)
                )
                vertical_paddingParam = attributes.getDimension(
                    R.styleable.BubbleTabBar_bubbletab_vertical_padding,
                    resources.getDimension(R.dimen.bubble_vertical_padding)
                )
                icon_sizeParam = attributes.getDimension(
                    R.styleable.BubbleTabBar_bubbletab_icon_size,
                    resources.getDimension(R.dimen.bubble_icon_size)
                )
                title_sizeParam = attributes.getDimension(
                    R.styleable.BubbleTabBar_bubbletab_title_size,
                    resources.getDimension(R.dimen.bubble_icon_size)
                )
                if (menuResource >= 0) {
                    setMenuResource(menuResource)
                }
            } finally {
                attributes.recycle()
            }


        }
    }


    private var oldBubble: Bubble? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setMenuResource(menuResource: Int) {
        val menu = (MenuParser(context).parse(menuResource))
        removeAllViews()
        Log.e("menu ", "-->" + menu.size)
        menu.forEach { it ->
            if (it.id == 0) {
                throw ExceptionInInitializerError("Id is not added in menu item")
            }
            it.apply {
                it.horizontal_padding = horizontal_paddingParam
                it.vertical_padding = vertical_paddingParam
                it.icon_size = icon_sizeParam
                it.icon_padding = icon_paddingParam
                it.custom_font = custom_fontParam
                it.disabled_icon_color = disabled_icon_colorParam
                it.title_size = title_sizeParam
            }
            val bubble = Bubble(context, it).apply {
                if (it.checked) {
                    this.isSelected = true
                    oldBubble = this
                }
                setOnClickListener {
                    var b = it.id
                    if(it.id != oldBubble?.id) {
                        /*if (oldBubble != null && oldBubble!!.id != b) {
                            (it as Bubble).isSelected = !it.isSelected
                            oldBubble!!.isSelected = false
                        }*/
                        oldBubble?.isSelected = false
                        (it as Bubble).isSelected = true
                        oldBubble = it

                        onBubbleClickListener?.onBubbleClick(it.id)
                    }
                }
            }
            addView(bubble)
            bubbles.add(bubble)

        }
        invalidate()
    }
}
