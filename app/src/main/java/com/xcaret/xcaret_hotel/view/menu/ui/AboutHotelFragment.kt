package com.xcaret.xcaret_hotel.view.menu.ui

import android.view.View
import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.AboutHotelFragmentBinding
import com.xcaret.xcaret_hotel.domain.Award
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.GenericAdapter
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticConstant
import com.xcaret.xcaret_hotel.view.config.analytics.logEvent
import com.xcaret.xcaret_hotel.view.menu.vm.AboutHotelViewModel
import java.lang.Exception

class AboutHotelFragment: BaseFragmentDataBinding<AboutHotelViewModel, AboutHotelFragmentBinding>(){
    override fun getLayout(): Int = R.layout.about_hotel_fragment
    override fun getViewModelClass(): Class<AboutHotelViewModel> = AboutHotelViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val awardAdapter = GenericAdapter<Award>(R.layout.item_award, 0.25f, 0f)

    override fun setupUI() {
        initComponents()
        setObservers()
    }

    private fun initComponents(){
        animToolbar(nestedScrollView = binding.nestedScroll)
        binding.rvAwards.adapter = awardAdapter
        Utils.heighPercentage(binding.headerLayout, 0.5f)
        Utils.heighPercentage(binding.rvAwards, 0.095f)
        setListeners()
    }

    private fun setObservers(){
        viewModel.hotelLiveData.value = _parentActivity?._viewModel?.currentHotelLive?.value
        val hotelCode = viewModel.hotelLiveData.value?.code ?: "0"

        logEvent(AnalyticConstant.ID_HOME_ABOUT.replace("_xx",hotelCode),
            AnalyticConstant.ITEM_NAME_HOME_ABOUT.replace("_xx",hotelCode),
            AnalyticConstant.CONTENT_TYPE_HOME
        )

        viewModel.findCategoryByCode().observe(this, Observer {
            viewModel.categoryHotelLiveData.value = it
            setPlaceObserver()
        })
    }

    private fun setPlaceObserver(){
        viewModel.findPlace().removeObservers(this)
        viewModel.findPlace().observe(this, Observer {
            viewModel.placeHotelLiveData.value = it
            setAwardObsevers()
        })
    }

    private fun setAwardObsevers(){
        viewModel.findAwardByPlaceUID().removeObservers(this)
        viewModel.findAwardByPlaceUID().observe(this, Observer {
            if (it.isNotEmpty())
                awardAdapter.addItems(it)
            else{
                binding.rvAwards.visibility = View.GONE
                binding.txtTitleAwards.visibility = View.GONE
            }

        })
    }

    private fun setListeners(){
        binding.ivMap.setOnClickListener {
            Utils.goToNavigation(viewModel.placeHotelLiveData.value?.latitude ?: 0.0, viewModel.placeHotelLiveData.value?.longitude ?: 0.0, context)
        }

        binding.btnReserve.setOnClickListener {
            //_parentActivity?.showContactDialog(CallUsDialogFragment.CODE, Constants.CATEGORY_CALLUS_CODE)
            try {
                navigate(R.id.about_fragment_to_quote_fagment)
            }catch (exc:Exception){

            }
        }
    }
}