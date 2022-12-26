package com.xcaret.xcaret_hotel.view.category.ui

import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.EventDetailFragmentBinding
import com.xcaret.xcaret_hotel.databinding.WorkshopDetailFragmentBinding
import com.xcaret.xcaret_hotel.domain.Activity
import com.xcaret.xcaret_hotel.domain.DayBooking
import com.xcaret.xcaret_hotel.domain.ScheduleActivity
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.category.vm.WorkShopDetailViewModel
import com.xcaret.xcaret_hotel.view.config.*
import kotlinx.android.synthetic.main.widget_number_picker.*
import java.time.format.TextStyle
import java.util.*

class EventDetailFragment: BaseFragmentDataBinding<WorkShopDetailViewModel, EventDetailFragmentBinding>() {
    override fun getLayout(): Int = R.layout.event_detail_fragment
    override fun getViewModelClass(): Class<WorkShopDetailViewModel> = WorkShopDetailViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val dayFilterAdapter = GenericAdapter<DayBooking>(R.layout.item_day_of_week,0.17f,0.08f)

    override fun setupUI() {
        initComponents()
        setObservers()
        setUpDays()
        setListener()
    }

    private fun initComponents() {
        Utils.heighPercentage(binding.headerLayout, 0.47f)
    }

    private fun setUpDays(){
        try {
            binding.rvDays.adapter = dayFilterAdapter
            val days = daysOfWeekFromLocale()
            val listDays = mutableListOf<DayBooking>()
            days.forEachIndexed { index, dayOfWeek ->
                val day = DayBooking()
                day.dayName =
                    (days[index].getDisplayName(TextStyle.SHORT, Locale.getDefault())).toLowerCase().capitalize()
                day.dayValue = index
                listDays.add(day)

            }
            viewModel.daysOfTheWeek.value =listDays
//                dayFilterAdapter.addItems(listDays)
//            val today = dayFilterAdapter.getItems()[DateUtil.getDayOfTheWeek()-1]
//            today.selected = true
//            viewModel.daySelected.value = today
        } catch (e: Exception) {
            Log.e("setUpDays",e.toString())
        }

    }

    private fun setListener(){
        dayFilterAdapter.setOnListItemViewClickListener( object : GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                try {
                    val selected = dayFilterAdapter.getItems()[position]
                    if(selected.dayValue == viewModel.daySelected.value?.dayValue) return
                    dayFilterAdapter.getItems().forEach { it.selected = false }
                    dayFilterAdapter.getItems()[position].selected = true
                    binding.rvDays.validateAndScrollToPosition(position)
                    dayFilterAdapter.notifyDataSetChanged()
                    viewModel.daySelected.value = selected


                }catch (e:java.lang.Exception){
                    Log.e("setUpDays",e.toString())
                }

            }
        } )
    }



    private fun setObservers(){
        viewModel.activityUID.value = arguments?.getString(Constants.ACT_UID) ?: ""
        viewModel.callFromMap.value = arguments?.getBoolean(Constants.CALLING_FROM_MAP) ?: false

        viewModel.activity.observe(this, Observer {
            setExtraObservers()
        })

        viewModel.findByUID().observe(this, Observer {
            viewModel.activity.value = it

        })

        viewModel.daySelected.observe(this,{day ->
            printSchedule(day)
        })

        viewModel.activityWithSchedules().observe(this, Observer { schedules ->
            viewModel.scheduleActivties.value = schedules
            printSchedule(viewModel.daySelected.value)
            removeDays()

        })
    }

    fun removeDays(){
        var availableDays = mutableListOf<DayBooking?>()
        for (scheduleDay: ScheduleActivity in viewModel.scheduleActivties.value?: emptyList()){
            val isDayFound = Constants.DaysWeek.values().find { it.value == scheduleDay.day?.toInt() }
            val numberDay = isDayFound?.value
            Log.e("RemoveDays" ,"$numberDay  ${isDayFound?.name} ${scheduleDay.day} ")
            if (isDayFound != null ){
                val dayToAdd = viewModel.daysOfTheWeek.value?.find { it.dayValue == numberDay }
                if (dayToAdd != null){
                    if (availableDays.find { it?.dayValue == dayToAdd.dayValue } == null)
                        availableDays.add(dayToAdd)
                }
                Log.e("AddDay" ,"${dayToAdd?.dayName} ${dayToAdd?.dayValue} ")
            }
        }
        if (availableDays.isNotEmpty()) {
            dayFilterAdapter.addItems(emptyList())
            dayFilterAdapter.addItems(availableDays.toList() as List<DayBooking>)
            setCurrentDay()
        }
    }
    private fun setCurrentDay(){
        var today: DayBooking? = null
        today = if (dayFilterAdapter.getItems().size > 7){
            dayFilterAdapter.getItems()[DateUtil.getDayOfTheWeek()-1]
        }else{
            dayFilterAdapter.getItems()[0]
        }
        today.selected = true
        viewModel.daySelected.value = today
    }

    private fun setExtraObservers(){
        viewModel.findCategoryByUID().removeObservers(this)

        viewModel.location().removeObservers(this)

        viewModel.findCategoryByUID().observe(this, Observer {
            viewModel.category.value = it
        })

        viewModel.location().observe(this, Observer {
            viewModel.location.value = it
        })

    }

    fun printSchedule(day:DayBooking?){
        try {
            if (day?.dayValue != 0 ) {
                val newActivitySchedule =
                    viewModel.scheduleActivties.value?.first { it.day == day?.dayValue.toString() }
                newActivitySchedule.let {
                    val newFormatedDateStart = DateUtil.checkDeviceTimeFormat(
                        HotelXcaretApp.mContext,
                        newActivitySchedule?.hourStart
                    )
                    val newFormatedDateEnd = DateUtil.checkDeviceTimeFormat(
                        HotelXcaretApp.mContext,
                        newActivitySchedule?.hourEnd
                    )
                    val s = "$newFormatedDateStart - $newFormatedDateEnd"
                    //val formatSchedule = listGroupSchedule.joinToString(separator = " - ")
                    binding.txtSchedule.text = s
                }
            }else{
                binding.txtSchedule.text = " "
            }
        } catch (e: Exception) {
            Log.e("Observer Dayselected",e.toString())

        }

    }


}