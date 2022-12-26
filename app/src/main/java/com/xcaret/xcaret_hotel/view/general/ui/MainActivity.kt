package com.xcaret.xcaret_hotel.view.general.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsLogger
import com.facebook.internal.Logger
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.xcaret.xcaret_hotel.BuildConfig
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.Hotel
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.glide_module.GlideApp
import com.xcaret.xcaret_hotel.view.config.glide_module.MyGlideModule
import com.xcaret.xcaret_hotel.view.config.navbottombar.OnBubbleClickListener
import com.xcaret.xcaret_hotel.view.config.navigation.NavHostFragment
import com.xcaret.xcaret_hotel.view.general.vm.MainViewModel
import com.xcaret.xcaret_hotel.view.menu.ui.CallUsDialogFragment
import com.xcaret.xcaret_hotel.view.quote.ui.DetailRoomDialog
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_view.*
import kotlinx.android.synthetic.main.toolbar_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.util.*

class MainActivity : AppCompatActivity() {

    val _viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    var currentHotel: Hotel? = null

    var currentPlaceHotel: Place? = null

    val animSlideDown: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.slide_down)
    }

    val animSlideUp: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.slide_up)
    }

    val firebaseStorage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance(BuildConfig.DBStorage)
    }

    val firebaseAnalytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }

    val firebaseInApp: FirebaseInAppMessaging by lazy {
        FirebaseInAppMessaging.getInstance()
    }

    val facebookAnalytics: AppEventsLogger by lazy {
        AppEventsLogger.newLogger(HotelXcaretApp.mContext)
    }


    //private var myFragmentNavigator: MyFragmentNavigator? = null
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment).navController
    }

    private val detailRoomDialog: DetailRoomDialog by lazy { DetailRoomDialog.newInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
    }

    override fun onResume() {
        Log.d("click btnBack", "onResume")
        if(!btnBack.hasOnClickListeners()) {
            Log.d("click btnBack", "nolisteners")
            btnBack.setOnClickListener {
                Log.d("click btnBack", "click")
                onBackPressed()
            }
        }
        super.onResume()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.scheme != null && "myapp" == intent.scheme) {
            val extras = intent.extras
            Amplify.Auth.handleWebUISignInResponse(intent)
        }
    }

    private fun initComponents() {
        setListeners()
        Utils.fixHeightTopView(toolbarView)
        updateVisibiltyToolbar(false)
        setupBottomNavigation()
    }

    @SuppressLint("RestrictedApi")
    private fun setListeners() {
        animSlideUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(p0: Animation?) = Unit
            override fun onAnimationRepeat(p0: Animation?) = Unit

            override fun onAnimationStart(p0: Animation?) {
                layoutBottomNav.visibility = View.VISIBLE
            }
        })

        animSlideDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) = Unit
            override fun onAnimationStart(p0: Animation?) = Unit

            override fun onAnimationEnd(p0: Animation?) {
                layoutBottomNav.visibility = View.GONE
            }
        })

        //val navHostFragment: NavHostFragment = mainNavHost as NavHostFragment
        //navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            LogHX.i("destination", destination.label.toString())
            val toolbarSettings = ToolbarUtils.toolbars[destination.label]
            LogHX.i("toolbarSettings", toolbarSettings?.toString())
            if (destination.label == FragmentTags.Map.value) {
                _viewModel.currentDestination.value?.let { currentDestionation ->
                    if (currentDestionation.label !in arrayOf(
                            FragmentTags.Home.value,
                            FragmentTags.AFI.value,
                            FragmentTags.Menu.value
                        )
                    )
                        toolbarSettings?.showBottomNav = false
                }
            }
            _viewModel.currentDestination.value = destination
            toolbarSettings?.let { ts ->
                updateVisibiltyToolbar(
                    show = ts.showToolbar, bgResource = ts.bgResource,
                    title = ts.title, showBackButton = ts.showBackBtn,
                    colorBackBtn = ts.backBtnColor, colorText = ts.textColor
                )
                updateVisibilityBottomNav(ts.showBottomNav)
            }
            LogHX.i("iterator back stack", "=====================================")
            showBackStack()
            LogHX.i("iterator back stack", "=====================================")
            selectBubbleBottom(destination.id)
        }

        navController.removeOnDestinationChangedListener { _, destination, _ ->
            LogHX.i("remove destination", "=====================================")
            showBackStack()
            LogHX.i("remove destination", "=====================================")
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showBackStack() {
        navController.backStack.forEach {
            LogHX.i("destination", "${it.destination.id} - ${it.destination.label}")
        }
    }

    fun selectBubbleBottom(destination: Int) {
        try {
            Log.d(
                "ids",
                "des: $destination, h: ${R.id.homeFragment}, m: ${R.id.mapFragment}, a: ${R.id.AFIFragment}, p: ${R.id.menuFragment}"
            )
            when (destination) {
                R.id.homeFragment -> bottomNavigation.setSelected(0, false)
                R.id.mapFragment -> bottomNavigation.setSelected(1, false)
                R.id.AFIFragment -> bottomNavigation.setSelected(2, false)
                R.id.menuFragment -> bottomNavigation.setSelected(3, false)
            }
        } catch (ex: java.lang.Exception) {
            LogHX.d("MainActivity", ex.toString())
        }
    }

    fun updateVisibilityBottomNav(show: Boolean) {
        val visible = if (show) View.VISIBLE else View.GONE
        layoutBottomNav.visibility = visible
    }

    fun updateVisibiltyToolbar(
        show: Boolean, bgResource: Int = R.color.colorPlecaTransparent,
        title: String? = "", showBackButton: Boolean = true,
        colorBackBtn: Int = R.color.colorBtnBack, colorText: Int = R.color.colorToolbar
    ) {
        val visible = if (show) View.VISIBLE else View.GONE
        if (show) toolbarView.setBackgroundColor(ContextCompat.getColor(this, bgResource))
        if (toolbarView.y < 0f) {
            toolbarView.y = 0f
            toolbarView.alpha = 1f
        }
        toolbarView.visibility = visible
        val showBack = if (showBackButton) View.VISIBLE else View.INVISIBLE
        toolbarView.btnBack.visibility = showBack
        toolbarView.btnBack.setColorFilter(
            ContextCompat.getColor(this, colorBackBtn),
            PorterDuff.Mode.SRC_IN
        )
        if(show) {
            toolbarView.txtTitle.setTextColor(ContextCompat.getColor(this, colorText))
            addObserverForTitle(title ?: "")
        }
    }

    fun setTitle(title: String?) {
        txtTitle.text = title
    }

    fun addObserverForTitle(key: String) {
        try {
            _viewModel.getLabelByKey().removeObservers(this)
            _viewModel.keyLabelLiveData.value = key
            _viewModel.getLabelByKey().observe(this, Observer { langLabel ->
                if (toolbarView.isVisible) {
                    if (txtTitle is TextView) {
                        txtTitle.text = langLabel?.value
                    }
                }
            })
        }catch (exc:Exception){

        }
    }

    private fun setupBottomNavigation() {
        //NavigationUIExtension.setupWithBubbleTabController(bottomNavigation, _viewModel.navController.value!!)
        bottomNavigation.addBubbleListener(object : OnBubbleClickListener {
            override fun onBubbleClick(id: Int) {
                LogHX.d("onBubbleClick", "${navController.currentDestination?.id} == $id")
                val builder = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setEnterAnim(R.anim.nav_default_enter_anim)
                    .setExitAnim(R.anim.nav_default_exit_anim)
                    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.anim.nav_default_pop_exit_anim)

                builder.setPopUpTo(R.id.homeFragment, id == R.id.homeFragment)

                val options = builder.build()
                try {
                    navigate(id, null, options)
                } catch (e: IllegalArgumentException) {
                }
            }
        })
    }

    fun setObservables() {
        cleanObservables()
        _viewModel.currentHotelLive.observe(this, Observer {
            currentHotel = it
        })

        _viewModel.findLabelByFloor().observe(this, Observer { label ->
            label?.let { _viewModel.labelFloor.value = it.value }
                ?: kotlin.run { _viewModel.labelFloor.value = getString(R.string.lbl_floor) }
        })

        _viewModel.findCategoryHotel().observe(this, Observer { category ->
            _viewModel.categoryHotelLive.value = category
        })

        _viewModel.findCategoryBulding().observe(this, Observer { category ->
            _viewModel.allByFilterGroup().removeObservers(this)
            val listUIDS = mutableListOf<String>()
            category.forEach { listUIDS.add(it.uid) }
            _viewModel.categoryListBuildingUID.value?.clear()
            _viewModel.categoryListBuildingUID.value?.addAll(listUIDS)
            _viewModel.categoryBuildingLive.value = category
        })

        _viewModel.categoryBuildingLive.observe(this, Observer { category ->
            category?.let { setObserverAllBuilding(it) }
        })

        _viewModel.categoryListIds.observe(this, Observer {
            setObserverBuildingPlace()
        })
    }

    private fun cleanObservables() {
        _viewModel.findCategoryHotel().removeObservers(this)
        _viewModel.findLabelByFloor().removeObservers(this)
        _viewModel.findCategoryBulding().removeObservers(this)
        _viewModel.categoryBuildingLive.removeObservers(this)
        _viewModel.categoryListIds.removeObservers(this)
    }

    private fun setObserverAllBuilding(filterCategory: List<Category>) {
        _viewModel.allByFilterGroup().observe(this, Observer { categoryList ->
            _viewModel.findPlaceInCategory().removeObservers(this)
            val listCategory = mutableListOf<String>()
            filterCategory.forEach { listCategory.add(it.uid) }
            categoryList.forEach { cat ->
                if (!listCategory.contains(cat.uid) && cat.uid.isNotEmpty()) listCategory.add(cat.uid)
            }
            _viewModel.categoryListIds.value = listCategory.toList()
        })
    }

    private fun setObserverBuildingPlace() {
        _viewModel.findPlaceInCategory().observe(this, Observer {
            _viewModel.completeInfoByHouses(it) { listPlace ->
                _viewModel.housesLiveData.value = listPlace
            }
        })
    }

    fun navigate(destination: Int, args: Bundle? = Bundle.EMPTY, navOptions: NavOptions? = null) {
        try {
            navController.navigate(destination, args, navOptions)
        } catch (e: Exception) {
            LogHX.e("Error navigate", destination.toString())
        }
    }

    fun popBackStack() = navController.popBackStack()

    fun toggleBottomNav() {
        val isVisible = layoutBottomNav.visibility == View.VISIBLE
        val anim = if (isVisible) animSlideDown else animSlideUp
        layoutBottomNav.startAnimation(anim)
    }

    fun loadRemoteImage(
        path: String,
        image: String,
        imageView: ImageView,
        onlyThumb: Boolean = false,
        default: String = ""
    ) {
        val path_ = path.replace("//","/")
        LogHX.d("loadRemoteImage", "$path_/$image")
        val referenceOrigin = firebaseStorage.getReference(path_.toLowerCase()).child("${image}.jpg")
        val referenceThumb =
            firebaseStorage.getReference(path_.toLowerCase()).child("thumbs/${image}.jpg")
        val hotelCode = path_.split('/')
//        val placeHolder =
//            if(default.isEmpty()) Utils.getDefaultImage(this, "${_viewModel.currentHotelLive.value?.code?.toLowerCase()}")
        val placeHolder =
            if (default.isEmpty()) Utils.getDefaultImage(this, hotelCode[0].toLowerCase())
            else Utils.getDrawableId(this, default)

//        Glide.with(this)
//            .load(placeHolder)
//            .into(imageView)

            GlideApp.with(imageView.context)
                .load(referenceThumb)
                .placeholder(placeHolder!!)
                .error(placeHolder)
//                .listener(object :RequestListener<Drawable?>{
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Drawable?>?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        LogHX.e("LoadRemote","onLoadFailed Tumb")
//                        return false
//                    }
//
//                    override fun onResourceReady(
//                        resource: Drawable?,
//                        model: Any?,
//                        target: Target<Drawable?>?,
//                        dataSource: DataSource?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        LogHX.e("LoadRemote Listener","onResourceReady Tumb")
//                        return false
//                    }
//                })
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        LogHX.e("LoadRemote","onResourceReady Tumb")
                        imageView.setImageDrawable(resource)
                        if (!onlyThumb) {
                            LogHX.e("LoadRemote","HD Image Ready")
                            GlideApp.with(imageView.context)
                                   .load(referenceOrigin)
                                   .placeholder(resource)
                                   .error(placeHolder)
                                   .into(imageView)
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        imageView.setImageDrawable(placeholder)
                        LogHX.e("LoadRemote","onLoadCleared HD")
                    }

                })

