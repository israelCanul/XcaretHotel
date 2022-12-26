package com.xcaret.xcaret_hotel.view.base

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.statusbar.StatusBarCompat
import com.xcaret.xcaret_hotel.view.general.ui.LoadingDialogFragment
import com.xcaret.xcaret_hotel.view.general.ui.MainActivity
import kotlin.math.abs

abstract class BaseFragmentDataBinding<V: ViewModel, T: ViewDataBinding>: Fragment(){
    abstract fun getLayout(): Int
    abstract fun getViewModelClass() : Class<V>
    abstract fun bindViewToModel()
    abstract fun setupUI()

    lateinit var binding: T
    lateinit var viewModel : V

    private val loadingDialog: LoadingDialogFragment by lazy {
        LoadingDialogFragment()
    }

    private var heightDefaultToolbar = 0f

    val _parentActivity: MainActivity? by lazy{
        requireActivity() as MainActivity
    }

    val settingsManager: SettingsManager by lazy { SettingsManager.getInstance(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        viewModel = ViewModelProvider(this).get(getViewModelClass())
        setupUI()
        bindViewToModel()
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        return binding.root
    }

    fun onBackPressed(listener:() -> Unit) {
        requireActivity().onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                listener()
            }
        })
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    fun showSystemUI() {
        try {
            val isNight = Settings.isActiveDarkTheme(HotelXcaretApp.mContext)

            activity?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            context?.let {
                activity?.window?.navigationBarColor =
                    ContextCompat.getColor(it, R.color.colorBackgroundNavigation)
            }
            activity?.let {
                StatusBarCompat.setStatusBarColor(it, Color.TRANSPARENT)
                if (isNight) StatusBarCompat.cancelLightStatusBar(it) else StatusBarCompat.changeToLightStatusBar(
                    it
                )
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun hideSystemUI(hideStatusbar:Boolean, hideNavigationBar:Boolean) {
        try {

            if(hideStatusbar && !hideNavigationBar)
                activity?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
            else if (hideNavigationBar && !hideStatusbar)
                activity?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

            else if (hideStatusbar && hideNavigationBar)
                activity?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)


        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun activateAnimBottomNav(nestedScrollView: NestedScrollView){
        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY > oldScrollY) {
                _parentActivity?.toggleBottomNav()
            }
            if (scrollY < oldScrollY) {
                _parentActivity?.toggleBottomNav()
            }
        }
    }

    fun navigate(destionation: Int, args: Bundle = Bundle.EMPTY){
        _parentActivity?.navigate(destionation, args)
    }

    fun popBackStack() = _parentActivity?.popBackStack()

    fun animToolbar(recyclerView: RecyclerView? = null, nestedScrollView: NestedScrollView? = null){
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            recyclerView?.setOnScrollChangeListener(View.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                animToolbar(scrollY, oldScrollY)
            })

            nestedScrollView?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                animToolbar(scrollY, oldScrollY)
            })
        }
    }

    fun animToolbar(scrollY:Int, oldScrollY: Int){
        _parentActivity?.findViewById<ConstraintLayout>(R.id.toolbarView)?.let { toolbar ->
            if(heightDefaultToolbar == 0f) heightDefaultToolbar = toolbar.height.toFloat()
            if(scrollY > oldScrollY){
                val toolbarY = toolbar.y
                val diff = scrollY - oldScrollY
                if(toolbarY > -heightDefaultToolbar) {
                    var newY = (abs(toolbarY) + diff)
                    val alpha = 1-((1/heightDefaultToolbar) * newY)
                    newY = if(-newY > -heightDefaultToolbar) newY else heightDefaultToolbar
                    toolbar.alpha = alpha
                    toolbar.y = -newY
                }
            }
            if(scrollY < oldScrollY){
                val toolbarY = toolbar.y
                if(toolbarY < 0) {
                    var newY = toolbarY + (oldScrollY - scrollY)
                    newY = if(newY > 0) 0f
                    else newY
                    val alpha = (1-(1/heightDefaultToolbar) * abs(newY))
                    LogHX.e("locationY", "$toolbarY, oldY: $newY")
                    if(toolbarY != newY){
                        toolbar.alpha = alpha
                        toolbar.y = newY
                    }

                }
            }
        }
    }

    fun configToolbar(key: String){
        val toolbarSettings = ToolbarUtils.toolbars[key]
        toolbarSettings?.let { ts ->
            _parentActivity?.updateVisibiltyToolbar(show = ts.showToolbar, bgResource = ts.bgResource,
                title = ts.title, showBackButton = ts.showBackBtn,
                colorBackBtn = ts.backBtnColor, colorText = ts.textColor
            )
        }
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

}