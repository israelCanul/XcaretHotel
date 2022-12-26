package com.xcaret.xcaret_hotel.view.menu.ui

import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.entity.ReservationAction
import com.xcaret.xcaret_hotel.data.entity.ReservationDetailResponse
import com.xcaret.xcaret_hotel.databinding.MyReservationsFragmentBinding
import com.xcaret.xcaret_hotel.domain.Reservation
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticConstant
import com.xcaret.xcaret_hotel.view.config.analytics.logEvent
import com.xcaret.xcaret_hotel.view.general.ui.DialogAlert
import com.xcaret.xcaret_hotel.view.general.ui.DialogAlertCancel
import com.xcaret.xcaret_hotel.view.menu.vm.MyReservationsViewModel
import kotlinx.android.synthetic.main.my_reservations_fragment.*
import org.jetbrains.anko.support.v4.runOnUiThread
import kotlin.Exception

class MyReservationsFragment: BaseFragmentDataBinding<MyReservationsViewModel, MyReservationsFragmentBinding>() {
    override fun getLayout(): Int = R.layout.my_reservations_fragment
    override fun getViewModelClass(): Class<MyReservationsViewModel> = MyReservationsViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    //private val reservationAdapter = ReservationAdapter()
    private val reservationAdapter = GenericAdapter<Reservation>(R.layout.item_reservations)


    private val faqsDialog: FaqsDialogFragment by lazy {
        FaqsDialogFragment.newInstance()
    }

    private val reservationCancelDialog: DialogAlertCancel by lazy {
        DialogAlertCancel.newInstance()
    }



    override fun setupUI() {
        initComponents()
        settingsManager.getUID.asLiveData().observe(this, Observer {
            viewModel.getSession().removeObservers(this)
            viewModel.uidLiveData.value = it
            setInitialObservers()
        })
        viewModel.getTotalRegisters()
        viewModel.doesDataFinished.value = true
        //viewModel.getReservationsInRange()
    }

    private fun initComponents(){
        Utils.fixHeightTopView(binding.statusBarFix)
        binding.rvReservations.adapter = reservationAdapter
        binding.rvReservations.setHasFixedSize(true)
        setListeners()



        logEvent(
            AnalyticConstant.ID_MENU_RESERVATIONS,
            AnalyticConstant.ITEM_NAME_MENU_RESERVATIONS,
            AnalyticConstant.CONTENT_TYPE_MENU)
    }


