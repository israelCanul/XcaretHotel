package com.xcaret.xcaret_hotel.view.general.ui

import android.animation.Animator
import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.xcaret.xcaret_hotel.BuildConfig
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.SplashFragmentBinding
import com.xcaret.xcaret_hotel.domain.ProviderType
import com.xcaret.xcaret_hotel.domain.User
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.general.vm.SplashViewModel
import kotlinx.android.synthetic.main.splash_fragment.*
import org.jetbrains.anko.support.v4.runOnUiThread
import java.util.*
import kotlin.concurrent.schedule

class SplashFragment: BaseFragmentDataBinding<SplashViewModel, SplashFragmentBinding>() {

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(requireContext()) }

    override fun getLayout(): Int = R.layout.splash_fragment
    override fun getViewModelClass(): Class<SplashViewModel> = SplashViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    override fun setupUI() {
        if(!BuildConfig.DEBUG){
            init()
        }else {
            viewModel.resultAppUpdate.asLiveData().observe(this, Observer { resultCode ->
                if (resultCode != -100) {
                    LogHX.e("resultAppUpdate", (resultCode == RESULT_OK).toString())
                    init()
                }
            })
            checkUpdate()
        }
    }

    private fun init(){
        setObservers()
        viewModel.isSessionActive {
            runOnUiThread {
                LogHX.e("session active", it.toString())
                viewModel.isSessionActive.value = it || Session.isVisitor(HotelXcaretApp.mContext)
                if(viewModel.isSessionActive.value == true){
                    viewModel.getUser { user ->
                        if(user?.provider != ProviderType.Visitor){
                            if (user?.email.isNullOrEmpty()){
                                viewModel.createPaxProfileByExternalId(user){ }
                            }else {
                                viewModel.createPaxProfile(user = user!!){ }
                            }
                        }
                        var contactKey = Session.getDeviceUID(requireContext())
                        user?.email?.let { email ->
                            if(!Session.isVisitor(HotelXcaretApp.mContext)) contactKey = email
                        }
                        MarketingCloud.init(requireContext()){
                            if(it) MarketingCloud.setManualContactKey(contactKey)
                            viewModel.getPaxProfileExternalId(user?.cognitoId!!) { user ->
                                val us = user.CONTACT?.ELEMENT?.get(0)
                                if (us!= null) {
                                    MarketingCloud.registerDataToMKTCloud(
                                        User(
                                            cognitoId = us.EXTERNAL_ID__C,
                                            firstName = us.FIRSTNAME,
                                            lastName = us.LASTNAME,
                                            name = us.NAME,
                                            email = us.EMAIL,
                                            phone = us.PHONE,
                                            cp = us.MAILINGPOSTALCODE,
                                            platform = "Android",
                                            title_code = us.TITLE,
                                            city = us.MAILINGCITY,
                                            state_code = us.MAILINGSTATECODEISO__C,
                                            country_code = us.MAILINGCOUNTRYCODEISO__C,
                                            address = us.MAILINGSTREET
                                        )
                                    )
                                }

                            }
                        }
                    }
                }
               // viewModel.load(viewModel.isSessionActive.value == false)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents(){
        hideSystemUI(hideStatusbar = true, hideNavigationBar = true)
        setListeners()
    }

    private fun setObservers(){
        viewModel.splashAnimEnd.observe(this, Observer {
            goMain()
        })

        viewModel.resultDownload.observe(this, Observer {
            goMain()
        })
    }

    private fun setListeners(){
        animation_view.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                viewModel.splashAnimEnd.postValue(true)
                binding.loadingProgress.visibility = View.VISIBLE
                viewModel.load(viewModel.isSessionActive.value == false)
            }

            override fun onAnimationRepeat(animation: Animator?) {}
        })
    }

    private fun goMain(){
        LogHX.e(
            "splash",
            "end animation: ${viewModel.splashAnimEnd.value}, end load firebase: ${viewModel.resultDownload.value?.success}"
        )
        viewModel.resultDownload.value?.let { resultDownload ->
            if(viewModel.splashAnimEnd.value == true && resultDownload.success)
                waitToGoHome()
        }
    }

    private fun waitToGoHome(){
        try {
            _parentActivity?.setObservables()
            Timer("GoHome", false).schedule(500) {
                runOnUiThread {
                    if (viewModel.isSessionActive.value == true) {
                        navigate(R.id.action_splashFragment_to_navigation)
                    } else {
                        val args = bundleOf(
                            Constants.REDIRECT_DESTINATION_ID to R.id.action_loginFragment_to_splashFragment,
                            Constants.CLEAR_STACK to true
                        )
                        navigate(R.id.action_splashFragment_to_loginFragment, args)
                    }
                }
            }
        }catch (exc:Exception){

        }
    }

    //region update manager
    private fun checkUpdate(){
        try {
            // Returns an intent object that you use to check for an update.
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo

            // Checks that the platform will allow the specified type of update.
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    // Request the update.
                    requestUpdate(appUpdateInfo)
                } else {
                    init()
                }
            }.addOnFailureListener {
                LogHX.e("appUpdateError", it.toString())
                init()
            }
        }catch (e: Exception){
            e.printStackTrace()
            init()
        }
    }

    private fun requestUpdate(appUpdateInfo: AppUpdateInfo){
        appUpdateManager.startUpdateFlowForResult(
            // Pass the intent that is returned by 'getAppUpdateInfo()'.
            appUpdateInfo,
            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
            AppUpdateType.IMMEDIATE,
            // The current activity making the update request.
            requireActivity(),
            // Include a request code to later monitor this update request.
            Constants.REQUEST_UPDATE)
    }
    //endregion
}