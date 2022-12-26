package com.xcaret.xcaret_hotel.view.booking.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.PickupFragmentBinding
import com.xcaret.xcaret_hotel.domain.Airline
import com.xcaret.xcaret_hotel.domain.AirlineTerminal
import com.xcaret.xcaret_hotel.domain.PaymentPickupError
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.booking.vm.PickupViewModel
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.autofittextview.AutofitHelper
import kotlinx.android.synthetic.main.buyer_data_fragment.*
import kotlinx.android.synthetic.main.pickup_fragment.*
import kotlinx.android.synthetic.main.pickup_fragment.btnNext
import kotlinx.android.synthetic.main.pickup_fragment.toolbar
import kotlinx.android.synthetic.main.toolbar_generic.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import java.util.*

class PickupFragment: BaseFragmentDataBinding<PickupViewModel, PickupFragmentBinding>(){
    override fun getLayout(): Int = R.layout.pickup_fragment
    override fun getViewModelClass(): Class<PickupViewModel> = PickupViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    override fun setupUI() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents(){
        toolbar.txtToolbarTitle.text = getString(R.string.lbl_transportation_title)
        toolbar.txtToolbarTitle.setKey(getString(R.string.rkey_lbl_transportation_title))
        Utils.fixHeightTopView(toolbar)
        setObservers()
        setListeners()
    }

