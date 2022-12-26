package com.xcaret.xcaret_hotel.view.booking.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.DialogDropDownFragmentBinding
import com.xcaret.xcaret_hotel.domain.AirlineTerminal
import com.xcaret.xcaret_hotel.view.base.BaseDialogBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.menu.vm.DropDownViewModel
import kotlinx.android.synthetic.main.dialog_drop_down_fragment.*
import kotlinx.android.synthetic.main.dialog_drop_down_fragment.auxNavigation
import kotlinx.android.synthetic.main.dialog_drop_down_fragment.statusBar
import kotlinx.android.synthetic.main.dialog_drop_down_fragment.txtClose

class DropDownAirlineTerminalDialog<T>: BaseDialogBinding<DropDownViewModel, DialogDropDownFragmentBinding>() {
    override fun getLayout(): Int = R.layout.dialog_drop_down_fragment
    override fun getViewModelClass(): Class<DropDownViewModel> = DropDownViewModel::class.java
    override fun bindViewToModel() { binding.itemViewModel = viewModel }
    override fun setupUI(){}

    private val airlineTerminalAdapter = GenericAdapter<AirlineTerminal>(R.layout.item_airline_terminal_spinner)
    private var listener: DropDownListener<T>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents(){
        fullScreen()
        setTransparentStatusBar()
        setTransparentNavigationBar()
        Utils.fixHeightTopView(statusBar)
        Utils.fixHeightBottomView(auxNavigation)
        recyclerGeneric.adapter = airlineTerminalAdapter
        setObservers()
        setListeners()
    }

    private fun preSelect(){
        arguments?.getString(Constants.AIRLINE_TERMINAL_CODE)?.let { airlineCode ->
            airlineTerminalAdapter.getItems().find { it.code.equals(airlineCode) }?.let { selected ->
                selected.isSelected = true
                val indexOf = airlineTerminalAdapter.getItems().indexOf(selected)
                if(indexOf > 0) recyclerGeneric.scrollToPosition(indexOf)
            }
        }
    }

    private fun setListeners(){
        txtClose.setOnClickListener {
            dismiss()
        }

        airlineTerminalAdapter.setOnListItemViewClickListener(object : GenericAdapter.OnListItemViewClickListener {
            override fun onClick(view: View, position: Int) {
                airlineTerminalAdapter.getItems().filter { it.isSelected }.forEach { it.isSelected = false }
                airlineTerminalAdapter.getItems()[position].isSelected = true
                airlineTerminalAdapter.notifyDataSetChanged()
                listener?.onSelectedItem(airlineTerminalAdapter.getItem(position) as T)
            }
        })
    }

    fun addListener(listener: DropDownListener<T>){
        this.listener = listener
    }

    override fun dismiss() {
        this.listener = null
        super.dismiss()
    }

    private fun setObservers(){
        progress.makeVisible()
        recyclerGeneric.makeGone()
        viewModel.allAirlineTerminal().observe(this, Observer {
            progress.makeGone()
            recyclerGeneric.makeVisible()
            airlineTerminalAdapter.addItems(it)
            preSelect()
        })
    }

    companion object{
        const val TAG = "DropDownAirlinesDialog"

        fun newInstance(current: AirlineTerminal? = null): DropDownAirlineTerminalDialog<AirlineTerminal> {
            val args = bundleOf(
                Constants.AIRLINE_TERMINAL_CODE to current?.code
            )
            val fr = DropDownAirlineTerminalDialog<AirlineTerminal>()
            fr.arguments = args
            return fr
        }
    }
}