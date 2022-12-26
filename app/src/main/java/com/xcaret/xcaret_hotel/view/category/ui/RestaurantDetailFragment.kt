package com.xcaret.xcaret_hotel.view.category.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.RestaurantDetailFragmentBinding
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.category.vm.RestaurantDetailViewModel
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.FragmentTags
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.menu.ui.CallUsDialogFragment
import kotlinx.android.synthetic.main.restaurant_detail_fragment.*

class RestaurantDetailFragment: BaseFragmentDataBinding<RestaurantDetailViewModel, RestaurantDetailFragmentBinding>() {
    override fun getLayout(): Int = R.layout.restaurant_detail_fragment
    override fun getViewModelClass(): Class<RestaurantDetailViewModel> = RestaurantDetailViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    override fun setupUI() {
        viewModel.restUID.value = arguments?.getString(Constants.REST_UID) ?: ""
        viewModel.callFromMap.value = arguments?.getBoolean(Constants.CALLING_FROM_MAP) ?: false
        setObserverRest()
    }

    private fun initComponents(){
        Utils.heighPercentage(headerLayout, 0.47f)
        setListeners()
    }

    private fun setObserverRest(){
        viewModel.findById().observe(this, Observer {
            viewModel.restLiveData.value = it
            setObserverCategory()
        })
    }

    private fun setObserverCategory(){
        cleanObservers()
        viewModel.findCategoryById().observe(this, Observer {
            viewModel.categoryLiveData.value = it
        })

        viewModel.findLocation().observe(this, Observer {
            viewModel.locationLiveData.value = it
        })

        viewModel.findRestDetail().observe(this, Observer {
            viewModel.restDetailLiveData.value = it
        })
    }

    private fun cleanObservers(){
        viewModel.findLocation().removeObservers(this)
        viewModel.findCategoryById().removeObservers(this)
        viewModel.findRestDetail().removeObservers(this)
    }

    private fun setListeners(){
        btnReserve.setOnClickListener {
            viewModel.restLiveData.value?.let {
                _parentActivity?.showContactDialog(CallUsDialogFragment.UID, it.contactCategoryUID ?: "")
            }
        }
    }
}