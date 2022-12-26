package com.xcaret.xcaret_hotel.view.tabs.ui

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.AfiFragmentBinding
import com.xcaret.xcaret_hotel.domain.AfiClass
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.ParkTour
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticConstant
import com.xcaret.xcaret_hotel.view.config.analytics.logEvent
import com.xcaret.xcaret_hotel.view.tabs.vm.AFIViewModel
import kotlinx.android.synthetic.main.afi_fragment.*

class AFIFragment: BaseFragmentDataBinding<AFIViewModel, AfiFragmentBinding>(){
    override fun getLayout(): Int = R.layout.afi_fragment
    override fun getViewModelClass(): Class<AFIViewModel> = AFIViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    //private val filterAdapter = GenericAdapter<Category>(R.layout.item_filter_generic, 0.31f, 0.0f)
    private val filterAdapter = GenericAdapter<AfiClass>(R.layout.item_filter_afi, 0.31f, 0.0f)
    private val parkTourAdapter = GenericAdapter<ParkTour>(R.layout.item_park, 0.9f, 0.0f)

    override fun setupUI() {
        initComponents()
        setObservers()
    }

    private fun initComponents(){
        showSystemUI()
        setImgAfi()
        animToolbar(nestedScrollView = binding.layoutMain)
        Utils.heighPercentage(binding.headerLayout, 0.47f)
        Utils.heighPercentage(binding.logoAfi, 0.095f)
        binding.rvParks.adapter = parkTourAdapter
        binding.rvCategories.adapter = filterAdapter
        setListeners()

        logEvent(
            AnalyticConstant.ID_NAV_AFI,
            AnalyticConstant.ITEM_NAME_NAV_AFI,
            AnalyticConstant.CONTENT_TYPE_NAVTABBAR)
    }

    private fun setImgAfi(){
        val imgAfiName = Constants.AFI_RES + Language.getLangCode(requireContext())
        val imgResource = Utils.getDrawableId(context, imgAfiName)
        imgResource?.let {img ->
            if(img > 0) binding.imgAfi.setImageResource(img)
            else binding.imgAfi.setImageResource(R.drawable.afi_en)
        } ?: binding.imgAfi.setImageResource(R.drawable.afi_en)
    }

    private fun setObservers(){
        viewModel.findCategoryByCode().observe(this, Observer {
            viewModel.categoryLiveData.value = it
            //setObserverFilters()
        })

        viewModel.categorySelectedLiveData.observe(this, Observer {
            setObserverByCategory()
        })

        viewModel.afiClassSelectedLiveData.observe(this, Observer {
            setObserverByAfiClass()
        })

        viewModel.findVideoByCode().observe(this, Observer {
            viewModel.videoLiveData.value = it
        })
        viewModel.findAFIClasses().observe(this, Observer { list->
            if (list != null) {
                val oldData = filterAdapter.getItems()
                val oldSelected = oldData.firstOrNull { it.selected == true }

                list.forEach { it.selected = false }

                oldSelected?.let { cat ->
                    val current = list.firstOrNull { it.uid == cat.uid }
                    current?.let { c ->
                        val index = list.indexOf(c)
                        if (index > -1) list[index].selected = true
                    }
                } ?: kotlin.run {
                    if (list.isNotEmpty()) list[0].selected = true
                }

                val current = list.firstOrNull { it.selected == true }
                current?.let { c ->
                    if (viewModel.afiClassSelectedLiveData.value?.uid != c.uid)
                        viewModel.afiClassSelectedLiveData.value = c
                }

                filterAdapter.addItems(list)
            }
        })
    }

    private fun setObserverFilters(){
//        viewModel.allByFilterGroup().removeObservers(this)
//        viewModel.allByFilterGroup().observe(this, Observer { list ->
//            val oldData = filterAdapter.getItems()
//            val oldSelected = oldData.firstOrNull { it.selected }
//
//            list.forEach { it.selected = false }
//
//            oldSelected?.let { cat ->
//                val current = list.firstOrNull { it.uid == cat.uid }
//                current?.let {c ->
//                    val index = list.indexOf(c)
//                    if(index > -1) list[index].selected = true
//                }
//            } ?: kotlin.run {
//                if(list.isNotEmpty()) list[0].selected = true
//            }
//
//            val current = list.firstOrNull { it.selected }
//            current?.let { c ->
//                if(viewModel.categorySelectedLiveData.value?.uid != c.uid)
//                    viewModel.categorySelectedLiveData.value = c
//            }
//
//            filterAdapter.addItems(list)
//        })
    }

    private fun setObserverByCategory(){
        viewModel.findCategoryByCode().removeObservers(this)
        viewModel.findByCategory().observe(this, Observer {
            parkTourAdapter.addItems(it)

        })
    }
    private fun setObserverByAfiClass(){
        viewModel.findByClass().removeObservers(this)
        viewModel.findByClass().observe(this, Observer {
            parkTourAdapter.addItems(it)
        })
    }

    private fun setListeners(){
        parkTourAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val itemSelected = parkTourAdapter.getItem(position)
                if (viewModel.afiClassSelectedLiveData.value?.code != Constants.AFICLASS_CODE_FERRY){
                    val navController = findNavController()
                    val args = bundleOf(Constants.PARK_UID to parkTourAdapter.getItem(position)?.uid)
                    navController.navigate(R.id.action_AFIFragment_to_parkDetailFragment, args)
                }else{
                    val navController = findNavController()
                    val args = bundleOf(Constants.PARK_UID to parkTourAdapter.getItem(position)?.uid)
                    navController.navigate(R.id.action_AFIFragment_to_ferryDetailFragment, args)
                }

            }
        })

        binding.headerLayout.setOnClickListener {
            viewModel.videoLiveData.value?.let {video ->
                video.videoId?.let {
                    logEvent(AnalyticConstant.ID_VIDEO_CODE.replace("_xx",video.code!!),
                        AnalyticConstant.ITEM_NAME_VIDE_CODE.replace("_xx",video.code!!),
                        AnalyticConstant.CONTENT_TYPE_AFI
                    )
                    val args = bundleOf(Constants.VIDEO_ID to it)
                    findNavController().navigate(R.id.action_AFIFragment_to_youTubePlayerActivity, args)
                }
            }
        }

        filterAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val selected = filterAdapter.getItems()[position]
                if(selected.uid == viewModel.categorySelectedLiveData.value?.uid) return
                filterAdapter.getItems().forEach { it.selected = false }
                filterAdapter.getItems()[position].selected = true
                rvCategories.validateAndScrollToPosition(position)
                filterAdapter.notifyDataSetChanged()
                //viewModel.categorySelectedLiveData.value = selected
                viewModel.afiClassSelectedLiveData.value = selected

                logEvent(AnalyticConstant.ID_PARK_CODE.replace("_xx",selected.code!!),
                    AnalyticConstant.ITEM_NAME_PARK_CODE.replace("_xx",selected.code!!),
                    AnalyticConstant.CONTENT_TYPE_AFIPARKS
                )
            }
        })
    }
}