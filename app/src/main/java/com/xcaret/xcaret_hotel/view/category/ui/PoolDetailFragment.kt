package com.xcaret.xcaret_hotel.view.category.ui

import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.PoolDetailFragmentBinding
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.category.vm.PoolDetailViewModel
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.FragmentTags

class PoolDetailFragment: BaseFragmentDataBinding<PoolDetailViewModel, PoolDetailFragmentBinding>(){
    override fun getLayout(): Int = R.layout.pool_detail_fragment
    override fun getViewModelClass(): Class<PoolDetailViewModel> = PoolDetailViewModel::class.java

    override fun bindViewToModel() { binding.viewModel = viewModel }

    override fun setupUI() {
        showSystemUI()
        viewModel.poolUID.value = arguments?.getString(Constants.POOL_UID) ?: ""
        viewModel.callFromMap.value = arguments?.getBoolean(Constants.CALLING_FROM_MAP) ?: false
        setObserver()
    }

    private fun setObserver(){
        viewModel.findById().observe(this, Observer {
            viewModel.poolLiveData.value = it
            setObserverCategory()
        })
    }

    private fun setObserverCategory(){
        cleanObservers()
        viewModel.findCategoryById().observe(this, Observer {
            viewModel.categoryLiveData.value = it
        })
    }

    private fun cleanObservers(){
        viewModel.findCategoryById().removeObservers(this)
    }

}