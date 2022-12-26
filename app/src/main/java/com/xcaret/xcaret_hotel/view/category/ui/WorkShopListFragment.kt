package com.xcaret.xcaret_hotel.view.category.ui

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.WorkshopListFragmentBinding
import com.xcaret.xcaret_hotel.domain.Activity
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.category.vm.WorkShopListViewModel
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.GenericAdapter
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.validateAndScrollToPosition
import kotlinx.android.synthetic.main.workshop_list_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread

class WorkShopListFragment: BaseFragmentDataBinding<WorkShopListViewModel, WorkshopListFragmentBinding>() {
    override fun getLayout(): Int = R.layout.workshop_list_fragment
    override fun getViewModelClass(): Class<WorkShopListViewModel> = WorkShopListViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val filterAdapter = GenericAdapter<Category>(R.layout.item_filter_generic, 0.33f, 0f)
    private val workShopAdapter = GenericAdapter<Activity>(R.layout.item_workshop)

    override fun setupUI() {
        viewModel.categoryUID.value = arguments?.getString(Constants.CAT_UID)
        initComponents()
        setListeners()
        setObservers()
    }

    private fun initComponents(){
        animToolbar(nestedScrollView = binding.nestedScroll)
        binding.headerLayout.bringToFront()
        Utils.heighPercentage(binding.headerLayout, 0.30f)
        filterAdapter.setOffsetHorizontalMargin(requireContext().resources.getDimensionPixelSize(R.dimen.margin_short).toFloat())
        binding.rvWorkShop.adapter = workShopAdapter
        binding.rvCategories.adapter = filterAdapter
    }
    private fun setObservers(){
        viewModel.workShopList.observe(this, Observer {
            workShopAdapter.addItems(it)
            viewModel.loading.value = false
        })

        viewModel.findCategoryByUID().observe(this, Observer {
            viewModel.categoryLiveData.value = it
        })

        viewModel.categorySelectedLiveData.observe(this, Observer { category ->
            category?.let { cat ->
                viewModel.loading.value = true
                setObserverWorkShop()
            }
        })

        viewModel.allByFilterGroup().observe(this, Observer { list ->
            doAsync {
                val oldData = filterAdapter.getItems()
                val oldSelected = oldData.firstOrNull { it.selected }

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

                val current = list.firstOrNull { it.selected }

                runOnUiThread {
                    current?.let { c ->
                        if (viewModel.categorySelectedLiveData.value?.uid != c.uid)
                            viewModel.categorySelectedLiveData.value = c
                    }
                    filterAdapter.addItems(list)
                }
            }
        })
    }

    private fun setObserverWorkShop(){
        viewModel.allActivitiesByCategory().removeObservers(this)
        viewModel.allActivitiesByCategory().observe(this, Observer { list ->
            viewModel.completeLocations(list){
                viewModel.workShopList.value = list
            }
        })
    }

    private fun setListeners(){
        filterAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val selected = filterAdapter.getItems()[position]
                if(selected.uid == viewModel.categorySelectedLiveData.value?.uid) return
                filterAdapter.getItems().forEach { it.selected = false }
                filterAdapter.getItems()[position].selected = true
                rvCategories.validateAndScrollToPosition(position)
                filterAdapter.notifyDataSetChanged()
                viewModel.categorySelectedLiveData.value = selected
            }
        })

        workShopAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val args = bundleOf(
                    Constants.ACT_UID to workShopAdapter.getItem(position)?.uid,
                    Constants.CALLING_FROM_MAP to false
                )
                navigate(R.id.action_workShopListFragment_to_workShopDetailFragment, args)
            }
        })
    }
}