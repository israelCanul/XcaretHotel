package com.xcaret.xcaret_hotel.view.config.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.xcaret.xcaret_hotel.view.config.Utils
import kotlin.math.max
import kotlin.math.min

class CustomToolbarViewBehavior (context: Context, attrs: AttributeSet): CoordinatorLayout.Behavior<ConstraintLayout>(context, attrs){

    private val statusBarHeight: Int = Utils.getStatusBarHeigth(context)

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: ConstraintLayout, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: ConstraintLayout, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        child.translationY = max(statusBarHeight - child.height.toFloat(), min(-(statusBarHeight.toFloat() + child.height.toFloat()), child.translationY - dy))
    }
}