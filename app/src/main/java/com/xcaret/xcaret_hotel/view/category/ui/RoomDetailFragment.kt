package com.xcaret.xcaret_hotel.view.category.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.RoomDetailFragmentBinding
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.domain.RoomAmenity
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.category.vm.RoomDetailViewModel
import com.xcaret.xcaret_hotel.view.config.*
import kotlinx.android.synthetic.main.room_detail_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

import com.xcaret.xcaret_hotel.data.repository.FirebaseReference
import com.xcaret.xcaret_hotel.domain.Gallery

class RoomDetailFragment: BaseFragmentDataBinding<RoomDetailViewModel, RoomDetailFragmentBinding>(){
    override fun getLayout(): Int = R.layout.room_detail_fragment
    override fun getViewModelClass(): Class<RoomDetailViewModel> = RoomDetailViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val adapterLocation = GenericAdapter<Place>(R.layout.item_room_location)
    private val adapterMainAmenity = GenericAdapter<RoomAmenity>(R.layout.item_room_main_amenity_left)
    private var filterAdapter: GenericAdapter<Category>? = null
    private val galleryToShow = mutableListOf<Gallery>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    override fun setupUI() {
        viewModel.hotelIdLiveData.value = _parentActivity?.currentHotel?.idSynxis?.toIntOrNull() ?: 0
        viewModel.currentHotel.value =_parentActivity?.currentHotel
        viewModel.roomUID.value = arguments?.getString(Constants.ROOM_UID) ?: ""

        setObserverRoom()
    }

    private fun initComponents(){
        Utils.heighPercentage(headerLayout, 0.47f)
        rvLocations.adapter = adapterLocation
        rvMainAmenities.adapter = adapterMainAmenity
        setListeners()
        setUpGalleryView()
    }

    private fun setObserverRoom(){
        viewModel.findById().observe(this, Observer {
            viewModel.roomLiveData.value = it
            completeRoomInfo()
            setObserverRatePlans()
            addItemToGallery(mutableListOf(Gallery(path = "/${FirebaseReference.ROOM_TYPE}",name = it?.lang?.image)))
            viewModel.getImages(it?.uid?: "")
        })

        viewModel.getLocations().observe(this, Observer {
            adapterLocation.addItems(it)
        })

        viewModel.fetchMainAmenitiesForRoom {
            adapterMainAmenity.addItems(it)
        }

        viewModel.allByRoomUID().observe(this, Observer {
            viewModel.listAmenities.value = it
            setObserversCategory(it)
        })

        viewModel.filterCategoryAmenitySelected.observe(this, Observer { category ->
            viewModel.listAmenitiesSelected.value = viewModel.listAmenities.value?.filter { it.categoryUID == category.uid}
        })

        viewModel.listAmenitiesSelected.observe(this, Observer {
            if(it.isNotEmpty()){
                txtAmenityDescription.text = it[0].lang?.description
            }
        })

        viewModel.galleryList.observe(this) {
            if (it.isNotEmpty()) {
                addItemToGallery(it)
            }
        }

        //region quotes
        viewModel.allDateByHotelId().observe(this, Observer { dq ->
            dq?.let {
                viewModel.dateLiveData.value = dq
                viewModel.hasDateLivedata.value = true

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

        viewModel.findSelectedByHotelId().observe(this, Observer {
            viewModel.suiteQuotesSelected.value = it
            showInfoVisitors()
        })
        //endregion
    }

    private fun setObserverRatePlans(){
        viewModel.findRatePlansByRoomHotelId().removeObservers(this)
        viewModel.findRatePlansByRoomHotelId().observe(this, Observer { listRatePlans ->
            viewModel.hasRatePlansLiveData.value = listRatePlans.isNotEmpty()
            if(listRatePlans.isNotEmpty()){
                var cheapRatePlan = listRatePlans[0]
                listRatePlans.forEach { rp ->
                    if(rp.averageAmount < cheapRatePlan.averageAmount)
                        cheapRatePlan = rp
                }
                viewModel.ratePlanLiveDate.value = cheapRatePlan
            }
        })
    }

    private fun setListeners(){
        btnReserve.onClick {
            val args = bundleOf(Constants.PRE_SELECT_ROOM to viewModel.roomLiveData.value?.codeSynxis)
            findNavController().navigate(R.id.action_roomDetailFragment_to_quoteFragment, args)
        }

        btnQuotes.onClick {
            val args = bundleOf(Constants.SHOW_CALENDAR to true)
            findNavController().navigate(R.id.action_roomDetailFragment_to_quoteFragment, args)
        }
    }

    private fun showInfoVisitors(){
        txtChildren.text = ""
        lblChildren.makeGone()
        viewModel.suiteQuotesSelected.value?.let { sq ->
            txtAdults.text = "${sq.adults} "
            if(sq.children > 0){
                txtChildren.text = " - ${sq.children}"
                lblChildren.makeVisible()
            }
        }
    }

    private fun setObserversCategory(categoryRoomAmenities: List<RoomAmenity>){
        doAsync {
            val groupByCategory = categoryRoomAmenities.groupBy { it.categoryUID }.toMutableMap()
            val mainCategory = viewModel.findCategoryByCode(Constants.AMENITY_ROOM_MAIN)
            groupByCategory.remove(mainCategory?.uid)
            val listCategoryUID = mutableListOf<String>()
            groupByCategory.keys.forEach { key ->
                key?.let { listCategoryUID.add(key) }
            }
            val categories = viewModel.findByListIds(listCategoryUID)
            uiThread {
                setupFiltersCategory(categories)
            }
        }
    }

    private fun setupFiltersCategory(list: List<Category>){
        filterAdapter = GenericAdapter(R.layout.item_filter_generic, 0.3f, 0f)
        rvCategoryAmenities.adapter = filterAdapter
        if(list.isNotEmpty()) {
            list[0].selected = true
            viewModel.filterCategoryAmenitySelected.value = list[0]
        }
        filterAdapter?.addItems(list)

        filterAdapter?.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val selected = filterAdapter?.getItems()?.get(position)
                if(selected?.uid == viewModel.filterCategoryAmenitySelected.value?.uid) return
                filterAdapter?.getItems()?.forEach { it.selected = false }
                filterAdapter?.getItems()?.get(position)?.selected = true
                rvCategoryAmenities.validateAndScrollToPosition(position)
                filterAdapter?.notifyDataSetChanged()
                viewModel.filterCategoryAmenitySelected.value = selected
            }
        })

    }

    private fun completeRoomInfo(){
        viewModel.findCategoryById().observe(this, Observer {
            viewModel.roomCategoryLiveData.postValue(it)
        })
    }

    //region Images
    fun setUpGalleryView(){
        ivHeader.setUpViewer(_parentActivity?.currentHotel?.code?:"", _parentActivity?.firebaseStorage)
    }

    fun addItemToGallery(itemsGallery:List<Gallery>){
        //val items = mutableListOf<Gallery>()
        //items.add(itemGallery)
        //viewModel.galleryListToShow.value?.let { items.addAll(it) }
        //viewModel.galleryListToShow.postValue(items)
        galleryToShow.addAll(itemsGallery)

        ivHeader.setUpAdapter(galleryToShow)
    }
    //endregion


}