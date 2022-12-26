package com.xcaret.xcaret_hotel.view.tabs.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.HomeFragmentBinding
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.Hotel
import com.xcaret.xcaret_hotel.domain.LangLabel
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticConstant
import com.xcaret.xcaret_hotel.view.config.analytics.logEvent
import com.xcaret.xcaret_hotel.view.general.ui.DialogAlert
import com.xcaret.xcaret_hotel.view.general.ui.MainActivity
import com.xcaret.xcaret_hotel.view.tabs.vm.HomeViewModel
import kotlinx.android.synthetic.main.about_hotel_fragment.*
import kotlinx.android.synthetic.main.home_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception


class HomeFragment : BaseFragmentDataBinding<HomeViewModel, HomeFragmentBinding>() {
    override fun getLayout(): Int = R.layout.home_fragment
    override fun getViewModelClass(): Class<HomeViewModel> = HomeViewModel::class.java
    override fun bindViewToModel() {
        binding.viewModel = viewModel
    }

    private val categoryAdapter = GenericAdapter<Category>(R.layout.item_home_category)
    private val hotelAdapter = GenericAdapter<Hotel>(R.layout.item_hotel, 0.92f)

    override fun setupUI() {
        showSystemUI()
        initComponents()
        FirebaseAnalytics.getInstance(requireContext()).logEvent("event_home",null)
        FirebaseInAppMessaging.getInstance().triggerEvent("event_home")
        _parentActivity?._viewModel?.categoryHotelLive?.value?.let { _ ->
            setObserverHotel()
        } ?: kotlin.run {
            viewModel.findCategoryHotel().observe(this, Observer { _ ->
                setObserverHotel()
            })
        }
        viewModel.getUserLive().observe(this,
            {
                if (it != null) {
                    viewModel.countryCode.value = it.country_code
                }
            }
        )
        viewModel.countryCode.observe(this,{
            doAsync {
                viewModel.findCountry(viewModel.countryCode.value!!)
            }

        })

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden)showSystemUI()
        //Log.e("HomeFragment","Hidden $hidden")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Session.isShowWelcomeAlert(requireContext()))
            showWelcomeAlert()
        else showCompleteAlert()
    }


    private fun initComponents() {
        val screenSize = Utils.getScreenSize(requireContext())
        Utils.fixHeightTopView(binding.auxStatusBar)
        Utils.fixMarginTopView(binding.btnWeather)
        val sizeCat = (screenSize.heightPixels * 0.22)
        categoryAdapter.widthPixel = (sizeCat * 0.8).toInt()
        categoryAdapter.heightPixel = sizeCat.toInt()
        binding.rvCategories.adapter = categoryAdapter
        binding.dsvHotels.adapter = hotelAdapter
        setListener()
        requestPermission()

    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && MainActivity.HAS_REQUEST_PERMISSION_HOME
        ) {
            MainActivity.HAS_REQUEST_PERMISSION_HOME = false
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    fun setDefaultvalues() {
        hotelAdapter.let {
            val hotel = hotelAdapter.getItem(0)
            if (hotel != null) {
                Settings.setHotelSelected(hotel.idSynxis?.toIntOrNull() ?: 0, requireContext())
                Settings.setHotelUIDSelected(hotel.uid ?: "", requireContext())
            }
        }
    }

    private fun setListener() {
        binding.btnWeather.onClick {
            try {
                logEvent(AnalyticConstant.ID_WEATHERHOME,
                AnalyticConstant.ITEM_NAME_WEATHER_HOME,
                AnalyticConstant.CONTENT_TYPE_HOME)

                navigate(R.id.action_homeFragment_to_weatherFragment)
            } catch (e: Exception) {
            }
        }

        binding.dsvHotels.addOnItemChangedListener { _, adapterPosition ->
            val hotel = hotelAdapter.getItem(adapterPosition)
            if (hotel?.uid != _parentActivity?._viewModel?.currentHotelLive?.value?.uid) {
                Settings.setHotelSelected(hotel?.idSynxis?.toIntOrNull() ?: 0, requireContext())
                viewModel.hotelSynxisSelected.value = Settings.getHotelSelected(requireContext())
                Settings.setHotelUIDSelected(hotel?.uid ?: "", requireContext())
                _parentActivity?._viewModel?.currentHotelLive?.value = hotel
                setObserverQuote()
            }
        }

        hotelAdapter.setOnListItemViewClickListener(object :
            GenericAdapter.OnListItemViewClickListener {
            override fun onClick(view: View, position: Int) {
                val m = hotelAdapter.getItem(position)
                navigate(R.id.action_homeFragment_to_aboutHotelFragment)
            }
        })

        categoryAdapter.setOnListItemViewClickListener(object :
            GenericAdapter.OnListItemViewClickListener {
            override fun onClick(view: View, position: Int) {
                try {
                    val catCode = categoryAdapter.getItem(position)?.code ?: ""
                    val hotel = _parentActivity?._viewModel?.currentHotelLive?.value
                    when (catCode) {
                        Constants.CATEGORY_ROOM_CODE -> {
                            val args = bundleOf(Constants.HOTEL_UID to hotel?.uid)
                            navigate(R.id.action_homeFragment_to_roomListFragment, args)
                        }
                        Constants.CATEGORY_REST_CODE -> {
                            val args = bundleOf(Constants.HOTEL_UID to hotel?.uid)
                            navigate(R.id.action_homeFragment_to_restaurantListFragment, args)
                        }
                        Constants.CATEGORY_BUILDING_CODE -> {
                            navigate(R.id.action_homeFragment_to_buildingDetailFragment)
                        }
                        Constants.CATEGORY_WORKSHOP -> {
                            val args =
                                bundleOf(Constants.CAT_UID to categoryAdapter.getItem(position)?.uid)
                            navigate(R.id.action_homeFragment_to_workShopListFragment, args)
                        }
                        Constants.CATEGORY_EVENTS -> {
                            val args =
                                bundleOf(Constants.CAT_UID to categoryAdapter.getItem(position)?.uid)
                            navigate(R.id.action_homeFragment_to_eventDetailFragment, args)
                        }
                    }
                } catch (e: Exception) {
                }
            }
        })

        binding.btnQuotes.onClick {
            navigate(R.id.action_homeFragment_to_quoteFragment)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setObserverHotel() {
        viewModel.hotelCategoryLiveData.value =
            _parentActivity?._viewModel?.categoryHotelLive?.value
        viewModel.hotelSynxisSelected.value = Settings.getHotelSelected(requireContext())
        _parentActivity?._viewModel?.currentHotelLive?.observe(this, Observer { currentHotel ->
            currentHotel?.let { hotel ->
                viewModel.allByHotel().removeObservers(this)
                viewModel.hotelLiveData.value = hotel
                viewModel.findDateByHotelId(hotel.idSynxis?.toInt())
                setObserverCategory()
            }
        })

        viewModel.all().observe(this, Observer { listHotels ->
            /*var position = 0
            if(listHotels.isNotEmpty()){
                _parentActivity?._viewModel?.currentHotelLive?.value?.let { hotel ->
                    if(viewModel.hotelSynxisSelected.value != hotel.idSynxis?.toIntOrNull() ?: 0) {
                        listHotels.find { it.idSynxis == viewModel.hotelSynxisSelected.value?.toString() }?.let { h ->
                            position = listHotels.indexOf(h)
                            if(position < 0) position = 0
                            _parentActivity?._viewModel?.currentHotelLive?.value = listHotels[position]
                        }
                    }
                } ?: kotlin.run {
                    viewModel.hotelSynxisSelected.value?.let { synxisCode ->
                        listHotels.find { it.idSynxis == synxisCode.toString() }?.let { h ->
                            position = listHotels.indexOf(h)
                            if(position < 0) position = 0
                            _parentActivity?._viewModel?.currentHotelLive?.value = listHotels[position]
                        }
                    } ?: kotlin.run { _parentActivity?._viewModel?.currentHotelLive?.value = listHotels[0] }
                }
            }*/
            if (listHotels.isNotEmpty()) {
                val listHotelsOrder = listHotels.sortedBy { it.order }
                var position = 0
                listHotelsOrder.forEachIndexed { index, hotel ->
                    if (hotel.idSynxis?.equals(viewModel.hotelSynxisSelected) == true)
                        position = index
                }
                _parentActivity?._viewModel?.currentHotelLive?.value = listHotelsOrder[position]
                viewModel.hotelsLive.value = listHotelsOrder
                hotelAdapter.addItems(listHotelsOrder)
                binding.dsvHotels.scrollToPosition(position)
                setObserverPlace()
                setDefaultvalues()
            }
        })

        viewModel.getWeatherByDay().observe(this, Observer {
            it?.let { weather ->
                txtWeather.text =
                    weather.formatTemp(weather.feels_like?.feels_like_day) + 0x00B0.toChar()
            }
        })

        setObserverQuote()
    }

    @SuppressLint("SetTextI18n")
    fun setObserverQuote() {
        viewModel.findDateByHotelId(viewModel.hotelSynxisSelected.value?.toInt())
            .removeObservers(this)
        viewModel.findDateByHotelId(viewModel.hotelSynxisSelected.value?.toInt())
            .observe(this, { date ->
                if (date != null) {
                    val formDate1 = DateUtil.changeFormatDate(
                        date.dateArrival,
                        DateUtil.DATE_FORMAT_WEATHER,
                        DateUtil.QUOTES_FORMAT
                    )
                    val formDate2 = DateUtil.changeFormatDate(
                        date.dateDeparture,
                        DateUtil.DATE_FORMAT_WEATHER,
                        DateUtil.QUOTES_FORMAT
                    )

                    txtNights.makeVisible()
                    textStayWithUs.text = "$formDate1 - $formDate2 / ${date.calculateNights()}"
                } else {
                    txtNights.makeGone()
                    textStayWithUs.setKey(getString(R.string.rkey_lbl_quotes))
                }

            })

    }

    fun findLabel(key: String, result: (res: LangLabel?) -> Unit) {
        doAsync {
            val label = viewModel.getLabelByCode(key)
            uiThread {
                result(label)
            }
        }
    }

    private fun setObserverPlace() {
        viewModel.findPlaceByCategory().removeObservers(this)
        viewModel.findPlaceByCategory().observe(this, Observer { listPlace ->
            viewModel.hotelsLive.value?.forEach { hotel ->
                hotel.hotelPlace =
                    listPlace.firstOrNull { it.code.equals(hotel.code, ignoreCase = true) }
            }
            hotelAdapter.notifyDataSetChanged()
        })
    }

    private fun setObserverCategory() {
        viewModel.allByHotel().observe(this, Observer {
            categoryAdapter.addItems(it)
        })
    }

    private fun showCompleteAlert() {
        doAsync {
            if (Session.getRememberDate(requireContext()).isEmpty()) return@doAsync
            val currentDate = DateUtil.getCalendar()
            val rememberDate = DateUtil.convertStringToDate(
                Session.getRememberDate(requireContext()),
                DateUtil.DATE_FORMAT_WEATHER
            )
            val user = viewModel.getUser()
            val compare = currentDate.compareTo(rememberDate)
            LogHX.e("compareTo", compare.toString())
            if (user?.isProfileComplete() == true) {
                Session.setRememberDate("", requireContext())
                return@doAsync
            }
            if (compare < 0) return@doAsync

            Session.setRememberDate("", requireContext())
            _parentActivity?.showAlert(
                icon = R.drawable.ic_information,
                message = getString(R.string.rkey_incomplete_profile_msg),
                accept = getString(R.string.rkey_btn_accept),
                cancel = getString(R.string.rkey_btn_cancel),
                cancelListener = object : DialogAlert.onClickListener {
                    override fun onClick(v: View?, dialog: DialogFragment) {
                        dialog.dismiss()
                    }
                },
                acceptListener = object : DialogAlert.onClickListener {
                    override fun onClick(v: View?, dialog: DialogFragment) {
                        redirectQuoteFragment()
                        dialog.dismiss()
                    }
                }
            )
        }
    }

    private fun redirectQuoteFragment() {
        Session.setRememberDate("", requireContext())
        navigate(R.id.action_homeFragment_to_quoteFragment)
    }

    private fun showWelcomeAlert() {
        var name = ""

        doAsync {
            viewModel.getUser()?.let { user ->
                name = "${user.firstName} ${user.lastName}"

            }
            try {
                Session.setShowWelcomeAlert(false, requireContext())
                val acct = activity as MainActivity
                val fragMngr = acct.supportFragmentManager
                if (acct != null && fragMngr != null) {
                    showAlert(
                        title = getString(R.string.rkey_welcome),
                        extraTitle = name,
                        message = getString(R.string.rkey_welcome_msg),
                        accept = getString(R.string.rkey_btn_accept),
                        fragmentManger = fragMngr,
                        acceptListener = object : DialogAlert.onClickListener {
                            override fun onClick(v: View?, dialog: DialogFragment) {
                                dialog.dismiss()
                                showCompleteAlert()
                            }
                        }
                    )
                }
            } catch (exc: IllegalStateException) {
                Log.e("ShowWelcomeAlert",exc.toString())
            }
            catch (exc: Exception){
                Log.e("ShowWelcomeAlert",exc.toString())
            }

        }
    }

    fun showAlert(
        icon: Int? = null,
        title: String? = null,
        extraTitle: String? = null,
        message: String,
        accept: String? = null,
        cancel: String? = null,
        fragmentManger: FragmentManager,
        acceptListener: DialogAlert.onClickListener,
        cancelListener: DialogAlert.onClickListener? = null,
    ) {

        try {
            val dialogAlert = DialogAlert.newInstance()
                .setIcon(icon)
                .setExtraTitle(extraTitle)
                .setDinamicTitle(title)
                .setDinamicMessage(message)
                .setDinamicTextButtonConfirm(accept)
                .setDinamicTextButtonCancel(cancel)
                .setOnAcceptListener(acceptListener)
                .setOnCancelListener(cancelListener)
                //dialogAlert.setDinamicTitle(title)
            dialogAlert.show(fragmentManger, DialogAlert.TAG)
        } catch(exc:IllegalStateException){

        }
        catch (e: Exception) {
            Log.e(DialogAlert.TAG, e.toString())
        }
    }


}