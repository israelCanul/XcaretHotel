package com.xcaret.xcaret_hotel.view.booking.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.DialogDropDownFragmentBinding
import com.xcaret.xcaret_hotel.domain.Airline
import com.xcaret.xcaret_hotel.view.base.BaseDialogBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.menu.vm.DropDownViewModel
import kotlinx.android.synthetic.main.dialog_drop_down_fragment.*

class DropDownAirlinesDialog<T>: BaseDialogBinding<DropDownViewModel, DialogDropDownFragmentBinding>() {
    override fun getLayout(): Int = R.layout.dialog_drop_down_fragment
    override fun getViewModelClass(): Class<DropDownViewModel> = DropDownViewModel::class.java
    override fun bindViewToModel() { binding.itemViewModel = viewModel }
    override fun setupUI(){}

    private val airlineAdapter = GenericAdapter<Airline>(R.layout.item_airlines_spinner)
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
        recyclerGeneric.adapter = airlineAdapter
        setObservers()
        setListeners()
    }

    private fun preSelect(){
        arguments?.getString(Constants.AIRLINE_CODE)?.let { airlineCode ->
            airlineAdapter.getItems().find { it.code.equals(airlineCode) }?.let { selected ->
                selected.is_selected = true
                val indexOf = airlineAdapter.getItems().indexOf(selected)
                if(indexOf > 0) recyclerGeneric.scrollToPosition(indexOf)
            }
        }
    }

    private fun setListeners(){
        txtClose.setOnClickListener {
            dismiss()
        }

        airlineAdapter.setOnListItemViewClickListener(object : GenericAdapter.OnListItemViewClickListener {
            override fun onClick(view: View, position: Int) {
                airlineAdapter.getItems().filter { it.is_selected }.forEach { it.is_selected = false }
                airlineAdapter.getItems()[position].is_selected = true
                airlineAdapter.notifyDataSetChanged()
                listener?.onSelectedItem(airlineAdapter.getItem(position) as T)
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
        viewModel.allAirlines().observe(this, Observer {
            progress.makeGone()
            recyclerGeneric.makeVisible()
            airlineAdapter.addItems(it)
            preSelect()
        })
    }

    companion object{
        const val TAG = "DropDownAirlinesDialog"

        fun newInstance(current: Airline? = null): DropDownAirlinesDialog<Airline> {
            val args = bundleOf(
                Constants.AIRLINE_CODE to current?.code
            )
            val fr = DropDownAirlinesDialog<Airline>()
            fr.arguments = args
            return fr
        }
    }
}