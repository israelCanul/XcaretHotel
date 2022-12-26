package com.xcaret.xcaret_hotel.view.category.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.BuildingDetailFragmentBinding
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.category.vm.BuildingDetailViewModel
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticConstant
import com.xcaret.xcaret_hotel.view.config.analytics.logEvent
import kotlinx.android.synthetic.main.building_detail_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class BuildingDetailFragment: BaseFragmentDataBinding<BuildingDetailViewModel, BuildingDetailFragmentBinding>(){
    override fun getLayout(): Int = R.layout.building_detail_fragment
    override fun getViewModelClass(): Class<BuildingDetailViewModel> = BuildingDetailViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val filterAdapter = GenericAdapter<Category>(R.layout.item_filter_building, 0.31f, 0f)
    private val roomTypeAdapter = GenericAdapter<RoomType>(R.layout.item_room_v2, 0.92f, 0f)
    private val restaurantAdapter = GenericAdapter<Place>(R.layout.item_restaurant_v2, 0.8f, 0f)
    private val barAdapter = GenericAdapter<Place>(R.layout.item_restaurant_v2, 0.8f, 0f)
    private var roomQuotesAdapter = GenericAdapter<SuiteQuotes>(R.layout.item_room_quotes)

    override fun setupUI(){
        filterAdapter.setOffsetHorizontalMargin(requireContext().resources.getDimensionPixelSize(R.dimen.margin_short).toFloat())
        viewModel.hotelLive.value = _parentActivity?.currentHotel
        viewModel.hotelIdLiveData.value = _parentActivity?.currentHotel?.idSynxis?.toIntOrNull() ?: 0
        viewModel.categoryUIDSelected.value = arguments?.getString(Constants.CAT_UID) ?: ""
        setObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents(){
        headerLayout.bringToFront()
        animToolbar(nestedScrollView = nestedScroll)
        Utils.heighPercentage(headerLayout, 0.40f)
        rvCategories.adapter = filterAdapter
        rvRooms.adapter = roomTypeAdapter
        rvRestaurants.adapter = restaurantAdapter
        rvBar.adapter = barAdapter
        rvRoomsQuotess.adapter = roomQuotesAdapter
        setListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun setObservers(){
        viewModel.categorySelectedLiveData.observe(this, Observer {
            _parentActivity?.setTitle(it.lang?.title)
            setObserverByCategory()
        })

        viewModel.findCategoryBuilding().observe(this, Observer {
            viewModel.categoryLiveData.value = it
            setObserverFilters()
        })

        viewModel.findCategoryRoom().observe(this, Observer {
            viewModel.categoryRoomLiveData.value = it
        })

        viewModel.findCategoryRestaurant().observe(this, Observer {
            viewModel.categoryRestLiveData.value = it
            setObserverFilterGroupRest()
        })

        viewModel.findCategoryBar().observe(this, Observer {
            viewModel.categoryBarLiveData.value = it
        })

        viewModel.findWelcomenLabel().observe(this, Observer {
            viewModel.welcomeLabelLive.value = it
        })

        //quotes
        viewModel.allDateByHotelId().observe(this, Observer { dq ->
            dq?.let {
                viewModel.dateLiveData.value = dq
                viewModel.hasDateLivedata.value = true
            }
        })

        viewModel.allSuitesByHotelIdExceptAdd().observe(this, Observer {
            roomQuotesAdapter.addItems(it)
            it.find { it.isSelected }?.let { sq ->
                viewModel.suiteQuoteSelected.value = sq
            }
        })

        viewModel.suiteQuoteSelected.observe(this, Observer { sq ->
            sq?.let { setObserverRatePlans() }
        })

        viewModel.dateLiveData.observe(this, Observer { dateQuotes ->
            dateQuotes?.let { dq ->
                val arrivalDateFormat = DateUtil.changeFormatDate(
                    dq.dateArrival,
                    DateUtil.DATE_FORMAT_WEATHER,
                    DateUtil.QUOTES_FORMAT
                )
                val departureDateFormat = DateUtil.changeFormatDate(
                    dq.dateDeparture,
                    DateUtil.DATE_FORMAT_WEATHER,
                    DateUtil.QUOTES_FORMAT
                )
                txtVisitDate.text = "$arrivalDateFormat - $departureDateFormat"
            }
        })

    }

    private fun setObserverRatePlans(){
        viewModel.findRatePlansByRoomHotelId().removeObservers(this)
        viewModel.findRatePlansByRoomHotelId().observe(this, Observer { listRatePlans ->
            doAsync {
                val orderRoom = mutableListOf<RoomType>()
                //listRatePlans ya viene ordernado de mayor a menor
                if (listRatePlans.isNotEmpty()) {
                    //hacemos un filtro y asignamos los rate plan al tipo de habitacion correspondiente
                    val groupRatePlans = listRatePlans.groupBy { it.roomCode }
                    groupRatePlans.forEach { (roomCode, lRatePlans) ->
                        val roomType = roomTypeAdapter.getItems().find { it.codeSynxis.equals(roomCode, ignoreCase = true) }
                        roomType?.let { rt ->
                            rt.ratePlan.clear()
                            rt.ratePlan.addAll(lRatePlans)
                            rt.hasDate = viewModel.hasDateLivedata.value ?: false
                            orderRoom.add(rt)
                        }
                    }
                }

                //agregamos el resto de habitaciones que no tienen rate plan
                roomTypeAdapter.getItems().forEach { rt ->
                    if(orderRoom.firstOrNull { it.code.equals(rt.code, true) } == null) {
                        rt.hasDate = viewModel.hasDateLivedata.value ?: false
                        orderRoom.add(rt)
                    }
                }
                uiThread {
                    if(orderRoom.isNotEmpty()) {
                        txtTitleRooms.visibility = View.VISIBLE
                        roomTypeAdapter.addItems(orderRoom)
                    }else{
                        txtTitleRooms.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun setObserverFilters(){
        viewModel.allByFilterGroup().observe(this, Observer { list ->
            val oldData = filterAdapter.getItems()
            val oldSelected = oldData.firstOrNull { it.selected }

            list.forEach {
                it.colorBackground = Utils.getColorByHouse(it.code)
                it.selected = false
            }

            oldSelected?.let { cat ->
                val current = list.firstOrNull { it.uid == cat.uid }
                current?.let {c ->
                    val index = list.indexOf(c)
                    if(index > -1) list[index].selected = true
                }
            } ?: kotlin.run {
                if(list.isNotEmpty()) {
                    if(!viewModel.categoryUIDSelected.value.isNullOrEmpty())
                        list.firstOrNull { it.uid == viewModel.categoryUIDSelected.value }?.selected = true
                    else
                        list[0].selected = true
                }
            }

            val current = list.firstOrNull { it.selected }
            current?.let { c ->
                if(viewModel.categorySelectedLiveData.value?.uid != c.uid)
                    viewModel.categorySelectedLiveData.value = c
            }

            filterAdapter.addItems(list)
        })
    }

    private fun setObserverFilterGroupRest(){
        viewModel.findCategoryByFilterGroup().removeObservers(this)
        viewModel.findCategoryByFilterGroup().observe(this, Observer {
            viewModel.filterGroupRest.value = it
            viewModel.placeLiveData.value?.let {
                setObserverNearPlace()
            }
        })
    }

    private fun setObserverNearPlace(){
        restaurantAdapter.addItems(mutableListOf())
        barAdapter.addItems(mutableListOf())
        roomTypeAdapter.addItems(mutableListOf())
        viewModel.findNearPlace().removeObservers(this)
        viewModel.findNearPlace().observe(this, Observer { listNearPlace ->
            val categoryResIds = mutableListOf<String>()
            val categoryBarId = mutableListOf<String>()
            viewModel.filterGroupRest.value?.forEach { category ->
                category?.let { cat ->
                    if(cat.code == Constants.CATEGORY_REST_BAR_SNACK){
                        categoryBarId.add(cat.uid)
                    }else if(cat.code in Constants.LIST_CATEGORY_ONLY_REST){
                        categoryResIds.add(cat.uid)
                    }
                }
            }

            val restaurants = listNearPlace.filter { categoryResIds.contains(it.categoryUID) }
            val bars = listNearPlace.filter { categoryBarId.contains(it.categoryUID) }

            viewModel.completeInfo(restaurants){
                if (restaurants.isNotEmpty()){
                    binding.txtTitleRestaurant.visibility = View.VISIBLE
                    txtDescriptionRest.visibility = View.VISIBLE
                    restaurantAdapter.addItems(restaurants)
                }else
                {
                    txtTitleRestaurant.visibility = View.GONE
                    txtDescriptionRest.visibility = View.GONE
                }

            }
            viewModel.completeInfo(bars){
                if (bars.isNotEmpty()){
                    binding.txtTitleBar.visibility = View.VISIBLE
                    binding.txtDescriptionBar.visibility = View.VISIBLE
                    barAdapter.addItems(bars)
                }else
                {
                    txtTitleBar.visibility = View.GONE
                    txtDescriptionBar.visibility = View.GONE
                }

            }
        })
    }

    private fun setObserverByCategory(){
        cleanObservers()
        viewModel.findByCategory().observe(this, Observer {
            completeInfo(it)
        })
    }

    private fun completeInfo(list: List<Place>){
        if(list.isNotEmpty()) {
            viewModel.placeLiveData.value = list[0]
            setObservableExtra()
            viewModel.filterGroupRest.value?.let {
                if(it.isNotEmpty()) setObserverNearPlace()
            }
        }
    }

    private fun setObservableExtra(){
        cleanObserverExtra()
        viewModel.findByLocation().observe(this, Observer {roomTypes ->
            completeInfoRooms(roomTypes)
        })
    }

    private fun completeInfoRooms(rooms: List<RoomType>){
        viewModel.allCategoryRoom().removeObservers(this)
        viewModel.allCategoryRoom().observe(this, Observer {catList ->
            rooms.forEach { r -> r.category = catList.firstOrNull { it.uid == r.categoryUID } }
            completeInfoAmenityRoom(rooms)
        })
    }

    private fun completeInfoAmenityRoom(list: List<RoomType>){
        viewModel.fetchCategoryAndMainAmenities(list){
            list.forEach {rType ->
                rType.mainAmenity?.let { listAmenities ->
                    val adapter = GenericAdapter<RoomAmenity>(R.layout.item_room_main_amenity_left_v2)
                    //val aux  = if(it.size >= 4) it.subList(0, it.size - 2) else it
                    val aux = mutableListOf<RoomAmenity>()
                    listAmenities.find { it.code.equals(Constants.AMENITY_CAPACITY) }?.let { aux.add(it) }
                    listAmenities.find { it.code.equals(Constants.AMENITY_SIZE) }?.let { aux.add(it) }

                    adapter.addItems(aux)
                    rType.mainAmenityAdapter = adapter
                }
            }
            if(list.isNotEmpty()){
                txtTitleRooms?.visibility = View.VISIBLE

                roomTypeAdapter.addItems(list)
            }else{
                txtTitleRooms?.visibility = View.GONE
            }

            viewModel.suiteQuoteSelected.value?.let { setObserverRatePlans() }
        }
    }

    private fun setListeners(){
        roomQuotesAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val currentSelected = roomQuotesAdapter.getItem(position)
                val oldSelected = roomQuotesAdapter.getItems().find { it.isSelected }
                if(currentSelected?.id != oldSelected?.id){
                    currentSelected?.isSelected = true
                    oldSelected?.isSelected = false
                    val listToUpdate = mutableListOf(currentSelected!!)
                    oldSelected?.let { listToUpdate.add(it) }
                    doAsync {
                        viewModel.saveOrUpdateListSuites(listToUpdate)
                        uiThread { viewModel.suiteQuoteSelected.value = currentSelected!! }
                    }
                }
            }
        })

        roomTypeAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val args = bundleOf(Constants.ROOM_UID to roomTypeAdapter.getItem(position)?.uid)
                findNavController().navigate(R.id.action_buildingDetailFragment_to_roomDetailFragment, args)
            }
        })

        restaurantAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val item = restaurantAdapter.getItem(position)
                logEvent(
                    AnalyticConstant.ID_RESERVATION_CODE.replace("_xx",item?.code!!),
                    AnalyticConstant.ITEM_NAME_RESERVATION_CODE.replace("_xx",item?.code!!),
                    AnalyticConstant.CONTENT_TYPE_GASTRONOMY
                )
                val args = bundleOf(Constants.REST_UID to restaurantAdapter.getItem(position)?.uid)
                findNavController().navigate(R.id.action_buildingDetailFragment_to_restaurantDetailFragment, args)
            }
        })

        barAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val args = bundleOf(Constants.REST_UID to barAdapter.getItem(position)?.uid)
                findNavController().navigate(R.id.action_buildingDetailFragment_to_restaurantDetailFragment, args)
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

        btnEdit.onClick {
            val args = bundleOf(Constants.SHOW_CALENDAR to true)
            findNavController().navigate(R.id.action_buildingDetailFragment_to_quoteFragment, args)
        }
        btnCotiza.onClick {
            val args = bundleOf(Constants.SHOW_CALENDAR to true)
            findNavController().navigate(R.id.action_buildingDetailFragment_to_quoteFragment, args)
        }
    }

    private fun cleanObservers(){
        viewModel.findByCategory().removeObservers(this)
    }

    private fun cleanObserverExtra(){
        viewModel.findByLocation().removeObservers(this)
    }
}