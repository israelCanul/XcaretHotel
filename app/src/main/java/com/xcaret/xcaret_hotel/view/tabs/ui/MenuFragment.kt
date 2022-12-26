package com.xcaret.xcaret_hotel.view.tabs.ui

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.MenuFragmentBinding
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticConstant
import com.xcaret.xcaret_hotel.view.config.analytics.logEvent
import com.xcaret.xcaret_hotel.view.general.ui.DialogAlert
import com.xcaret.xcaret_hotel.view.general.ui.LoadingDialogFragment
import com.xcaret.xcaret_hotel.view.menu.ui.*
import com.xcaret.xcaret_hotel.view.security.ui.DialogEnterCode
import com.xcaret.xcaret_hotel.view.tabs.vm.MenuViewModel
import kotlinx.android.synthetic.main.menu_fragment.*
import kotlinx.android.synthetic.main.pickup_fragment.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.support.v4.runOnUiThread
import java.lang.IllegalStateException

class MenuFragment: BaseFragmentDataBinding<MenuViewModel, MenuFragmentBinding>(){
    override fun getLayout(): Int = R.layout.menu_fragment
    override fun getViewModelClass(): Class<MenuViewModel> = MenuViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val termsDialog: LegalDialogFragment by lazy {
        LegalDialogFragment.newInstance(Constants.TERMS_CONDITON)
    }

    private val privacyDialog: LegalDialogFragment by lazy {
        LegalDialogFragment.newInstance(Constants.NOTICE_PRIVACY)
    }

    private val langDialog: LanguageDialogFragment by lazy {
        LanguageDialogFragment.newInstance()
    }

    private val faqsDialog: FaqsDialogFragment by lazy {
        FaqsDialogFragment.newInstance()
    }

    override fun setupUI() {
        showSystemUI()
        _parentActivity?.setObservables()
        setObservables()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        showSystemUI()
    }

    private fun setObservables(){
        _parentActivity?.packageManager?.let { packageManager ->
            viewModel.xcaretAppIsInstalled.value = Utils.isPackageInstalled(Constants.PACKAGE_PARKS_APP, packageManager)
        }

        viewModel.findContactWriteUse().observe(this, Observer {
            viewModel.contactLiveData.value = it
        })

        settingsManager.getUID.asLiveData().observe(this, Observer {
            viewModel.getSession().removeObservers(this)
            viewModel.uidLiveData.value = it
            setObserverSession()
        })

        _parentActivity?._viewModel?.currentHotelLive?.observe(this, Observer {
            viewModel.hotelLive.value = it
        })
    }

    private fun setObserverSession(){
        viewModel.getSession().observe(this, Observer {
            viewModel.userLiveData.value = it
        })
    }

    private fun initComponents(){
        Utils.fixHeightTopView(statusBarFix)
        val isNightTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        switchTheme.isChecked = isNightTheme == Configuration.UI_MODE_NIGHT_YES
        setListeners()
        logEvent(
            AnalyticConstant.ID_NAV_MENU,
            AnalyticConstant.ITEM_NAME_NAV_MAP,
            AnalyticConstant.CONTENT_TYPE_NAVTABBAR)
    }

    private fun setListeners(){
        onBackPressed {
            if(viewModel.changedTheme.value == true){
                val builder = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setEnterAnim(R.anim.nav_default_enter_anim)
                    .setExitAnim(R.anim.nav_default_exit_anim)
                    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.anim.nav_default_pop_exit_anim)

                builder.setPopUpTo(R.id.homeFragment, true)
                viewModel.changedTheme.value = false
                val options = builder.build()
                try {
                    _parentActivity?.navigate(R.id.homeFragment, null, options)
                } catch (e: IllegalArgumentException) { }
            }else _parentActivity?.popBackStack()
        }

        optMyReservations.onClick {
            _parentActivity?.navigate(R.id.action_menuFragment_to_myReservationFragment)
        }
        optLang.setOnClickListener {
            logEvent(
                AnalyticConstant.ID_MENU_LANGUAGE,
                AnalyticConstant.ITEM_NAME_MENU_LANGUAGE,
                AnalyticConstant.CONTENT_TYPE_MENU)
            langDialog.show(childFragmentManager, LanguageDialogFragment.TAG)
        }

        optCallUs.setOnClickListener {
            logEvent(
                AnalyticConstant.ID_MENU_CALLNOW,
                AnalyticConstant.ITEM_NAME_MENU_CALLNOW,
                AnalyticConstant.CONTENT_TYPE_MENU)
            _parentActivity?.showContactDialog(CallUsDialogFragment.CODE, Constants.CATEGORY_CALLUS_CODE)
        }

        optWriteUs.setOnClickListener {
            logEvent(
                AnalyticConstant.ID_MENU_EMAIL,
                AnalyticConstant.ITEM_NAME_MENU_EMAIL,
                AnalyticConstant.CONTENT_TYPE_MENU)
            writeUs()
        }

        optRate.setOnClickListener {
            logEvent(
                AnalyticConstant.ID_MENU_REVIEW,
                AnalyticConstant.ITEM_NAME_MENU_REVIEW,
                AnalyticConstant.CONTENT_TYPE_MENU)

            openPlayStore(context?.packageName)
        }

