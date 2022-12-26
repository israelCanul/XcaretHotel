package com.xcaret.xcaret_hotel.view.category.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.RestaurantListFragmentBinding
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.category.vm.RestaurantListViewModel
import com.xcaret.xcaret_hotel.view.config.*
import kotlinx.android.synthetic.main.restaurant_list_fragment.*
import kotlinx.android.synthetic.main.restaurant_list_fragment.rvRestaurants
import kotlinx.android.synthetic.main.room_list_fragment.headerLayout
import kotlinx.android.synthetic.main.room_list_fragment.rvCategories

class RestaurantListFragment: BaseFragmentDataBinding<RestaurantListViewModel, RestaurantListFragmentBinding>() {
    override fun getLayout(): Int = R.layout.restaurant_list_fragment
    override fun getViewModelClass(): Class<RestaurantListViewModel> = RestaurantListViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private var restaurantAdapter = GenericAdapter<Place>(R.layout.item_restaurant, 0f, 0f)
    private val filterAdapter = GenericAdapter<Category>(R.layout.item_filter_generic, 0.33f, 0f)

    override fun setupUI() {
        showSystemUI()
        setObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogHX.e("onViewCreated", "RestaurantListFragment")
        initComponents()
    }

    private fun initComponents(){
        animToolbar(nestedScrollView = nestedScroll)
        headerLayout.bringToFront()
        Utils.heighPercentage(headerLayout, 0.30f)
        setListeners()
        filterAdapter.setOffsetHorizontalMargin(requireContext().resources.getDimension(R.dimen.margin_short))
        rvRestaurants.adapter = restaurantAdapter
        rvCategories.adapter = filterAdapter
    }

    private fun setObservers(){
        viewModel.categorySelectedLiveData.observe(this, Observer {
            setObserverByCategory()
        })

        viewModel.findCategoryByCodeLive().observe(this, Observer {
            viewModel.categoryLiveData.value = it
            setObserverFilters()
        })
    }

    private fun setObserverFilters(){
        viewModel.allByFilterGroup().observe(this, Observer { list ->
            val oldData = filterAdapter.getItems()
            val oldSelected = oldData.firstOrNull { it.selected }

            list.forEach { it.selected = false }

            oldSelected?.let { cat ->
                val current = list.firstOrNull { it.uid == cat.uid }
                current?.let {c ->
                    val index = list.indexOf(c)
                    if(index > -1) list[index].selected = true
                }
            } ?: kotlin.run {
                if(list.isNotEmpty()) list[0].selected = true
            }

            val current = list.firstOrNull { it.selected }
            current?.let { c ->
                if(viewModel.categorySelectedLiveData.value?.uid != c.uid)
                    viewModel.categorySelectedLiveData.value = c
            }

            filterAdapter.addItems(list)
        })
    }

    private fun setObserverByCategory(){
        viewModel.findByCategory().removeObservers(this)
        viewModel.loadingLiveData.value = true
        viewModel.findByCategory().observe(this, Observer {
            completeInfo(it)
        })
    }

    private fun completeInfo(list: List<Place>){
        viewModel.completeInfo(list){
            viewModel.loadingLiveData.value = false
            restaurantAdapter.addItems(list)
        }
    }

    private fun setListeners(){
        restaurantAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val rest = restaurantAdapter.getItem(position)
                val args = bundleOf(Constants.REST_UID to rest?.uid)
                navigate(R.id.action_restaurantListFragment_to_restaurantDetailFragment, args)
            }
        })

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
    }
}