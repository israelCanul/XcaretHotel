package com.xcaret.xcaret_hotel.view.config.statusbar

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

/**
 * Utils for status bar
 * Created by qiu on 3/29/16.
 */

object StatusBarCompat {
    //Get alpha color
    fun calculateStatusBarColor(color: Int, alpha: Int): Int {
        val a = 1 - alpha / 255f
        var red = color shr 16 and 0xff
        var green = color shr 8 and 0xff
        var blue = color and 0xff
        red = (red * a + 0.5).toInt()
        green = (green * a + 0.5).toInt()
        blue = (blue * a + 0.5).toInt()
        return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
    }

    /**
     * set statusBarColor
     * @param statusColor color
     * @param alpha       0 - 255
     */
    fun setStatusBarColor(
        activity: Activity,
        @ColorInt statusColor: Int,
        alpha: Int
    ) {
        setStatusBarColor(activity, calculateStatusBarColor(statusColor, alpha))
    }

    fun setStatusBarColor(
        activity: Activity,
        @ColorInt statusColor: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop.setStatusBarColor(activity, statusColor)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompatKitKat.setStatusBarColor(activity, statusColor)
        }
    }

    fun translucentStatusBar(activity: Activity) {
        translucentStatusBar(activity, false)
    }

    /**
     * change to full screen mode
     * @param hideStatusBarBackground hide status bar alpha Background when SDK > 21, true if hide it
     */
    fun translucentStatusBar(
        activity: Activity,
        hideStatusBarBackground: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop.translucentStatusBar(activity, hideStatusBarBackground)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompatKitKat.translucentStatusBar(activity)
        }
    }

    fun setStatusBarColorForCollapsingToolbar(
        activity: Activity,
        appBarLayout: AppBarLayout?,
        collapsingToolbarLayout: CollapsingToolbarLayout?,
        toolbar: Toolbar?,
        @ColorInt statusColor: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop.setStatusBarColorForCollapsingToolbar(
                activity,
                appBarLayout!!,
                collapsingToolbarLayout!!,
                toolbar!!,
                statusColor
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompatKitKat.setStatusBarColorForCollapsingToolbar(
                activity,
                appBarLayout!!,
                collapsingToolbarLayout!!,
                toolbar!!,
                statusColor
            )
        }
    }

    fun changeToLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            activity.window.statusBarColor = Color.WHITE
            return
        }
        val window = activity.window ?: return
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            window.insetsController?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        else {
            val oldFlags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = oldFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun cancelLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            activity.window.statusBarColor = Color.BLACK
            return
        }
        val window = activity.window ?: return
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            window.insetsController?.setSystemBarsAppearance(0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        else {
            val oldFlags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = oldFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}