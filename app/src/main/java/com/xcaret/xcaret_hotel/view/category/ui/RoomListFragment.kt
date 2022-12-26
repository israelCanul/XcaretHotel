package com.xcaret.xcaret_hotel.view.category.ui

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.RoomListFragmentBinding
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.RoomAmenity
import com.xcaret.xcaret_hotel.domain.RoomType
import com.xcaret.xcaret_hotel.domain.SuiteQuotes
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.category.vm.RoomListViewModel
import com.xcaret.xcaret_hotel.view.config.*
import kotlinx.android.synthetic.main.room_list_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.IllegalArgumentException

class RoomListFragment: BaseFragmentDataBinding<RoomListViewModel, RoomListFragmentBinding>(){
    override fun getLayout(): Int = R.layout.room_list_fragment
    override fun getViewModelClass(): Class<RoomListViewModel> = RoomListViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val roomTypeAdapter = GenericAdapter<RoomType>(R.layout.item_room, 0.0f, 0f)
    private val filterAdapter = GenericAdapter<Category>(R.layout.item_filter_generic, 0.31f, 0f)
    private var roomQuotesAdapter = GenericAdapter<SuiteQuotes>(R.layout.item_room_quotes)


    override fun setupUI() {
        showSystemUI()
        initComponents()
        viewModel.hotelLive.value = _parentActivity?.currentHotel
        viewModel.hotelIdLiveData.value = _parentActivity?.currentHotel?.idSynxis?.toIntOrNull() ?: 0
        viewModel.hotelUIDLiveData.value = arguments?.getString(Constants.HOTEL_UID) ?: ""
        setObservers()
    }

    private fun initComponents(){
        animToolbar(nestedScrollView = binding.layoutMain)
        binding.headerLayout.bringToFront()
        Utils.heighPercentage(binding.headerLayout, 0.30f)
        setListeners()
        filterAdapter.setOffsetHorizontalMargin(requireContext().resources.getDimensionPixelSize(R.dimen.margin_short).toFloat())
        binding.rvRoomsQuotess.adapter = roomQuotesAdapter
        binding.rvCategories.adapter = filterAdapter
        binding.rvRooms.adapter = roomTypeAdapter
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
                try {
                    val args = bundleOf(Constants.ROOM_UID to roomTypeAdapter.getItem(position)?.uid)
                    findNavController().navigate(R.id.action_roomListFragment_to_roomDetailFragment, args)
                }catch (exc:IllegalArgumentException){

                }

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

        binding.btnEdit.onClick {
            val args = bundleOf(Constants.SHOW_CALENDAR to true)
            findNavController().navigate(R.id.action_roomListFragment_to_quoteFragment, args)
        }
        binding.btnCotiza.onClick {
            val args = bundleOf(Constants.SHOW_CALENDAR to true)
            findNavController().navigate(R.id.action_roomListFragment_to_quoteFragment, args)
        }
    }

    private fun setObservers(){
        viewModel.categorySelectedLiveData.observe(this, Observer {
            setObserverByCategory()
        })

        viewModel.findCategoryByCodeLive().observe(this, Observer {
            viewModel.categoryLiveData.value = it
            setObserverFilters()
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
                binding.txtVisitDate.text = "$arrivalDateFormat - $departureDateFormat"
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

                    //agregamos el resto de habitaciones que no tienen rate plan
                    roomTypeAdapter.getItems().forEach { rt ->
                        if(orderRoom.firstOrNull { it.code.equals(rt.code, true) } == null) {
                            rt.hasDate = viewModel.hasDateLivedata.value ?: false
                            orderRoom.add(rt)
                        }
                    }
                }
                uiThread {
                    if(orderRoom.isNotEmpty()) roomTypeAdapter.addItems(orderRoom)
                }
            }
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
        cleanObservers()
        viewModel.loadingLiveData.value = true
        viewModel.findByCategory().observe(this, Observer {
            completeInfo(it)
        })
    }

    private fun completeInfo(list: List<RoomType>){
        viewModel.fetchCategoryAndMainAmenities(list){
            list.forEach {rType ->
                rType.mainAmenity?.let { listAmenities ->
                    val adapter = GenericAdapter<RoomAmenity>(R.layout.item_room_main_amenity_right)
                    //val aux  = if(it.size >= 2) it.subList(0, 2) else it
                    val aux = mutableListOf<RoomAmenity>()
                    listAmenities.find { it.code.equals(Constants.AMENITY_CAPACITY) }?.let { aux.add(it) }
                    listAmenities.find { it.code.equals(Constants.AMENITY_VIEW_CODE) }?.let { aux.add(it) }

                    adapter.addItems(aux)
                    rType.mainAmenityAdapter = adapter
                }
            }
            roomTypeAdapter.addItems(list)
            viewModel.loadingLiveData.value = false
            viewModel.suiteQuoteSelected.value?.let { setObserverRatePlans() }
        }
    }

    private fun cleanObservers(){
        viewModel.findByCategory().removeObservers(this)
    }

}