    private fun setListeners(){
        binding.btnFaqs.onClick {

        }

        binding.btnStayWithUs.onClick {
            navigate(R.id.action_my_reservations_to_quotefragment)
        }

        binding.txtErrorRetry.onClick {
            //fetchRemoteReservation()
            viewModel.getReservationsHotel(viewModel.userLiveData.value?.email!!)

        }
        binding.btnBack.onClick {
            //_parentActivity?.onBackPressed()
            //removeFragment()
            try {
                navigate(R.id.action_myReservationsFragment_to_menuFragment)
                removeFragment()
            }catch (exc:Exception){
                Log.e("btnback",exc.toString())
            }
        }
        binding.btnFaqs.onClick {
            //faqsDialog.show(childFragmentManager,FaqsDialogFragment.TAG)
            try {
                navigate(R.id.action_myReservationsFragment_to_FAQsFragment)
            }catch (exc:Exception){
                Log.e("btnFaqs",exc.toString())
            }

        }

        binding.rvReservations.addOnScrollListener(scrollListner)

        reservationAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                if(view.id == R.id.btnMoreDetails){
                    reservationAdapter.getItems().find { it.expand }?.let { collapse ->
                        val posToCollapse = reservationAdapter.getItems().indexOf(collapse)
                        collapse.expand = false
                        reservationAdapter.notifyItemChanged(posToCollapse)
                    }

                    val toExpand = reservationAdapter.getItem(position)
                    toExpand?.let { te ->
                        te.expand = true
                        reservationAdapter.notifyItemChanged(position)
                    }
                }else if(view.id == R.id.btnLessDetails){
                    val toCollapse = reservationAdapter.getItem(position)
                    toCollapse?.let { tc ->
                        tc.expand = false
                        reservationAdapter.notifyItemChanged(position)
                    }
                }
                if(view.id == R.id.btnCancelReservation){
                    val item = reservationAdapter.getItem(position)
                    var isCallRequired = true
                    if (item?.policies?.contains("N/A") == true || item?.policies.isNullOrEmpty()){
                        item?.policies ="N/A"
                        isCallRequired = false
                    }
                    showAlert(item?.policies!!,isCallRequired,
                        object: DialogAlertCancel.onClickListener{
                            override fun onClick(v: View?, dialog: DialogFragment) {
                                dialog.dismiss()
                                //Call
                                _parentActivity?.showContactDialog(CallUsDialogFragment.CODE, Constants.CATEGORY_CALLUS_CODE)

                            }
                        }
                    ,object :DialogAlertCancel.onClickListener{
                            override fun onClick(v: View?, dialog: DialogFragment) {
                                dialog.dismiss()
                                //Cancel
                            }
                    }
                    )
                }
            }
        })

    }

    private fun setInitialObservers() {
        viewModel.getHotels().observe(this,{
            viewModel.listHotels.value = it
        })

        viewModel.getSession().observe(viewLifecycleOwner, Observer {
            viewModel.userLiveData.value = it
            //fetchRemoteReservation()
            viewModel.getReservationsHotel(viewModel.userLiveData.value?.email?:"")

        })

        viewModel.allReservations().observe(viewLifecycleOwner, Observer {
            //reservationAdapter.addItems(it)
            //viewModel.getReservationsInRange()
           // viewModel.Reservation.value = it
        })

        viewModel.Reservation.observe(this,{
            if(it!=null)  {
                rvReservations.visibility = View.VISIBLE
                if (it.isNotEmpty())reservationAdapter.addItems(it)
            }

        })

        viewModel.listReservationDetail.observe(this,{
            if (it.isNotEmpty()) {
                viewModel.getReservations(it)
                viewModel.sizeCaptured.value = it.size
            }else{
                viewModel.statusView.value = ReservationAction.ERROR_EMPTY
            }
        })

        viewModel.listReservations.observe(this,{ newItems ->

            if (!newItems.first()?.reservationNumber.isNullOrEmpty()) {
                viewModel.searchMore.value = 0

                viewModel.statusView.value = ReservationAction.SUCCESS
                newItems.forEach { item ->
                    if (item != null) {
                        reservationAdapter.addItem(item)
                    }
                }
                viewModel.lastItemRegister.value =
                    reservationAdapter.getItems().last().reservationNumber
            }else{
                viewModel.searchMore.value = 2
            }
            viewModel.doesDataFinished.value = true

        })

        viewModel.statusView.observe(this, {currentStatus->
            if(currentStatus == ReservationAction.ERROR_ENDPONT){
                viewModel.getReservationsInRange()

            }
        })
    }

    fun removeFragment() {
        viewModel.forceClose.value = true
        //viewModelStore.clear()
        val fragmentManager = parentFragmentManager
        fragmentManager.fragments.forEach {
            if (it is MyReservationsFragment)
                fragmentManager.beginTransaction().remove(it).commitAllowingStateLoss()
        }
    }


    //region Scroll Listener

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListner = object :RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }

        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            try {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount

                val isNotLoadingAndIsNotLastPage = !isLoading && !isLastPage
                val isAtLasItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                val isNotAtBegining = firstVisibleItemPosition >= 0
                val isTotalMoreThanVisible = isTotalMoreThanVisible(totalItemCount)

                viewModel.shouldPaginate.value =
                    isNotLoadingAndIsNotLastPage && isAtLasItem && isNotAtBegining &&
                            isTotalMoreThanVisible && isScrolling
                if (viewModel.shouldPaginate.value == true) {
                    if (viewModel.statusView.value == ReservationAction.ERROR_ENDPONT) {
                        DbModePagination(
                            isNotLoadingAndIsNotLastPage = isNotLoadingAndIsNotLastPage,
                            isAtLasItem = isAtLasItem, isNotAtBegining = isNotAtBegining,
                            isTotalMoreThanVisible = isTotalMoreThanVisible,
                            lastVisibleItemPosition = lastVisibleItemPosition
                        )

                    } else {
                        val lastRecord = reservationAdapter.getItems().last()
                        var start = 0
                        val referenceRecord =
                            viewModel.listReservationResponse.value?.find { it.ReservationNumber == lastRecord.reservationNumber }
                        if (referenceRecord != null) {
                            start =
                                viewModel.listReservationResponse.value?.indexOf(referenceRecord)!!
                            start++
                            start += viewModel.searchMore.value!!

                        }
                        val newList =
                            viewModel.listReservationResponse.value?.subList(start, start + 3)
                        if (newList != null) {
                            if (lastRecord.reservationNumber != newList.first().ReservationNumber) {
                                if (viewModel.doesDataFinished.value == true) {
                                    viewModel.doesDataFinished.value = false
                                    viewModel.reservationUseCase.recursiveGetReservationDetail(
                                        0, newList,
                                        mutableListOf()
                                    ) {
                                        if (it.data?.isNotEmpty() == true) {
                                            viewModel.listReservationDetail.postValue(it.data)
                                        } else {
                                            val listDummy =
                                                mutableListOf<ReservationDetailResponse>()
                                            listDummy.add(
                                                ReservationDetailResponse(
                                                    ReservationNumber = "NA",
                                                    SaleDate = "NA",
                                                    SaleId = 0,
                                                    Amount = 0.0f
                                                )
                                            )
                                            viewModel.listReservationDetail.postValue(listDummy)
                                        }

                                    }
                                }
                                isScrolling = false
                            }
                        }

                        viewModel.lastItemInList.value =
                            lastVisibleItemPosition == viewModel.listReservationResponse.value?.size!! - 1
                        //viewModel.lastItemInList.value =  lastVisibleItemPosition ==  10
                        viewModel.showFaqs.value = viewModel.lastItemInList.value
                    }

                }


            }catch (exc:Exception){
                Log.e("Onscrolled",exc.toString())
            }
        }
    }

    fun isTotalMoreThanVisible(totalItemCount:Int):Boolean{
        return if(viewModel.statusView.value == ReservationAction.ERROR_ENDPONT){
            totalItemCount >= Constants.QUERY_SIZE_PAGE
        }else{
            totalItemCount >= viewModel.sizeCaptured.value!!
        }
    }

    fun DbModePagination(isNotLoadingAndIsNotLastPage:Boolean,isAtLasItem:Boolean,
                         isNotAtBegining:Boolean,isTotalMoreThanVisible:Boolean,lastVisibleItemPosition:Int){
        //val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_SIZE_PAGE
        viewModel.shouldPaginate.value = isNotLoadingAndIsNotLastPage && isAtLasItem && isNotAtBegining &&
                isTotalMoreThanVisible && isScrolling
        if (viewModel.shouldPaginate.value == true){
            viewModel.to.value = reservationAdapter.getItems().last().idReservationNumber
            viewModel.from.value = viewModel.to.value!! - Constants.QUERY_SIZE_PAGE
            viewModel.getReservationsInRange()
            isScrolling = false
        }
        viewModel.lastItemInList.value =  reservationAdapter.getItem(lastVisibleItemPosition)?.idReservationNumber == 1

        viewModel.showFaqs.value = viewModel.limitReached.value == true && viewModel.lastItemInList.value == true

    }
    //endregion

    private fun fetchRemoteReservation(){
        try {
            viewModel.statusView.value = ReservationAction.LOADING
            viewModel.fetchReservationByEmail(/*"accept@fraudtest.com"*/ viewModel.userLiveData.value?.email
                ?: ""
            ) {
                if (it.success) {
                    it.data?.let { data ->
                        if (data.isEmpty()) runOnUiThread {
                            viewModel.statusView.value = ReservationAction.ERROR_EMPTY
                        }
                        else {
                            viewModel.updateReservationsInFirebase(it.data) { ok ->
                                if(viewModel.forceClose.value == false) {
                                    runOnUiThread {
                                        try {
                                            if (ok) viewModel.statusView.value =
                                                ReservationAction.SUCCESS
                                            else viewModel.statusView.value =
                                                ReservationAction.ERROR_ENDPONT
                                        } catch (ex: Exception) {

                                        }
                                    }
                                }
                            }
                        }
                    } ?: kotlin.run {
                        runOnUiThread {
                            viewModel.statusView.value = ReservationAction.ERROR_EMPTY
                        }
                    }
                } else runOnUiThread {
                    viewModel.statusView.value = ReservationAction.ERROR_ENDPONT
                }
            }
        }catch (exc:Exception){

        }
    }

    fun showAlert(lblmessage:String,isCallRequired:Boolean,
        callListener: DialogAlertCancel.onClickListener,
        closeListener:DialogAlertCancel.onClickListener){

        runOnUiThread {
            try {
                val dialogAlert = DialogAlertCancel.newInstance()
                    .setDaysBeforeCancel(getString(R.string.rkey_lbl_politic_days))
                    .setH4(getString(R.string.rkey_lbl_message_politic))
                    .setDinamicTitle(getString(R.string.rkey_lbl_cancellation_policies))
                    .setDinamicTextButtonCall(getString(R.string.rkey_lbl_call_to_cancel))
                    .setH2(lblmessage)
                    .setCallRequired(isCallRequired)
                    .setOnCloseListener(closeListener)
                    .setOnCallListener(callListener)

                dialogAlert.showSecure(fragmentManager = parentFragmentManager, DialogAlertCancel.TAG)

            }catch (e: Exception){
                Log.e(DialogAlert.TAG, e.toString())
            }
        }
    }

}