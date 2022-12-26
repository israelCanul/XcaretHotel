package com.xcaret.xcaret_hotel.view.quote.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.data.usecase.ParamSettingUseCase
import com.xcaret.xcaret_hotel.databinding.DialogVisitorsFragmentBinding
import com.xcaret.xcaret_hotel.domain.PaxRules
import com.xcaret.xcaret_hotel.domain.SuiteQuotes
import com.xcaret.xcaret_hotel.view.base.BaseDialogBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.quote.vm.RoomGuestViewModel
import kotlinx.android.synthetic.main.dialog_visitors_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class VisitorsDialogFragment: BaseDialogBinding<RoomGuestViewModel, DialogVisitorsFragmentBinding>() {
    private val listAgesView = mutableListOf<NumberPickerSecondaryView>()
    private val paramSettingUseCase: ParamSettingUseCase by lazy {
        ParamSettingUseCase()
    }
    private val langLabelUseCase = LangLabelUseCase()

    private var visitorsListener: VisitorListener? = null
    private var adults: Int = 2
    private var children: Int = 0
    private var limitAge = 17
    private var lblChild: String = ""
    private var INFERIORAGE: Int? = 0

    private val suiteQuotesAdapter = GenericAdapter<SuiteQuotes>(R.layout.item_tab_suite_number_v2)

    override fun getLayout(): Int = R.layout.dialog_visitors_fragment
    override fun getViewModelClass(): Class<RoomGuestViewModel> = RoomGuestViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }
    override fun setupUI() {
        viewModel.hotelIdLive.value = _parentActivity?.currentHotel?.idSynxis?.toIntOrNull() ?: 0
        viewModel.currentHotel.value = _parentActivity?.currentHotel
        getRemoteChildLabel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents() {
        //fullScreen()
        viewModel.maxTabs.value = Settings.getParam(Constants.max_room_quotes, HotelXcaretApp.mContext).toIntOrNull() ?: 5
        isCancelable = false
        rvSuites.adapter = suiteQuotesAdapter
        setObservers()
        //Utils.fixHeightBottomView(auxNavigation)
        //Utils.fixHeightTopView(toolbarViewInside)
        setListeners()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    private fun getRemoteChildLabel(){
        doAsync {
            langLabelUseCase.findLabelOutLive(getString(R.string.rkey_lbl_child))?.let { label ->
                lblChild = label.value ?: getString(R.string.lbl_child)
            }
        }
    }

    private fun setObservers(){

        viewModel.allByHotelId().observe(this, Observer { listTabSuite ->
            if(listTabSuite.isNotEmpty()) {
                suiteQuotesAdapter.addItems(listTabSuite)
                viewModel.sizeTabs.value = listTabSuite.size
                viewModel.currentTabSelected.value = listTabSuite.firstOrNull { it.isSelected }
            }else addTab()
        })

        viewModel.currentTabSelected.observe(this, Observer { suiteQuotes ->
            suiteQuotes?.let {
                selectTabSuite(suiteQuotesAdapter.getItems().indexOf(it))
                adults = it.adults
                children = it.children
                updateCurrentTab()
                updateUI()
            }
        })

        viewModel.currentHotel.observe(this,{ it->
            binding.txtHotelName.text = it?.name
            INFERIORAGE = it?.minimumAgeChildren?.toInt()


        })
    }

    private fun addTab(){
        contentAges.removeAllViews()
        adults = DEFAULT_ADULTS
        children = DEFAULT_CHILDS
        val newSuite = SuiteQuotes(
            number = suiteQuotesAdapter.getItems().size+1,
            adults = adults,
            children = children
        )
        listAgesView.clear()
        suiteQuotesAdapter.getItems().add(newSuite)
        viewModel.sizeTabs.value = suiteQuotesAdapter.getItems().size
        viewModel.currentTabSelected.value = newSuite
    }

    fun setVisitorsListener(visitorListener: VisitorListener){
        this.visitorsListener = visitorListener
    }

    private fun updateUI(){
        doAsync {
            val paramSetting = paramSettingUseCase.findParamSettingByCode(Constants.MAX_AGE_FOR_CHILD)
            paramSetting?.let { ps ->
                limitAge = ps.value?.toIntOrNull() ?: limitAge
            }
            val ages = viewModel.currentTabSelected.value?.ageString?.split(",") ?: emptyList()
            uiThread {
                updateTotals()
                contentAges.removeAllViews()
                listAgesView.clear()
                viewModel.currentTabSelected.value?.let { sq ->
                    adults = sq.adults
                    children = sq.children
                    npvAdults.setDefaultNumber(sq.adults, false)
                    npvChildren.setDefaultNumber(sq.children, false)
                    applyRules()
                    if(children > 0)
                        addChildren(ages)
                }
            }
        }
    }

    private fun selectTabSuite(position: Int){
        suiteQuotesAdapter.getItems().filter { it.isSelected }.forEach { it.isSelected = false }
        suiteQuotesAdapter.getItem(position)?.isSelected = true
        suiteQuotesAdapter.notifyDataSetChanged()
        rvSuites.scrollToPosition(position)
        LogHX.i("test", "hola")
    }

    private fun setListeners(){
        btnBack.onClick {
            dismiss()
            visitorsListener?.onBackPressed()
        }

        btnNext.onClick {
            visitorsListener?.onSetVisitors(suiteQuotesAdapter.getItems())
        }

        suiteQuotesAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                when(view.id){
                    R.id.unSelectedLayout -> {
                        viewModel.currentTabSelected.value = suiteQuotesAdapter.getItem(position)
                    }
                    else -> { //remove
                        suiteQuotesAdapter.getItems().removeAt(position)
                        suiteQuotesAdapter.getItems().forEachIndexed { index, suiteQuotes -> suiteQuotes.number = index+1 }
                        viewModel.currentTabSelected.value =  suiteQuotesAdapter.getItem(position-1)
                    }
                }
            }
        })

        btnAddSuite.onClick {
            addTab()
        }

        npvChildren.setNumberPickerListener(object: NumberPickerView.NumberPickerListener{
            override fun onChangeNumber(number: Int) {
                when {
                    number == 0 -> {
                        lblAges.makeGone()
                        listAgesView.clear()
                        contentAges.removeAllViews()
                    }
                    number > listAgesView.size -> {
                        lblAges.makeVisible()
                        val numberView = getNumberView()
                        listAgesView.add(numberView)
                        updateAgeChilds()
                        numberView.setLocalText("$lblChild ${listAgesView.size}")
                        contentAges.addView(numberView)
                    }
                    else -> {
                        if(listAgesView.size == 1) lblAges.makeGone()
                        else lblAges.makeVisible()
                        val numberView = listAgesView.last()
                        numberView.setNumberPickerListener(null)
                        contentAges.removeView(numberView)
                        listAgesView.removeLast()
                    }
                }
                children = number
                updateCurrentTab()
                updateTotals()
                applyRules()
            }
        })

        npvAdults.setNumberPickerListener(object: NumberPickerView.NumberPickerListener{
            override fun onChangeNumber(number: Int) {
                adults = number
                updateCurrentTab()
                updateTotals()
                applyRules()
            }
        })

        dialog?.setOnKeyListener { dialog, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_BACK){
                dismiss()
                visitorsListener?.onBackPressed()
            }
            true
        }
    }

    private fun updateCurrentTab(){
        viewModel.currentTabSelected.value?.let { currentTab ->
            currentTab.children = children
            currentTab.adults = adults
            viewModel.currentAdults.value = adults
            viewModel.currentChildren.value = children
        }
    }

    private fun updateTotals(){
        viewModel.totalSuite.value = suiteQuotesAdapter.getItems().size
        var guests = 0
        suiteQuotesAdapter.getItems().forEach {
            guests += it.adults + it.children
        }
        viewModel.totalVisitors.value = guests
    }

    private fun applyRules(){
        val rules = _parentActivity?.currentHotel?.getListPaxRules()
        rules?.let { rul ->
            var ruleAdult: PaxRules? = null
            var ruleChild: PaxRules? = null
            rul.filter { it.adults == adults }.forEach { rule ->
                if(ruleAdult?.children ?: 0 < rule.children)
                    ruleAdult = rule
            }

            rul.filter { it.children == children }.forEach { rule ->
                if(ruleChild?.adults ?: 0 < rule.adults)
                    ruleChild = rule
            }
            ruleAdult?.let { rAdult -> npvChildren.setLimit(rAdult.children) }
            ruleChild?.let { rChild -> npvAdults.setLimit(rChild.adults) }
        }
    }

    private fun addChildren(age: List<String>){
        age.forEach { a ->
            lblAges.makeVisible()
            val numberView = getNumberView(a.toIntOrNull() ?: 1)
            listAgesView.add(numberView)
            numberView.setLocalText("$lblChild ${listAgesView.size}")
            contentAges.addView(numberView)
        }
    }

    private fun updateAgeChilds(){
        viewModel.currentTabSelected.value?.let { sq ->
            val ages = mutableListOf<Int>()
            listAgesView.forEach {
                ages.add(it.getNumber())
            }
            sq.setAges(ages)
            LogHX.i("update ages", "ages")
        }
    }

    private fun getNumberView(defaultNumber: Int = INFERIORAGE!!): NumberPickerSecondaryView {
        val numberView = NumberPickerSecondaryView(requireContext())
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.margin_mini_x)
        numberView.layoutParams = layoutParams
        numberView.setDefaultNumber(defaultNumber)
        numberView.setLimit(limitAge)
        numberView.inferiorLimit(INFERIORAGE!!)
        numberView.setNumberPickerListener(object: NumberPickerSecondaryView.NumberPickerListener{
            override fun onChangeNumber(number: Int) {
                viewModel.currentTabSelected.value?.let { sq ->
                    updateAgeChilds()
                }
            }
        })
        return numberView
    }

    interface VisitorListener{
        fun onSetVisitors(suitesQuotes: List<SuiteQuotes>)
        fun onBackPressed()
    }

    companion object {
        const val TAG = "VisitorsDialogFragment"
        //const val INFERIOR_AGE = 1
        const val DEFAULT_ADULTS = 2
        const val DEFAULT_CHILDS = 0

        fun newInstance(): VisitorsDialogFragment{
            return VisitorsDialogFragment()
        }
    }
}