package com.xcaret.xcaret_hotel.view.afi.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.stfalcon.imageviewer.StfalconImageViewer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.FerryDetailFragmentBinding
import com.xcaret.xcaret_hotel.domain.Destination
import com.xcaret.xcaret_hotel.domain.DestinationActivity
import com.xcaret.xcaret_hotel.domain.Gallery
import com.xcaret.xcaret_hotel.domain.ParkTour
import com.xcaret.xcaret_hotel.view.afi.vm.FerryDetailViewModel
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.*
import kotlinx.android.synthetic.main.ferry_detail_fragment.*
import kotlinx.android.synthetic.main.park_detail_fragment.headerLayout
import kotlinx.android.synthetic.main.park_detail_fragment.ivMap
import kotlinx.android.synthetic.main.park_detail_fragment.rvAwards
import kotlinx.android.synthetic.main.park_detail_fragment.svTab

class FerryDetailFragment: BaseFragmentDataBinding<FerryDetailViewModel, FerryDetailFragmentBinding>(){
    override fun getLayout(): Int = R.layout.ferry_detail_fragment
    override fun getViewModelClass(): Class<FerryDetailViewModel> = FerryDetailViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val destinationActivityAdapter = GenericAdapter<DestinationActivity>(R.layout.item_destination_activity, 0.9f, 0.0f)
    private val destinationBenefitAdapter = GenericAdapter<DestinationActivity>(R.layout.item_destination_activity_benefits, 0.0f, 0.0f)
    private lateinit var viewer: StfalconImageViewer<Destination>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    override fun setupUI() {
        viewModel.parkUIDLiveData.value = arguments?.getString(Constants.PARK_UID) ?: ""
        setObservers()
    }

    private fun initComponents(){
        Utils.heighPercentage(headerLayout, 0.6f)
        setListeners()
        svTab.setTextValues(getString(R.string.rkey_lbl_whats_include_tour),getString(R.string.rkey_recomendation))
        binding.rvActivityDestinations.adapter = destinationActivityAdapter
        binding.rvActivityDestinations.setHasFixedSize(true)
        binding.rvBenefits.adapter = destinationBenefitAdapter
    }

    private fun setObservers(){
        viewModel.findById().observe(this, Observer {
            viewModel.parkLiveData.value = it
            addObserversSwitch()
        })

        viewModel.findDestinations().observe(this, Observer {
            val ir = it
            it.let {
                viewModel.currentDestinationLive.value = it[0]
            }
        })

        viewModel.currentDestinationLive.observe(this, Observer {
            it.let {
                val t = it
                addObserverToDestinationActivities()
            }

        })

        viewModel.destinationAttractionList.observe(this, Observer {
            if (it != null) {
                if(it.isNotEmpty()) {
                    destinationActivityAdapter.addItems(it)
                }
            }
        })
        viewModel.destinationBenefitsList.observe(this, Observer {
            if (it != null) {
                if(it.isNotEmpty()){
                    destinationBenefitAdapter.addItems(it)
                }
            }
        })

        viewModel.destinationServiceList.observe(this, Observer {
            if (it != null) {
                //viewModel.servicesBus.value = it[0]
            }
        })
    }

    private fun addObserverToDestinationActivities(){
        viewModel.findDestinationActivities().removeObservers(this)
        viewModel.findDestinationActivities().observe(this, Observer {
            val t = it
            viewModel.orderDestinantionsByType(it)

        })
    }

    private fun addObserversSwitch(){
        viewModel.switchValue.removeObservers(this)
        viewModel.switchValue.observe(this, Observer {
            val content: String? =
                if(it)
                    viewModel.parkLiveData.value?.lang?.include
                else
                    viewModel.parkLiveData.value?.lang?.recomendation
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
            setUpImageViewer()

        }
    }

    private fun setUpImageViewer() {
        val list =  mutableListOf<Destination>().apply {
            viewModel.currentDestinationLive.value?.let { add(it) }
        }
        viewer = StfalconImageViewer.Builder<Destination>(context, list, ::loadPosterImage)
            .withStartPosition(0)
            .withHiddenStatusBar(false)
            .show()
    }

    private fun loadPosterImage(imageView: ImageView, poster: Destination?) {
        val realPath = Constants.PATH_DESTINATION
        val referenceOrigin =
            poster?.image?.let { _parentActivity?.firebaseStorage?.getReference(realPath)?.child("${it}.jpg") }
        imageView.apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            background = this.resources.getDrawable(R.drawable.bg_imageviewer)
            if (poster != null) {
                loadImage(referenceOrigin!!)
            }
        }
    }
}