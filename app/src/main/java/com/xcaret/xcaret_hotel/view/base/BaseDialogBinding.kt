package com.xcaret.xcaret_hotel.view.base

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.config.Settings
import com.xcaret.xcaret_hotel.view.general.ui.MainActivity

abstract class BaseDialogBinding<V: ViewModel, T: ViewDataBinding>: DialogFragment() {
    abstract fun getLayout(): Int
    abstract fun getViewModelClass(): Class<V>
    abstract fun bindViewToModel()
    abstract fun setupUI()

    lateinit var binding: T
    lateinit var viewModel: V

    val _parentActivity: MainActivity? by lazy{
        requireActivity() as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(getViewModelClass())
        setupUI()
        bindViewToModel()
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        return view
    }

    fun fullScreen() {
        dialog?.window?.let { window ->
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
                window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
            else {
                window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
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

    fun hideSystemUI() {
        try {
            dialog?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                    //or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
    fun hideSystem() {
        try {
            dialog?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            //or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun setTransparentStatusBar(){
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog?.window?.statusBarColor = ContextCompat.
                getColor(HotelXcaretApp.mContext, android.R.color.transparent)
    }
    fun setTransparentNavigationBar(){
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog?.window?.navigationBarColor = ContextCompat.
        getColor(HotelXcaretApp.mContext, R.color.colorBackgroundNavigation_Aux)
    }

    fun setColorStatusBar(@ColorRes colorRes:Int){
        val color = ContextCompat.getColor(HotelXcaretApp.mContext, colorRes)
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity?.window?.statusBarColor = color

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