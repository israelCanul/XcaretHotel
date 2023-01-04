package com.xcaret.xcaret_hotel.photopass.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import com.xcaret.xcaret_hotel.view.general.ui.MainActivity

abstract class BaseFragment : Fragment(){
    private lateinit var _parentView: View
    val _parentActivity: MainActivity? by lazy{
        activity?.let {
            it as MainActivity
        } ?: kotlin.run { null }
    }
    abstract fun getLayout(): Int
    abstract fun onViewFragmentCreated(view: View, savedInstanceState: Bundle?)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _parentView = inflater.inflate(getLayout(), container, false)
        return _parentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showSystemUI()
        onViewFragmentCreated(view, savedInstanceState)
    }

    private fun showSystemUI() {
        activity?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    }
    fun fullScreen() {
        activity?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
    fun onBackPressed(listener:() -> Unit) {
        requireActivity().onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                listener()
            }
        })
    }
    fun popBackStack() = _parentActivity?.popBackStack()
    fun onBackPressed() {
        activity?.onBackPressed()
    }

    fun navigate(des: Int, args: Bundle = Bundle.EMPTY, options: NavOptions? = null){
        _parentActivity?.navigate(des, args, options)
        //findNavController().navigate(des, args)
    }


    fun navigate(des: Int){
        //findNavController().navigate(des)
        _parentActivity?.navigate(des)
    }
}