        optAbout.setOnClickListener {
            val hotelCode = _parentActivity?.currentHotel?.code
            logEvent(
                AnalyticConstant.ID_MENU_ABOUT.replace("xx",hotelCode!!),
                AnalyticConstant.ITEM_NAME_MENU_ABOUT.replace("xx",hotelCode),
                AnalyticConstant.CONTENT_TYPE_MENU)
            navigate(R.id.action_menuFragment_to_aboutHotelFragment)
        }

        optParks.setOnClickListener {
            logEvent(
                AnalyticConstant.ID_MENU_APP_XCARET,
                AnalyticConstant.ITEM_NAME_MENU_APP_XCARET,
                AnalyticConstant.CONTENT_TYPE_MENU)
            openExternalApp(Constants.PACKAGE_PARKS_APP)
        }

        optTerms.setOnClickListener {
            termsDialog.show(childFragmentManager,LegalDialogFragment.TAG_TERMS)
            logEvent(
                AnalyticConstant.ID_MENU_TERMS,
                AnalyticConstant.ITEM_NAME_MENU_TERMS,
                AnalyticConstant.CONTENT_TYPE_MENU)

        }

        optPrivacy.setOnClickListener {
            logEvent(
                AnalyticConstant.ID_MENU_NOTICE_PRIVACY,
                AnalyticConstant.ITEM_NAME_MENU_NOTICE_PRIVACY,
                AnalyticConstant.CONTENT_TYPE_MENU)
            privacyDialog.show(childFragmentManager, LegalDialogFragment.TAG_PRIVACY)
        }
        optFaqs.setOnClickListener{
            //faqsDialog.show(childFragmentManager,FaqsDialogFragment.TAG)
            navigate(R.id.action_menuFragment_to_FAQFragment)

        }

        switchTheme.setOnCheckedChangeListener { _, b ->
            logEvent(
                AnalyticConstant.ID_MENU_THEME,
                AnalyticConstant.ITEM_NAME_MENU_THEME,
                AnalyticConstant.CONTENT_TYPE_MENU)

            //removeHomeAndMapFragment()
            val isNightTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            val isNight = isNightTheme == Configuration.UI_MODE_NIGHT_YES

            LogHX.i("activeDarkTheme", "$isNight")
            Settings.activeDarkTheme(!isNight, requireContext())
            viewModel.changedTheme.value = true
            when (isNightTheme) {
                Configuration.UI_MODE_NIGHT_YES ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Configuration.UI_MODE_NIGHT_NO ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        optMyProfile.onClick {
            navigate(R.id.action_menuFragment_to_profileFragment)
        }

        optAuthentication.onClick {
            val args = bundleOf(Constants.IS_VISTOR to true)
            navigate(R.id.action_menuFragment_to_loginFragment, args)
        }

        optSignOut.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        MarketingCloud.setManualContactKey(Session.getDeviceUID(requireContext()))
        _parentActivity?.let {
            _parentActivity?.showAlert(
                title = getString(R.string.rkey_sign_out_title),
                message = getString(R.string.rkey_sign_out_message),
                accept = getString(R.string.rkey_lbl_continue),
                cancel = getString(R.string.rkey_btn_cancel),
                cancelListener = object : DialogAlert.onClickListener {
                    override fun onClick(v: View?, dialog: DialogFragment) {
                        dialog.dismiss()
                    }
                },
                acceptListener = object : DialogAlert.onClickListener {
                    override fun onClick(v: View?, dialog: DialogFragment) {
                        dialog.dismiss()
                        viewModel.signOut {
                            //if (it) {
                                val args = bundleOf(
                                    Constants.REDIRECT_DESTINATION_ID to R.id.action_loginFragment_to_splashFragment,
                                    Constants.CLEAR_STACK to true
                                )

                                logEvent(
                                    AnalyticConstant.ID_MENU_LOGOUT,
                                    AnalyticConstant.ITEM_NAME_MENU_LOGOUT,
                                    AnalyticConstant.CONTENT_TYPE_MENU
                                )


                                findNavController().navigate(
                                    R.id.action_menuFragment_to_loginFragment_signOut,
                                    args
                                )
                            //}
                        }
                    }
                }
            )
        }
    }

    private fun writeUs(){
        context?.let {ctx ->
            viewModel.contactLiveData.value?.let { contact ->
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                val subject = ctx.resources.getString(R.string.lbl_subject)
                val mailto = "mailto:${contact.value}?subject=${Uri.encode(subject)}"
                emailIntent.data = Uri.parse(mailto)
                try {
                    startActivity(Intent.createChooser(emailIntent, subject))
                } catch (e: Exception) {}
            }
        }
    }

    private fun openExternalApp(packageName: String){
        val launchIntent = _parentActivity?.packageManager?.getLaunchIntentForPackage(packageName)
        launchIntent?.let {
            startActivity(launchIntent)
        } ?: kotlin.run {
            openPlayStore(packageName)
        }
    }

    private fun openPlayStore(packageName: String?){
        try {
            val marketUri = Uri.parse("${Constants.URL_MARKET}$packageName")
            val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
            startActivity(marketIntent)
        } catch (e: java.lang.Exception) {
            val marketUri = Uri.parse("${Constants.URL_PLAY_STORE}$packageName")
            val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
            startActivity(marketIntent)
        }
    }

}