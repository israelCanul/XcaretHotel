package com.xcaret.xcaret_hotel.view.config

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.config.navbottombar.BubbleTabBar
import com.xcaret.xcaret_hotel.view.config.navbottombar.OnBubbleClickListener
import java.lang.ref.WeakReference

object NavigationUIExtension {

    fun setupWithBubbleTabController(
        bubbleTabBar: BubbleTabBar,
        navController: NavController
    ) {
        bubbleTabBar.addBubbleListener(object : OnBubbleClickListener {
            override fun onBubbleClick(id: Int) {
                onNavDestionationSelected(id, navController)
            }
        })

        val weakReference: WeakReference<BubbleTabBar> = WeakReference(bubbleTabBar)
        navController.addOnDestinationChangedListener(object :
            NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                val view = weakReference.get()
                if (view == null) {
                    navController.removeOnDestinationChangedListener(this)
                    return
                }
                val bubbles = view.bubbles
                bubbles.forEachIndexed { index, bubble ->
                    if (matchDestination(destination, bubble.id))
                        view.setSelected(index, false)
                }
            }
        })
    }

    fun matchDestination(destination: NavDestination, destId: Int): Boolean {
        var currentDestination = destination
        while (currentDestination.id != destId && currentDestination.parent != null)
            currentDestination = currentDestination.parent!!
        return currentDestination.id == destId
    }

    fun onNavDestionationSelected(id: Int, navController: NavController): Boolean {
        val builder = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.nav_default_fade_out)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)

        val options = builder.build()
        return try {
            navController.navigate(id, null, options)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

}