    private fun setObservers(){
        viewModel.getTerminalLabel().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            viewModel.lblTerminal.value = it?.value ?: ""
        })
        viewModel.findNotTransportationCaption().observe(viewLifecycleOwner) {
            binding.lblNotTransportationCaption.text =
                it?.value?.replace("@", _parentActivity?.currentHotel?.name ?: "")
        }
    }

    private fun showError(valid: PaymentPickupError){
        etTerminal.error = if(valid.terminal != 0) etTerminal.error else null
        etNumber.error = if(valid.flightNumber != 0) etNumber.error else null
        etHour.error = if(valid.hour != 0) etHour.error else null
        etAirline.error = if(valid.airline != 0) etAirline.error else null
    }

    private fun setListeners(){
        btnNext.onClick {

            _parentActivity?._viewModel?.bookingData?.value?.bookingAttempt =null

            if(viewModel.isCompleteInfo() || viewModel.skipFormShuttle.value == true) {
                try {
                    _parentActivity?._viewModel?.bookingData?.value?.arrival = viewModel.arrivalInfo.value
                    _parentActivity?._viewModel?.bookingData?.value?.departure = viewModel.departureInfo.value
                    _parentActivity?._viewModel?.bookingData?.value?.arrival?.toJson()
                    _parentActivity?._viewModel?.bookingData?.value?.departure?.isDeparture = true
                    _parentActivity?._viewModel?.bookingData?.value?.departure?.toJson()
                    findNavController().navigate(R.id.action_pickupFragment_to_paymentFragment)

                } catch (exc: Exception) {

                }
            }
            else {
                val arrivalValid = viewModel.getError(viewModel.arrivalInfo.value)
                val departureValid = viewModel.getError(viewModel.departureInfo.value)

                if(arrivalValid.hasError) {
                    showError(arrivalValid)
                    activeArrival()
                }else if(departureValid.hasError){
                    showError(departureValid)
                    activeArrival()
                }else {
                    updateInfo()
                    _parentActivity?._viewModel?.bookingData?.value?.arrival = viewModel.arrivalInfo.value
                    _parentActivity?._viewModel?.bookingData?.value?.departure = viewModel.departureInfo.value

                    _parentActivity?._viewModel?.bookingData?.value?.arrival?.toJson()
                    _parentActivity?._viewModel?.bookingData?.value?.departure?.isDeparture = true
                    _parentActivity?._viewModel?.bookingData?.value?.departure?.toJson()

                    findNavController().navigate(R.id.action_pickupFragment_to_paymentFragment)
                }
            }
        }

        btnArrivalInactive.onClick {
            activeArrival()
        }

        btnDepartureInactive.onClick {
            activeDeparture()
        }

        etAirline.onClick {
            showAirlinesDropDown()
        }

        etTerminal.onClick {
            showAirlineTerminalDropDown()
        }

        etHour.onClick {
            getHour()
        }

        swNotTransportation.onCheckedChange { _, isChecked ->
            viewModel.skipFormShuttle.value = isChecked
            if(isChecked) lblNotTransportationCaption.makeVisible()
            else lblNotTransportationCaption.makeGone()
        }

        swNoFlight.setOnCheckedChangeListener { _, isChecked ->
            viewModel.skipFormShuttle.value = isChecked
            if(isChecked) lblNoFlightCaption.makeVisible()
            else lblNoFlightCaption.makeGone()
        }
        toolbar.btnToolbarBack.onClick {
            _parentActivity?.onBackPressed()
        }

    }

    private fun activeArrival(){
        if(viewModel.arrivalActive.value == false) {
            updateInfo()
            viewModel.arrivalActive.value = true
            viewModel.currentInfo.value = viewModel.arrivalInfo.value
        }
    }

    private fun activeDeparture(){
        if(viewModel.arrivalActive.value == true) {
            updateInfo()
            viewModel.arrivalActive.value = false
            viewModel.currentInfo.value = viewModel.departureInfo.value
        }
    }

    private fun showAirlinesDropDown(){
        val dialog = DropDownAirlinesDialog.newInstance(viewModel.currentInfo.value?.airline)
        dialog.addListener(object: DropDownListener<Airline>{
            override fun onSelectedItem(item: Airline) {
                dialog.dismiss()
                viewModel.currentAirline.value = item
                updateInfo()
            }
        })
        dialog.showSecure(childFragmentManager, DropDownAirlinesDialog.TAG)
    }

    private fun showAirlineTerminalDropDown(){
        val dialog = DropDownAirlineTerminalDialog.newInstance(viewModel.currentInfo.value?.terminal)
        dialog.addListener(object: DropDownListener<AirlineTerminal>{
            override fun onSelectedItem(item: AirlineTerminal) {
                dialog.dismissSecure()
                item.value = "${viewModel.lblTerminal.value} ${item.number}"
                viewModel.currentAirlineTerminal.value = item
                updateInfo()
            }
        })
        dialog.showSecure(childFragmentManager, DropDownAirlineTerminalDialog.TAG)
    }

    private fun updateInfo(){
        viewModel.currentInfo.value?.airline = viewModel.currentAirline.value
        viewModel.currentInfo.value?.terminal = viewModel.currentAirlineTerminal.value
        viewModel.currentInfo.value?.hour = etHour.text.toString()
        viewModel.currentInfo.value?.flightNumber = etNumber.text.toString().toIntOrNull()
        if(viewModel.arrivalActive.value == true) {
            viewModel.arrivalInfo.value = viewModel.currentInfo.value
        }
        else {
            viewModel.departureInfo.value = viewModel.currentInfo.value
        }
        viewModel.currentInfo.value = viewModel.currentInfo.value
    }

    private fun getHour(){
        val date = DateUtil.getCalendar()
        val currentHour = etHour.text
        var hour = date.get(Calendar.HOUR_OF_DAY)
        var minute = date.get(Calendar.MINUTE)
        if(!currentHour.isNullOrEmpty()){
            val splitHour = currentHour.split(":")
            hour = splitHour[0].toIntOrNull() ?: hour
            minute = splitHour[1].toIntOrNull() ?: minute
        }

        val timePickerDialog = TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val h = if(hourOfDay.toString().length == 1) "0$hourOfDay" else hourOfDay
            val m = if(minute.toString().length == 1) "0$minute" else minute
            etHour.setText("$h:$m")
            updateInfo()
        }, hour, minute, true)

        timePickerDialog.show()
    }

}