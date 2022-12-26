package com.xcaret.xcaret_hotel.view.afi.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.ParkDetailFragmentBinding
import com.xcaret.xcaret_hotel.domain.Award
import com.xcaret.xcaret_hotel.view.afi.vm.ParkDetailViewModel
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.*
import kotlinx.android.synthetic.main.park_detail_fragment.*
import kotlinx.android.synthetic.main.park_detail_fragment.headerLayout
import kotlinx.android.synthetic.main.park_detail_fragment.rvAwards

class ParkDetailFragment: BaseFragmentDataBinding<ParkDetailViewModel, ParkDetailFragmentBinding>(){
    override fun getLayout(): Int = R.layout.park_detail_fragment
    override fun getViewModelClass(): Class<ParkDetailViewModel> = ParkDetailViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val awardAdapter = GenericAdapter<Award>(R.layout.item_award, 0.25f, 0.1f)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    override fun setupUI() {
        viewModel.parkUIDLiveData.value = arguments?.getString(Constants.PARK_UID) ?: ""
        setObservers()
    }

    private fun initComponents(){
        rvAwards.adapter = awardAdapter
        Utils.heighPercentage(headerLayout, 0.6f)
        Utils.heighPercentage(rvAwards, 0.095f)
        setListeners()
    }

    private fun setObservers(){
        viewModel.findById().observe(this, Observer {
            viewModel.parkLiveData.value = it
            addObserversSwitch()
        })

        viewModel.findAwardByParkUID().observe(this, Observer {
            awardAdapter.addItems(it)
        })
    }

    private fun addObserversSwitch(){
        viewModel.switchValue.removeObservers(this)
        viewModel.switchValue.observe(this, Observer {
            val content: String? =
                if(it)
                    viewModel.parkLiveData.value?.lang?.recomendation
                else
                    viewModel.parkLiveData.value?.lang?.include
            viewModel.switchContentValue.value = content
        })
    }

    private fun setListeners(){
        svTab.setOnChangeListener(object: SwitchNeumorphisView.OnChangeListener{
            override fun onChange(leftActive: Boolean) {
                viewModel.switchValue.value = leftActive
            }
        })

        ivMap.setOnClickListener {
            Utils.goToNavigation(viewModel.parkLiveData.value?.latitude ?: 0.0, viewModel.parkLiveData.value?.longitude ?: 0.0, context)
        }
    }
}