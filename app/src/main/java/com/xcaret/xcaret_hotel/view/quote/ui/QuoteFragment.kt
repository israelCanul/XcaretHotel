package com.xcaret.xcaret_hotel.view.quote.ui

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.QuoteFragmentBinding
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticType
import com.xcaret.xcaret_hotel.view.config.analytics.addToCart
import com.xcaret.xcaret_hotel.view.general.ui.DialogAlert
import com.xcaret.xcaret_hotel.view.general.ui.DialogAlertCancel
import com.xcaret.xcaret_hotel.view.general.ui.LoadingDialogFragment
import com.xcaret.xcaret_hotel.view.general.ui.MainActivity
import com.xcaret.xcaret_hotel.view.quote.vm.QuoteViewModel
import kotlinx.android.synthetic.main.quote_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.uiThread
import java.lang.Exception
import kotlin.math.abs

class QuoteFragment: BaseFragmentDataBinding<QuoteViewModel, QuoteFragmentBinding>(){
    override fun getLayout(): Int = R.layout.quote_fragment
    override fun getViewModelClass(): Class<QuoteViewModel> = QuoteViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val suiteTabAdapter = GenericAdapter<SuiteQuotes>(R.layout.item_tab_suite_number)
    private val roomsAdapter = GenericAdapter<RoomType>(R.layout.item_suite_available)
    private val priceRoomAdapter = GenericAdapter<RoomType>(R.layout.item_room_selected_price)
    private var showCalendar = false
    private var preSelectRoom = ""

    private val loadingDialog: LoadingDialogFragment by lazy {
        LoadingDialogFragment.newInstance()
    }

    override fun setupUI() {
        initComponents()
        showCalendar = arguments?.getBoolean(Constants.SHOW_CALENDAR) ?: false
        preSelectRoom = arguments?.getString(Constants.PRE_SELECT_ROOM) ?: ""
        viewModel.hotelIdSelected.value = _parentActivity?.currentHotel?.idSynxis?.toIntOrNull() ?: 0
        viewModel.currentHotel.value =_parentActivity?._viewModel?.currentHotelLive?.value

        viewModel.isInfoCleaned.value = false

        _parentActivity?.let { act ->
            viewModel.heightDefaultToolbar.value = act.resources.getDimensionPixelSize(R.dimen.height_toolbar).toFloat()
            viewModel.minHeightTabs.value = act.resources.getDimensionPixelSize(R.dimen.item_height_min_tab_quotes).toFloat()
            viewModel.maxHeightTabs.value = act.resources.getDimensionPixelSize(R.dimen.item_height_tab_quotes).toFloat()
            viewModel.heightHeaderQuotes.value = act.resources.getDimensionPixelSize(R.dimen.height_header_quotes).toFloat() + Utils.getStatusBarHeigth(act)
        }
        setObservers()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if(_parentActivity?._viewModel?.activeNextStep?.value == true){
            _parentActivity?._viewModel?.activeNextStep?.value = false
            redirect()
        }
    }


    private fun showWelcomeAlert() {
        var name = ""

        doAsync {
            viewModel.getUser()?.let { user ->
                name = "${user.firstName} ${user.lastName}"

            }
            try {
                Session.setShowWelcomeAlert(false, requireContext())
                val acct = activity as MainActivity
                val fragMngr = acct.supportFragmentManager
                if (acct != null && fragMngr != null) {
                    _parentActivity?.showAlert(
                        title = getString(R.string.rkey_welcome),
                        extraTitle = name,
                        message = getString(R.string.rkey_welcome_msg),
                        accept = getString(R.string.rkey_btn_accept),
                        acceptListener = object : DialogAlert.onClickListener {
                            override fun onClick(v: View?, dialog: DialogFragment) {
                                dialog.dismiss()
                                //showCompleteAlert()
                            }
                        }
                    )
                }
            } catch (exc: IllegalStateException) {
                Log.e("ShowWelcomeAlert",exc.toString())
            }
            catch (exc: Exception){
                Log.e("ShowWelcomeAlert",exc.toString())
            }

        }
    }