//        referenceThumb.downloadUrl.addOnSuccessListener { thumb ->
//            LogHX.e("LoadRemote","OnSuccessListener Ready${thumb.path}")
//            Glide.with(applicationContext)
//                .load(thumb)
//                .placeholder(placeHolder!!)
//                .error(placeHolder)
//                .into(object : CustomTarget<Drawable>() {
//                    override fun onResourceReady(
//                        resource: Drawable,
//                        transition: Transition<in Drawable>?
//                    ) {
//                        LogHX.e("LoadRemote","onResourceReady Tumb")
//                        imageView.setImageDrawable(resource)
//                        if (!onlyThumb) {
//                            LogHX.e("LoadRemote","HD Image Ready")
//                            referenceOrigin.downloadUrl.addOnSuccessListener { image ->
//                                Glide.with(applicationContext)
//                                    .load(image)
//                                    .placeholder(resource)
//                                    .error(placeHolder)
//                                    .into(imageView)
//                            }
//                        }
//                    }
//
//                    override fun onLoadCleared(placeholder: Drawable?) {
//                        imageView.setImageDrawable(placeholder)
//                        LogHX.e("LoadRemote","onLoadCleared HD")
//                    }
//
//                })
//        }
    }

    fun showInMap(placeUID: String) {
        val args = bundleOf(Constants.PLACE_UID to placeUID)
        navController.navigate(R.id.mapFragment, args)
    }

    fun showContactDialog(type: Int, value: String) {
        val callUsDialog = CallUsDialogFragment.newInstance(type, value)
        callUsDialog.show(supportFragmentManager, CallUsDialogFragment.TAG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_UPDATE) {
            lifecycleScope.launch {
                _viewModel.setResultAppUpdate(resultCode)
            }
        }
        if (requestCode == AWSCognitoAuthPlugin.WEB_UI_SIGN_IN_ACTIVITY_CODE) {
            Amplify.Auth.handleWebUISignInResponse(data)
        } else if (requestCode == Constants.SCAN_REQUEST_CODE) {
            data?.let { mData ->
                if (mData.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                    data.getParcelableExtra<CreditCard>(CardIOActivity.EXTRA_SCAN_RESULT)
                        ?.let { scanResult ->
                            resultFromScanCard(scanResult)
                        }
                }
            }
        }
    }

    fun showScanCreditCard() {
        val scanIntent = Intent(this, CardIOActivity::class.java)
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false)
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true)
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true)
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, true)
        startActivityForResult(scanIntent, Constants.SCAN_REQUEST_CODE)
    }

    private fun resultFromScanCard(scanResult: CreditCard) {
        LogHX.d("scanResult", scanResult.toString())
        if (scanResult.isExpiryValid) {
            val expiryDate = "${scanResult.expiryMonth}/${scanResult.expiryYear}"
            LogHX.d("ExpiryDate", expiryDate)
            _viewModel.expiryDate.value = expiryDate
        }
        if (!scanResult.cardholderName.isNullOrEmpty())
            _viewModel.holderName.value = scanResult.cardholderName
        if (!scanResult.formattedCardNumber.isNullOrEmpty())
            _viewModel.cardNumber.value = scanResult.formattedCardNumber
    }

    //region AllertDialogs

    fun showSuccessAlert(message: String, acceptListener: DialogAlert.onClickListener) {
        showAlert(
            icon = R.drawable.ic_succes,
            message = message,
            accept = getString(R.string.rkey_lbl_ok),
            acceptListener = acceptListener
        )
    }

    fun showErrorAlert(
        message: String,
        accept: String? = null,
        acceptListener: DialogAlert.onClickListener,
        cancelListener: DialogAlert.onClickListener
    ) {
        showAlert(
            icon = R.drawable.ic_error,
            message = message,
            cancel = getString(R.string.rkey_btn_cancel),
            accept = accept ?: getString(R.string.rkey_lbl_retry),
            acceptListener = acceptListener,
            cancelListener = cancelListener
        )
    }

    fun showInfoAlert(message: String, acceptListener: DialogAlert.onClickListener) {
        showAlert(
            icon = R.drawable.ic_info,
            message = message,
            accept = getString(R.string.rkey_lbl_ok),
            acceptListener = acceptListener
        )
    }

    fun showAlert(
        icon: Int? = null,
        title: String? = null,
        extraTitle: String? = null,
        message: String,
        extra: String? = " ",
        accept: String? = null,
        cancel: String? = null,
        acceptListener: DialogAlert.onClickListener,
        cancelListener: DialogAlert.onClickListener? = null
    ) {

        try {

            val dialogAlert = DialogAlert.newInstance()
                .setIcon(icon)
                .setExtraTitle(extraTitle)
                .setDinamicTitle(title)
                .setDinamicMessage(message, extra)
                .setDinamicTextButtonConfirm(accept)
                .setDinamicTextButtonCancel(cancel)
                .setOnAcceptListener(acceptListener)
                .setOnCancelListener(cancelListener)
            dialogAlert.showSecure(supportFragmentManager, DialogAlert.TAG)
            //dialogAlert.showSecure()
        } catch (e: Exception) {
            Log.e(DialogAlert.TAG, e.toString())
        }
    }

    fun showSnack(message: String,snackType:Int) {
        try {
            val snackAlert = DialogSnack.newInstance()
                .setTypeSnack(snackType)
                .setDinamicMessage(message)
            snackAlert.showSecure(supportFragmentManager, DialogSnack.TAG)
        } catch (e: Exception) {
            Log.e(DialogSnack.TAG, e.toString())
        }
    }

    fun showRoomTypeDialog() {
        detailRoomDialog.showSecure(supportFragmentManager, DetailRoomDialog.TAG)
    }

    //endregion

    //region Firebase
    fun deepLink() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                if (deepLink != null) {
                    var url = deepLink.path
                    val urlshort = intent.data.toString()
                    val codeNavigation = urlshort.substringAfterLast(
                        delimiter = "/",
                        missingDelimiterValue = "Extension not found"
                    ) ?: "APP"
                    if (codeNavigation.contains('X', false)) {
                        //navigationParkPromotion(codeNavigation)
                    } else {
                        //goPromotion("hm", codeNavigation)
                    }
                } else {
                    Log.d("MainActivity", "getDynamicLink: no link found")
                }

            }
            .addOnFailureListener(this) { e ->
                Log.w("MainActivity", "getDynamicLink:onFailure", e)
            }
    }

    companion object {
        var HAS_REQUEST_PERMISSION_HOME = true
        var HAS_REQUEST_PERMISSION_MAP = true
    }
}
