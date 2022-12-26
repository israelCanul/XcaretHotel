package com.xcaret.xcaret_hotel.view.base

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.DialogFragment
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.config.Settings
import com.xcaret.xcaret_hotel.view.config.statusbar.StatusBarCompat
import com.xcaret.xcaret_hotel.view.general.ui.MainActivity


abstract class BaseDialog: DialogFragment(){

    val _parentActivity: MainActivity? by lazy{
        requireActivity() as MainActivity
    }

    abstract fun getLayout(): Int
    abstract fun initComponents()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayout(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    fun fullScreen() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog?.window?.let { window ->
                window.apply {
                    setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return
                }
                if (activity == null) {
                    return
                }

                val isNightTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                val isNight = isNightTheme == Configuration.UI_MODE_NIGHT_YES
                //if(isNight) window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                if(!isNight) window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    fun hideSystemUI() {
        try {
            activity?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            dialog?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun hideStatusBar(){
        try {
            activity?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }catch (exc:Exception){

        }
    }

    fun showSystemUI() {
        try {
            val isNight = Settings.isActiveDarkTheme(HotelXcaretApp.mContext)

            activity?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            context?.let {
                activity?.window?.navigationBarColor =
                    ContextCompat.getColor(it, R.color.colorBackgroundNavigation)
            }
            activity?.let {
//                StatusBarCompat.setStatusBarColor(it, Color.TRANSPARENT)
//                if (isNight) StatusBarCompat.cancelLightStatusBar(it) else StatusBarCompat.changeToLightStatusBar(
//                    it
//                )
                it.window?.statusBarColor =
                    ContextCompat.getColor(it, R.color.colorBackgroundNavigation)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    fun setTransparentStatusBar(){
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog?.window?.statusBarColor = ContextCompat.
        getColor(HotelXcaretApp.mContext, android.R.color.transparent)
    }
    fun setColorStatusBar(){
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (isNightEnabled()){
            dialog?.window?.statusBarColor = ContextCompat.
            getColor(HotelXcaretApp.mContext, R.color.color47)
        }else{
            dialog?.window?.statusBarColor = ContextCompat.
            getColor(HotelXcaretApp.mContext, R.color.colorBackgroundNavigation_Aux)
        }
    }

    fun customColorStatusBar(idColor:Int){
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog?.window?.statusBarColor = ContextCompat.
        getColor(HotelXcaretApp.mContext, idColor)

    }

    fun setColorNavBar(){
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (isNightEnabled()){
            dialog?.window?.navigationBarColor = ContextCompat.
            getColor(HotelXcaretApp.mContext, R.color.color47)
        }else{
            dialog?.window?.navigationBarColor = ContextCompat.
            getColor(HotelXcaretApp.mContext, R.color.colorBackgroundNavigation_Aux)
        }


    }

    fun isNightEnabled():Boolean{
        return Settings.isActiveDarkTheme(HotelXcaretApp.mContext)
        //val isNightTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        //return isNightTheme == Configuration.UI_MODE_NIGHT_YES
    }

    fun setTransitionStyle(resource: Int){
        dialog?.window?.attributes?.windowAnimations = resource
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    override fun onDestroyView() {
        dialog?.setDismissMessage(null)
        super.onDestroyView()
    }


}