    private fun initComponents(){
        binding.layoutBalanceSheet.makeInVisible()
        Utils.fixHeightTopView(binding.toolbarViewInside)
        Utils.fixHeightTopView(binding.auxTop)
        setListeners()
        binding.rvTabSuites.apply {
            adapter = suiteTabAdapter
        }
        binding.rvSuites.apply {
            adapter = roomsAdapter
        }
        binding.rvPriceRooms.apply {
            setHasFixedSize(true)
            adapter = priceRoomAdapter
        }
    }

    private fun setListeners(){
        binding.txtCurrency.onClick {
            changeCurrency()
        }

        binding.btnBack.onClick {
            _parentActivity?.onBackPressed()
        }

        binding.lblEdit.onClick {
            showDialogCalendar()
        }

        binding.lblRetry.onClick {
            if (viewModel.quotesStatus.value == StatusResponse.EMPTY.value){
                showDialogCalendar()
            }else {
                quotes()
            }
        }

        suiteTabAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                selectTab(position)
            }
        })

        roomsAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                if (view.id == R.id.layoutContent){
                    selectRoomByPosition(position)
                }else if(view.id == R.id.txtPolices){
                    val policies = roomsAdapter.getItem(position)?.ratePlan
                    var policiesDescription = ""
                    if (policies!=null) policiesDescription = policies[0].policiesDescription.toString()
                    showAlert(policiesMessage = policiesDescription,
                        object: DialogAlertCancel.onClickListener{
                            override fun onClick(v: View?, dialog: DialogFragment) {
                                dialog.dismiss()
                            }
                        }
                        ,object : DialogAlertCancel.onClickListener{
                            override fun onClick(v: View?, dialog: DialogFragment) {
                                dialog.dismiss()
                            }
                        }
                    )
                }

            }
        })

        priceRoomAdapter.setOnListItemViewClickListener(object :
            GenericAdapter.OnListItemViewClickListener {
            override fun onClick(view: View, position: Int) {
                var oldPosition = -1

                val itemsFound = priceRoomAdapter.getItems().filter { it.isExpandable }
                itemsFound.forEach { selected ->
                    selected.isExpandable = false
                    oldPosition = priceRoomAdapter.getItems().indexOf(selected)
                    priceRoomAdapter.notifyItemChanged(oldPosition)
                }

                if (oldPosition != position) {
                    priceRoomAdapter.getItems().forEachIndexed { index, roomType ->
                        if (index == position) {
                            roomType.isExpandable = true
                            priceRoomAdapter.notifyItemChanged(index)
                            // rvPriceRooms.validateAndScrollToPosition(position)
                        }
                    }
                }
            }
        })

        binding.btnReserve.onClick {
            if(viewModel.activeLiveCheckStep.value == false) return@onClick
            loadingDialog.show(childFragmentManager, LoadingDialogFragment.TAG)
            if(_parentActivity?._viewModel?.doesRequireReOrderSuites?.value == true){
                navigate(R.id.action_quoteFragment_to_paymentFragment)
                loadingDialog.dismiss()
            }else{
                liveCheck()
            }

            //showNavigationOptions()
        }

        binding.nestedScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->

            LogHX.d("Scroll", "y: $scrollY oldY: $oldScrollY toolbar: ${viewModel.heightDefaultToolbar.value!!}")
            //fix height bottom sheet
            if(binding.scrollPriceNights.visibility == View.VISIBLE) binding.scrollPriceNights.collapse()
            if(scrollY > oldScrollY){
                LogHX.d("======================", " DOWN ======================")

                //anim toolbar
                val toolbarY = binding.headerLayout.y
                val diff = scrollY - oldScrollY
                if(toolbarY > -viewModel.heightDefaultToolbar.value!!) {
                    var newY = (abs(toolbarY) + diff)
                    val alpha = 1-((1/viewModel.heightDefaultToolbar.value!!) * newY)
                    newY = if(-newY > -viewModel.heightDefaultToolbar.value!!) newY else viewModel.heightDefaultToolbar.value!!
                    LogHX.e("locationY", "$newY, oldY: $toolbarY")
                    binding.toolbarViewInside.alpha = alpha
                    binding.headerLayout.y = -newY
                }

                //anim bottom sheet
                if(binding.layoutBalanceSheet.visibility == View.VISIBLE){
                    val posY = binding.layoutBalanceSheet.y
                    if(posY < (viewModel.posYDefaultBottomSheet.value!! + binding.layoutBalanceSheet.height))
                        binding.layoutBalanceSheet.y = posY + diff
                    else binding.layoutBalanceSheet.y = viewModel.posYDefaultBottomSheet.value!! + binding.layoutBalanceSheet.height
                }

                //anim tabs
                Log.e("Tabs", "2")
                hideTabs(false)
                val tabsHeight = binding.rvTabSuites.height.toFloat()
                val minHeightTabs = viewModel.minHeightTabs.value!!.toFloat()
                if(tabsHeight > minHeightTabs){
                    var newHeight = tabsHeight - diff
                    newHeight = if(newHeight > minHeightTabs) newHeight else minHeightTabs
                    binding.rvTabSuites.updateHeight(newHeight.toInt())
                }

            }
            if(scrollY < oldScrollY){
                LogHX.d("======================", " UP ======================")

                val diff = oldScrollY - scrollY

                //anim toolbar
                val toolbarY = binding.headerLayout.y
                if(toolbarY < 0) {
                    var newY = toolbarY + (oldScrollY - scrollY)
                    newY = if(newY > 0) 0f
                    else newY
                    val alpha = (1-(1/viewModel.heightDefaultToolbar.value!!) * abs(newY))
                    LogHX.e("locationY", "$toolbarY, oldY: $newY")
                    if(toolbarY != newY){
                        binding.toolbarViewInside.alpha = alpha
                        binding.headerLayout.y = newY
                    }

                }

                //anim bottom sheet
                if(binding.layoutBalanceSheet.visibility == View.VISIBLE){
                    val posY = binding.layoutBalanceSheet.y
                    if(posY > viewModel.posYDefaultBottomSheet.value!!){
                        val newY = posY - (oldScrollY - scrollY)
                        binding.layoutBalanceSheet.y =
                            if(newY > viewModel.posYDefaultBottomSheet.value!!) newY
                            else viewModel.posYDefaultBottomSheet.value!!
                    }
                }

                //anim tabs
                Log.e("Tabs", "1")
                hideTabs(true)
                val tabsHeight = binding.rvTabSuites.height.toFloat()
                val maxHeightTabs = viewModel.maxHeightTabs.value!!.toFloat()
                if(tabsHeight < maxHeightTabs){
                    var newHeight = tabsHeight + diff
                    newHeight = if(newHeight < maxHeightTabs) newHeight else maxHeightTabs
                    binding.rvTabSuites.updateHeight(newHeight.toInt())
                }
            }
        })

        binding.lblPurchaseDetail.onClick {
            if (binding.scrollPriceNights.visibility == View.VISIBLE) {
                binding.scrollPriceNights.collapse()
                binding.rvPriceRooms.visibility = View.GONE
            } else {
                binding.scrollPriceNights.expand()
                binding.rvPriceRooms.visibility = View.VISIBLE
            }
        }


    }

    fun hideTabs(visible :Boolean){
        viewModel.hideTabs(visible)?.let {
            suiteTabAdapter.addItems(it)
            suiteTabAdapter.notifyDataSetChanged()
        }

    }

    private fun selectRoomByPosition(position: Int){
        val roomOld = roomsAdapter.getItems().find { it.isSelected }
        val currentRoom = roomsAdapter.getItem(position)
        val sq = viewModel.suiteQuoteSelected.value
        if (sq!= null) {
            addToCart(AnalyticType.ADD_TO_CART,
                Booking(suiteQuotes = mutableListOf(sq))
            )
        }


        if(!currentRoom?.codeSynxis.equals(roomOld?.codeSynxis, ignoreCase = true)){
            currentRoom?.isSelected = true
            sq?.suiteCodeSelected = currentRoom?.codeSynxis ?: ""
            sq?.suiteNameSelected = currentRoom?.lang?.title ?: ""
            sq?.ratePlanCode = currentRoom?.getRatePlan(true)?.ratePlanCode ?: ""
            roomOld?.let { ro ->
                val pos = roomsAdapter.getItems().indexOf(ro)
                ro.isSelected = false
                roomsAdapter.notifyItemChanged(pos)
            }
            roomsAdapter.notifyItemChanged(position)
            sq?.let {
                val toUpdate = mutableListOf(it)
                viewModel.bandNotUpdateRecycler.value = true
                doAsync {
                    //viewModel.saveSuiteQuotes(toUpdate)
                    viewModel.updateSingleSuiteQuote(it)
                    uiThread {
                        binding.rvSuites.validateAndScrollToPosition(position)
                    }
                }
            }
        }
    }

    fun updateSuiteQuote(){

        LogHX.e("updateSuiteQuote","Aqui voy")
        if (viewModel.suiteQuotesLiveData.value?.size!! <= 1) {
            val roomToUpdate = roomsAdapter.getItems().find { it.isSelected }
            val position = roomsAdapter.getItems().indexOf(roomToUpdate)

            val sq = viewModel.suiteQuoteSelected.value
            viewModel.bandNotUpdateRecycler.value = true
            sq?.ratePlanCode = roomToUpdate?.getRatePlan(true)?.ratePlanCode ?: ""
            sq?.country = Session.getIsoPaymentCode(HotelXcaretApp.mContext)

            roomsAdapter.notifyItemChanged(position)

            sq?.let {
                val toUpdate = mutableListOf(it)

                doAsync {
                    viewModel.saveSuiteQuotes(toUpdate)
                    uiThread {
                        binding.rvSuites.validateAndScrollToPosition(position)
                    }
                }
            }
        }else{
            viewModel.suiteQuotesLiveData.value!!.forEachIndexed { index, suiteQuote ->
                val newSuiteQuote =roomsAdapter.getItems().find { it.codeSynxis == suiteQuote.suiteCodeSelected }
                suiteQuote.ratePlanCode = newSuiteQuote?.getRatePlan(true)?.ratePlanCode ?: ""
                suiteQuote.channel = Settings.getParam(Constants.channel_id, HotelXcaretApp.mContext).toIntOrNull() ?: 0
                suiteQuote.currency = Language.getCurrency(requireContext())
                suiteQuote.hodelCode= Settings.getHotelSelected(HotelXcaretApp.mContext)
                suiteQuote.country = Session.getIsoPaymentCode(HotelXcaretApp.mContext)

                doAsync { viewModel.updateSingleSuiteQuote(suiteQuote) }
            }
        }
        viewModel.updateCurrency.value = false
    }

    private fun setObservers(){

        _parentActivity?._viewModel?.currentFragment?.observe(this,{
            LogHX.e("currentFragment","cambie valor $it")
            if(it == -1) {
                if(!Session.isVisitor(HotelXcaretApp.mContext) &&
                    _parentActivity?._viewModel?.comeFromFragment?.value?.equals("QuotesFragment") == true
                )
                    if (Session.isShowWelcomeAlert(HotelXcaretApp.mContext))showWelcomeAlert()
            }

        })

        viewModel.getSession().observe(this, Observer {
            viewModel.userLiveData.value = it
        })

        viewModel.showBalanceGeneral.observe(this, Observer {
            try {
                if (it && binding.layoutBalanceSheet.visibility != View.VISIBLE) {
                    binding.auxScroll.updateHeight(layoutBalanceSheet.height)
                    binding.layoutBalanceSheet.upIn {
                        runOnUiThread { viewModel.posYDefaultBottomSheet.value = binding.layoutBalanceSheet.y }
                    }
                }
                if(!it && binding.layoutBalanceSheet.visibility == View.VISIBLE){
                    binding.auxScroll.updateHeight(1)
                    binding.layoutBalanceSheet.makeInVisible()
                }
            }catch (e: Exception){}
        })

        viewModel.findDateByHotelId().observe(this, Observer { dq ->
            if(dq == null){
                if (!Session.isHotelInfoCleaned(HotelXcaretApp.mContext)){
                    showDialogCalendar()
                }else{
                    Session.setHotelInfoCleaned(false,HotelXcaretApp.mContext)
                }
            }

            else {
                viewModel.dateQuotesLiveData.value = dq
            }
        })

        viewModel.suiteQuoteSelected.observe(this, Observer {
            it?.let {
                if(viewModel.bandNotUpdateRecycler.value == true)
                    roomsAdapter.notifyDataSetChanged()
                else
                    quotes()
                viewModel.bandNotUpdateRecycler.value = false
            }
        })

        viewModel.hasValidate.observe(this, Observer {
            if(it){
                suiteTabAdapter.addItems(viewModel.suiteQuotesLiveData.value ?: emptyList())
                viewModel.suiteQuoteSelected.value = viewModel.suiteQuotesLiveData.value?.find { it.isSelected }
                viewModel.suiteQuoteSelected.value?.let { sq ->
                    val index = suiteTabAdapter.getItems().indexOf(sq)
                    binding.rvTabSuites.validateAndScrollToPosition(index)
                }
                calculateBalanceSheet()
            }
        })

        viewModel.findSuiteByHotelId().observe(this, Observer { listSuiteCodes ->
            viewModel.suiteQuotesLiveData.value = listSuiteCodes

            if(viewModel.hasValidate.value != true && listSuiteCodes.size >= 2){
                loadingDialog.show(childFragmentManager, LoadingDialogFragment.TAG)
                validateQuotes()
            }else viewModel.hasValidate.value = true
        })

        viewModel.currentHotel.observe(this, {it->
            binding.txtHotelName.text = it?.name
        })

        viewModel.findCurrency(Language.getCurrency(HotelXcaretApp.mContext)).observe(this,{
            if (it!= null) {
                if (!it.isoPayment.isNullOrEmpty())
                    Session.setIsoPaymentCode(it.isoPayment?:"", HotelXcaretApp.mContext)
                else
                    Session.setIsoPaymentCode("mex", HotelXcaretApp.mContext)

                Session.setCountryCode(it.isoCountry!!, requireContext())
            }
        })

        viewModel.suiteQuotesLiveData.observe(this,{list ->
            val listUdp = list
            if(list.size >= 2) {

            }
        })

        viewModel.error.observe(this,{statusResponse->
            when(statusResponse){
                StatusResponse.EMPTY-> {viewModel.quotesStatus.value = StatusResponse.EMPTY.value
                    binding.lblRetry.setKey(getString(R.string.rkey_lbl_edit))
                }
                StatusResponse.SERVER-> {
                    //viewModel.quotesStatus.value = StatusResponse.SERVER.value
                    binding.lblRetry.setKey(getString(R.string.rkey_lbl_retry))}
                else -> {}
            }
        })
    }

    private fun validateQuotes(position: Int = 0){
        viewModel.suiteQuotesLiveData.value?.let { listSuiteQuotes ->
            if(position < listSuiteQuotes.size){
                val sq = listSuiteQuotes[position]
                if(sq.suiteCodeSelected.isEmpty())
                    validateQuotes(position + 1)
                else {
                    viewModel.quotes(sq) { resQuotes ->
                        if (resQuotes.success) {
                            val aux = resQuotes.result.find { it.roomCode.equals(sq.suiteCodeSelected, ignoreCase = true) }
                            runOnUiThread {
                                aux?.let {
                                    viewModel.addNights(it.priceForNights)
                                }
                                ?: kotlin.run {
                                    sq.suiteCodeSelected = ""
                                    sq.suiteNameSelected = ""
                                    sq.ratePlanCode = ""
                                    sq.promoCode = ""
                                    runOnUiThread { viewModel.listSuiteQuotesToUpdate.value?.add(sq) }
                                }
                            }
                        }
                        validateQuotes(position + 1)
                    }
                }
            }else {
                if(viewModel.listSuiteQuotesToUpdate.value?.size ?: 0 > 0){
                    doAsync {
                        viewModel.saveSuiteQuotes(viewModel.listSuiteQuotesToUpdate.value!!)
                        uiThread {
                            viewModel.hasValidate.value = true
                            try{ loadingDialog.dismiss() }catch (e: Exception){ e.printStackTrace() }
                        }
                    }
                }else runOnUiThread {
                    viewModel.hasValidate.value = true
                    try{ loadingDialog.dismiss() }catch (e: Exception){ e.printStackTrace() }
                }
            }
        }
    }

    private fun calculateBalanceSheet(){
        var total = 0.0
        var currency = ""
        val listDetail = mutableListOf<RoomType>()

        suiteTabAdapter.getItems().filter { it.suiteCodeSelected.isNotEmpty() }.forEach { sq ->
            val rt = roomsAdapter.getItems().find { it.codeSynxis.equals(sq.suiteCodeSelected, ignoreCase = true) }
            if (rt!= null) {
                //  roomsAdapter.getItems().find { it.codeSynxis.equals(sq.suiteCodeSelected, ignoreCase = true) }?.let { rt ->
                rt?.getRatePlan(true)?.let { rp ->
                    total += rp.amount
                    currency = rp.currency
                    sq.ratePlan = rp
                    rp.priceForNights.clear()
                    rp.priceForNights.addAll(
                        viewModel.getNigths(
                            sq.suiteCodeSelected,
                            sq.ratePlanCode,
                            sq.hodelCode
                        )
                    )
                    rt.princeNigthsAdapter =
                        GenericAdapter<QuotesRoomRatePlansNights>(R.layout.item_price_room)
                    rt.princeNigthsAdapter?.addItems(rp.priceForNights)
                }
                listDetail.add(rt.copy())
            }
           // }
        }
        priceRoomAdapter.addItems(listDetail)
        viewModel.activeLiveCheckStep.value = listDetail.size == viewModel.suiteQuotesLiveData.value?.size

        if(total > 0){
            val symbol = "".getSymbolCurrency()
            viewModel.totalLiveData.value = "$symbol ${total.formatCurrency()} $currency"
            viewModel.showBalanceGeneral.value = true
        }else {
            viewModel.totalLiveData.value = ""
            viewModel.showBalanceGeneral.value = false
        }
    }

    private fun setObserverRoomRates(){
        viewModel.findByRoomId().observe(viewLifecycleOwner, Observer { listRatePlans ->
            completeInfoRooms(listRatePlans)
        })
    }

    private fun quotes(){
        viewModel.error.value = StatusResponse.NONE
        viewModel.findByRoomId().removeObservers(this)
        viewModel.suiteQuoteSelected.value?.let { sq ->
            sq.channel = Settings.getParam(Constants.channel_id, HotelXcaretApp.mContext).toIntOrNull() ?: 0
            sq.currency = Language.getCurrency(requireContext())
            sq.hodelCode= Settings.getHotelSelected(HotelXcaretApp.mContext)
            sq.country = Session.getIsoPaymentCode(HotelXcaretApp.mContext)
            //sq.country = ValidateCountry(Language.getCurrency(requireContext()))
            viewModel.quotes(sq){ resQuotes ->
                runOnUiThread { viewModel.responseQuotesLive.value = resQuotes }
                doAsync {
                    //viewModel.updateSuiteQuote()
                    viewModel.deleteRatePlans(sq.id, sq.hodelCode)
                    viewModel.removeNigths(sq.ratePlanCode, sq.suiteCodeSelected, sq.hodelCode)
                    if(resQuotes.result.isNotEmpty()) {
                        viewModel.saveRatePlans(resQuotes.result)
                        resQuotes.result.forEach { rq ->
                            viewModel.addNights(rq.priceForNights)
                        }
                    }
                    uiThread {
                        if(resQuotes.result.isNotEmpty())
                            setObserverRoomRates()
                        viewModel.error.value =
                            if(!resQuotes.success) StatusResponse.SERVER
                            else if(resQuotes.result.isEmpty()) StatusResponse.EMPTY
                            else StatusResponse.SUCCESS
                    }
                }
            }
        }
    }


    private fun changeCurrency(){
        val dialog = CurrencyDialog.newInstance()
        dialog.addCurrencyListener(object: CurrencyDialog.CurrencyListener{
            override fun onSelectCurrency(item: Currency) {
                dialog.dismiss()
                Language.setCurrency(item, requireContext())
                Session.setIsoPaymentCode(item.isoPayment!!,requireContext())
                Session.setCountryCode(item.isoCountry!!,requireContext())
                quotes()
                viewModel.updateCurrency.value =true
            }
        }).show(childFragmentManager, CurrencyDialog.TAG)
    }

    private fun liveCheck(position: Int = 0){
        viewModel.suiteQuotesLiveData.value?.let { listSQ ->
            val sq = listSQ[position]
            viewModel.liveCheck(sq){ res: ResponseLiveCheck ->

                runOnUiThread {
                    when (res.errorCode) {
                        StatusResponse.SERVER -> { //error server
                            loadingDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                "Error, verify your connection and retry",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        StatusResponse.EMPTY -> { //error livecheck
                            loadingDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                "Error, the room has not disponibilty, select other",
                                Toast.LENGTH_LONG
                            ).show()
                            val index = listSQ.indexOf(res.errorSuite)
                            if (index >= 0) selectTab(index)
                        }
                        else -> { //success
                            if(viewModel.suiteQuotesLiveData.value?.size ?: 1 == position + 1) {
                                loadingDialog.dismiss()
                                redirect()
                            }
                            else liveCheck(position+1)
                        }
                    }
                }
            }
        }
    }

    private fun completeInfoRooms(response: List<SuiteRatePlans>){
        viewModel.completeRatePlanInfoToRooms(response){
            runOnUiThread {
                roomsAdapter.addItems(it)
                binding.rvSuites.makeVisible()
                binding.loaderSuites.makeGone()
                calculateBalanceSheet()

                if(preSelectRoom.isNotEmpty()){
                    val roomType = roomsAdapter.getItems().find { it.codeSynxis.equals(preSelectRoom, true) }
                    preSelectRoom = ""
                    roomType?.let { rt ->
                        val pos = roomsAdapter.getItems().indexOf(rt)
                        selectRoomByPosition(pos)
                    }
                }else{
                    viewModel.suiteQuoteSelected.value?.let { sq ->
                        if(sq.suiteCodeSelected.isNotEmpty()){
                            roomsAdapter.getItems().firstOrNull { it.codeSynxis.equals(sq.suiteCodeSelected, ignoreCase = true) }?.let { rt ->
                                val pos = roomsAdapter.getItems().indexOf(rt)
                                if(viewModel.updateCurrency.value == true) updateSuiteQuote()
                                runOnUiThread { binding.rvSuites.validateAndScrollToPosition(pos) }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun selectTab(position: Int){
        val oldSelected = viewModel.suiteQuotesLiveData.value?.find { it.isSelected }
        val currentSelected = viewModel.suiteQuotesLiveData.value?.get(position)

        oldSelected?.let { os ->
            currentSelected?.let { cs ->
                if(os.id != cs.id){
                    viewModel.suiteQuoteSelected.value = null
                    val listToUpdate = mutableListOf<SuiteQuotes>()
                    os.isSelected = false
                    cs.isSelected = true
                    listToUpdate.add(os)
                    listToUpdate.add(cs)
                    doAsync {
                        viewModel.saveSuiteQuotes(listToUpdate)
                    }
                }
            }
        }
    }

    private fun showDialogCalendar(){
        val dialogCalendar = CalendarDialogFragment.newInstance()
        dialogCalendar.setOnCalendarListener(object: CalendarDialogFragment.CalendarListener{
            override fun onFinishDateListener(dateStart: String, dateEnd: String) {
                LogHX.i("dates", "$dateStart - $dateEnd")
                dialogCalendar.dismiss()
                val datesQuotes = DateQuotes(
                    hotelId = viewModel.hotelIdSelected.value ?: 0,
                    dateArrival = dateStart,
                    dateDeparture = dateEnd,
                )
                viewModel.temporalDateQuotesLive.value = datesQuotes
                showDialogVisitors()
            }

            override fun onDismissCalendarDialog() {
                if(viewModel.dateQuotesLiveData.value == null)
                    _parentActivity?.onBackPressed()
            }
        })
        dialogCalendar.show(childFragmentManager, CalendarDialogFragment.TAG)
    }

    private fun showDialogVisitors(){
        val dialogVisitors = VisitorsDialogFragment.newInstance()
        dialogVisitors.setVisitorsListener(object: VisitorsDialogFragment.VisitorListener{
            override fun onSetVisitors(suitesQuotes: List<SuiteQuotes>) {
                dialogVisitors.dismiss()
                doAsync {
                    viewModel.deleteSuiteQuotes()
                    suitesQuotes.forEachIndexed { index, it ->
                        it.hodelCode = viewModel.hotelIdSelected.value ?: 0
                        it.startDate = viewModel.temporalDateQuotesLive.value?.dateArrival ?: ""
                        it.endDate = viewModel.temporalDateQuotesLive.value?.dateDeparture ?: ""
                        it.country = Session.getIsoPaymentCode(HotelXcaretApp.mContext)
                        it.currency = Language.getCurrency(requireContext())
                        it.id_room = index + 1
                        //it.country = ValidateCountry(it.currency)
                    }
                    viewModel.saveSuiteQuotes(suitesQuotes)
                    if(viewModel.dateQuotesLiveData.value?.dateArrival?.equals(viewModel.temporalDateQuotesLive.value?.dateArrival) != true
                        || viewModel.dateQuotesLiveData.value?.dateDeparture?.equals(viewModel.temporalDateQuotesLive.value?.dateDeparture) != true )
                            viewModel.temporalDateQuotesLive.value?.let { viewModel.saveDate(it) }
                }
            }

            override fun onBackPressed() {
                showDialogCalendar()
            }
        })
        dialogVisitors.show(childFragmentManager, VisitorsDialogFragment.TAG)
    }

    private fun redirect(){
        when {
            Session.isVisitor(requireContext()) -> {showAlertVisitor()}
            viewModel.userLiveData.value?.isProfileComplete() == false -> showAlertCompleteProfile()
            else -> findNavController().navigate(R.id.action_quoteFragment_to_buyerDataFragment)
        }
    }

    //region AlertDialogs

    private fun showAlertVisitor(){
        _parentActivity?.showAlert(
            title = getString(R.string.rkey_login_sign_up_title_alert),
            message = getString(R.string.rkey_login_sign_up_message_alert),
            accept = getString(R.string.rkey_lbl_continue),
            cancel = getString(R.string.rkey_btn_cancel),
            cancelListener = object : DialogAlert.onClickListener {
                override fun onClick(v: View?, dialog: DialogFragment) {
                    dialog.dismiss()
                }
            },
            acceptListener = object : DialogAlert.onClickListener {
                override fun onClick(v: View?, dialog: DialogFragment) {
                    Session.setShowWelcomeAlert(true,HotelXcaretApp.mContext)
                    _parentActivity?._viewModel?.comeFromFragment?.value = "QuotesFragment"
                    LogHX.e("OnCLick",Session.isShowWelcomeAlert(HotelXcaretApp.mContext).toString())
                    val args = bundleOf(Constants.IS_VISTOR to true)
                    findNavController().navigate(R.id.action_quoteFragment_to_loginFragment, args)

                    dialog.dismiss()
                }
            }
        )
    }

    private fun showAlertCompleteProfile(){
        _parentActivity?.showAlert(
            title = getString(R.string.rkey_complete_profile_title_alert),
            message = getString(R.string.rkey_complete_profile_message_alert),
            accept = getString(R.string.rkey_lbl_continue),
            cancel = getString(R.string.rkey_btn_cancel),
            cancelListener = object : DialogAlert.onClickListener {
                override fun onClick(v: View?, dialog: DialogFragment) {
                    dialog.dismiss()
                }
            },
            acceptListener = object : DialogAlert.onClickListener {
                override fun onClick(v: View?, dialog: DialogFragment) {
                    val args = bundleOf(Constants.REQUIRED to true)
                    findNavController().navigate(R.id.action_quoteFragment_to_profileFragment, args)
                    dialog.dismiss()
                }
            }
        )
    }
    fun showAlert(policiesMessage:String,
        callListener: DialogAlertCancel.onClickListener,
        closeListener:DialogAlertCancel.onClickListener){

        runOnUiThread {
            try {
                val dialogAlert = DialogAlertCancel.newInstance()
                    .setDaysBeforeCancel(getString(R.string.rkey_lbl_politic_days))
                    .setDinamicTextButtonCall(getString(R.string.rkey_lbl_call_to_cancel))
                    .setH4(getString(R.string.rkey_lbl_message_politic))
                    .setDinamicTitle(getString(R.string.rkey_lbl_cancellation_policies))
                    .setH2(policiesMessage)
                    .setOnCloseListener(closeListener)
                    .setOnCallListener(callListener)

                dialogAlert.showSecure(fragmentManager = parentFragmentManager, DialogAlertCancel.TAG)

            }catch (e: Exception){
                Log.e(DialogAlert.TAG, e.toString())
            }
        }
    }
    //endregion
}