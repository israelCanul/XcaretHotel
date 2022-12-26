package com.xcaret.xcaret_hotel.view.quote.ui

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.android.material.snackbar.Snackbar
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.ParamSettingUseCase
import com.xcaret.xcaret_hotel.databinding.DialogCalendarFragmentBinding
import com.xcaret.xcaret_hotel.domain.DateQuotes
import com.xcaret.xcaret_hotel.domain.MonthBooking
import com.xcaret.xcaret_hotel.domain.YearBooking
import com.xcaret.xcaret_hotel.view.base.BaseDialogBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.calendarview.model.CalendarDay
import com.xcaret.xcaret_hotel.view.config.calendarview.model.CalendarMonth
import com.xcaret.xcaret_hotel.view.config.calendarview.model.DayOwner
import com.xcaret.xcaret_hotel.view.config.calendarview.ui.DayBinder
import com.xcaret.xcaret_hotel.view.config.calendarview.ui.MonthScrollListener
import com.xcaret.xcaret_hotel.view.config.calendarview.ui.ViewContainer
import com.xcaret.xcaret_hotel.view.config.calendarview.utils.yearMonth
import com.xcaret.xcaret_hotel.view.quote.vm.DateViewModel
import kotlinx.android.synthetic.main.dialog_calendar_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.textColorResource
import org.jetbrains.anko.uiThread
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.abs

import kotlin.Exception
import com.xcaret.xcaret_hotel.domain.LangLabel
import org.jetbrains.anko.support.v4.runOnUiThread


class CalendarDialogFragment: BaseDialogBinding<DateViewModel, DialogCalendarFragmentBinding>() {

    private val today = LocalDate.now()

    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null
    private val currentYear: Int by lazy {
        Calendar.getInstance().get(Calendar.YEAR)
    }
    private val paramSettingUseCase: ParamSettingUseCase by lazy {
        ParamSettingUseCase()
    }

    private val headerDateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    private val finalDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val startBackground: GradientDrawable by lazy {
        ContextCompat.getDrawable(requireContext(), R.drawable.continuous_selected_bg_start) as GradientDrawable
    }

    private val endBackground: GradientDrawable by lazy {
        ContextCompat.getDrawable(requireContext(), R.drawable.continuous_selected_bg_end) as GradientDrawable
    }

    private var calendarListener: CalendarListener? = null

    private val yearAdapter = GenericAdapter<YearBooking>(R.layout.item_year)
    private val monthAdapter = GenericAdapter<MonthBooking>(R.layout.item_month)

    private var currentYearSelected: YearBooking? = null
    private var currentMonthSelected: MonthBooking? = null

    override fun getLayout(): Int = R.layout.dialog_calendar_fragment
    override fun getViewModelClass(): Class<DateViewModel> = DateViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }
    override fun setupUI() {
        viewModel.hotelIdLiveData.value = _parentActivity?.currentHotel?.idSynxis?.toIntOrNull() ?: 0
        viewModel.currentHotel. value = _parentActivity?.currentHotel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setTransitionStyle(R.style.DialogAnimation)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents() {
        //isCancelable = false
        //fullScreen()
        //Utils.fixHeightBottomView(auxNavigation)
        //Utils.fixHeightTopView(toolbarViewInside)
        findDatesByHotel {
            setupRecyclerYear() {
                setupRecyclerMonth()
                setupCalendarView()
            }
            addStyeAnimRecycler()
            setListener()
            setObservers()
        }
    }

    private fun findDatesByHotel(ready: () -> Unit){
        viewModel.allByHotelId().observe(this, androidx.lifecycle.Observer { dateQuotes ->
            viewModel.dateLiveDate.value = dateQuotes
            dateQuotes?.let { dq ->
                viewModel.reservationYearMonth.value = YearMonth.parse(dq.dateArrival, finalDateFormatter)
                viewModel.currentYearMonth.value = YearMonth.now()
                viewModel.arrivalDate.value = dq.dateArrival
                viewModel.departureDate.value = dq.dateDeparture
                startDate = LocalDate.parse(dq.dateArrival, finalDateFormatter)
                endDate = LocalDate.parse(dq.dateDeparture, finalDateFormatter)
                /*calendarView?.scrollToMonth(viewModel.currentYearMonth.value!!)
                calendarView?.notifyCalendarChanged()
                bindSummaryViews()*/
            }
            ready()
        })
    }

    private fun setObservers(){
        viewModel.arrivalDate.observe(this, androidx.lifecycle.Observer {
            updateDates()
        })

        viewModel.departureDate.observe(this, androidx.lifecycle.Observer {
            updateDates()
        })

        viewModel.currentHotel.observe(this,{
            binding.txtHotelName.text= it?.name
        })
    }

    private fun updateDates(){

        val dateQuotes = viewModel.dateLiveDate.value ?: DateQuotes(
            hotelId = viewModel.hotelIdLiveData.value ?: 0
        )
        viewModel.arrivalDate.value?.let { aDate ->
            viewModel.departureDate.value?.let { dDate ->
                dateQuotes.dateArrival = aDate
                dateQuotes.dateDeparture = dDate

                viewModel.dateLiveDate.value = dateQuotes
            }
        }
    }

    private fun setupRecyclerYear(ready: () -> Unit){
        rvYears.adapter = yearAdapter
        val yearsArray = mutableListOf<YearBooking>()
        doAsync {
            LIMIT_YEARS = paramSettingUseCase.findParamSettingByCode(Constants.MAX_YEARS_QUOTES)?.value?.toIntOrNull() ?: LIMIT_YEARS
            for(i in currentYear..(currentYear + LIMIT_YEARS)) yearsArray.add(
                YearBooking(
                    i,
                    currentYear == i
                )
            )
            uiThread {
                yearAdapter.addItems(yearsArray)
                ready()
            }
        }
    }

    private fun setupRecyclerMonth(){
        rvMoths.adapter = monthAdapter
        val currentMonth = YearMonth.now()
        val monthArray = mutableListOf<MonthBooking>()
        val monthsArray = DateFormatSymbols(Locale.getDefault()).months
        yearAdapter.getItems().forEach { year ->
            val startMonth = if(year.year == currentYear) currentMonth.monthValue else 1
            for(i in startMonth..LIMIT_MONTHS)
                monthArray.add(MonthBooking(year = year.year, monthValue = i, monthsArray[i - 1]))
        }
        monthAdapter.addItems(monthArray)
    }

    private fun setListener(){
        btnBack.onClick {
            dismiss()
            calendarListener?.onDismissCalendarDialog()
        }

        monthAdapter.setOnListItemViewClickListener(object :
            GenericAdapter.OnListItemViewClickListener {
            override fun onClick(view: View, position: Int) {
                rvMoths.scrollToPosition(position)
            }
        })

        yearAdapter.setOnListItemViewClickListener(object :
            GenericAdapter.OnListItemViewClickListener {
            override fun onClick(view: View, position: Int) {
                rvYears.scrollToPosition(position)
            }
        })

        rvYears.addOnItemChangedListener { _, position ->
            val year = yearAdapter.getItem(position)
            val month = monthAdapter.getItems().find { it.monthValue == calendarView.findFirstVisibleMonth()?.month }
            year?.let { y ->
                month?.let { m ->
                    if(currentYearSelected?.year != y.year || currentMonthSelected?.monthValue != m.monthValue) {
                        currentYearSelected = year
                        currentMonthSelected = m
                        rvMoths.scrollToPosition(monthAdapter.getItems().indexOf(m))
                        calendarView.scrollToMonth(YearMonth.of(y.year, m.monthValue))
                    }
                }
            }
        }

        rvMoths.addOnItemChangedListener { _, position ->
            val month = monthAdapter.getItem(position)
            val monts = currentMonthSelected
            if (currentMonthSelected != null)
                if (month?.monthValue!= currentMonthSelected?.monthValue){
                        if (startDate!= null && endDate!= null)
                            startDate = null
                            endDate = null
                }

            val year = yearAdapter.getItems().find { it.year == month?.year }
            month?.let { m ->
                year?.let { y ->
                    if(currentYearSelected?.year != y.year || currentMonthSelected?.monthValue != m.monthValue) {
                        currentYearSelected = year
                        currentMonthSelected = m
                        rvYears.scrollToPosition(yearAdapter.getItems().indexOf(y))
                        val yea= YearMonth.of(y.year,m.monthValue)
                        calendarView.scrollToMonth(YearMonth.of(y.year, m.monthValue))
                    }
                    val currentMonth = viewModel.reservationYearMonth.value ?: kotlin.run { YearMonth.now() }
                    if (currentMonth != null && viewModel.firstTime.value == false){
                        calendarView.scrollToMonth(currentMonth)
                        viewModel.firstTime.value = true
                    }
                }
            }
        }

        btnNext.onClick {
            if (startDate!= null && endDate != null) {
                calendarListener?.onFinishDateListener(
                    finalDateFormatter.format(startDate), finalDateFormatter.format(
                        endDate
                    )
                )
            }
        }

        dialog?.setOnKeyListener { dialog, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_BACK){
                dismiss()
                calendarListener?.onDismissCalendarDialog()
            }
            true
        }

    }

    private fun setupCalendarView(){
        // Set the First day of week depending on Locale
        val daysOfWeek = daysOfWeekFromLocale()
        (dayLegends as ViewGroup).children.forEachIndexed { index, view ->
            (view as TextView).apply {
                text = (daysOfWeek[index].getDisplayName(TextStyle.NARROW, Locale.getDefault())).toUpperCase()
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                //textColorResource = R.color.colorQuote0
                //alpha = 0.5f
            }
        }

        val currentMonth = viewModel.currentYearMonth.value ?: kotlin.run { YearMonth.now() }
        calendarView.setup(
            currentMonth,
            currentMonth.plusMonths(monthAdapter.getItems().size.toLong()),
            daysOfWeek.first()
        )

        calendarView.scrollToMonth(currentMonth)


        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val exFourDayText = view.findViewById<TextView>(R.id.exFourDayText)
            val exFourRoundBgView = view.findViewById<View>(R.id.exFourRoundBgView)

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH && (day.date == today || day.date.isAfter(today))) {
                        val date = day.date
                        if (startDate != null) {
                            if (date < startDate || endDate != null) {
                                startDate = date
                                endDate = null
                            } else if (date != startDate) {
                                val minNight = viewModel.currentHotel.value?.minNight ?: 40
                                val maxNight = viewModel.currentHotel.value?.maxNight ?:40
                                if( minNight <= CalculateNights(date).toInt()){
                                    if( maxNight >= CalculateNights(date).toInt()){
                                        endDate = date
                                    }else{
                                        showWarning(getString(R.string.rkey_lbl_max_nights),maxNight.toString())
                                        //showSnackBar_("Selecciona maximo ${viewModel.currentHotel.value?.maxNight!!} noche(s)")
                                    }

                                }else{
                                    showWarning(getString(R.string.rkey_lbl_minimum_nights),minNight.toString())
                                    //showSnackBar_("Selecciona al menos ${viewModel.currentHotel.value?.minNight!!} noche(s)")
                                }
                            }
                        } else {
                            startDate = date
                        }
                        calendarView.notifyCalendarChanged()
                        bindSummaryViews()
                    }
                }
            }

            private fun CalculateNights(endDate: LocalDate):Long {
                var dayDifference:Long = 0
                try {
                    dayDifference = ChronoUnit.DAYS.between(startDate,endDate)

                }catch (exc:Exception){

                }
                return dayDifference
            }

        }

        calendarView.monthScrollListener = object: MonthScrollListener{
            override fun invoke(p1: CalendarMonth) {
                val year = yearAdapter.getItems().firstOrNull { it.year == p1.year }
                val month = monthAdapter.getItems()
                    .firstOrNull { it.year == p1.year && it.monthValue == p1.month }

                year?.let { rvYears.scrollToPosition(yearAdapter.getItems().indexOf(it)) }
                month?.let { rvMoths.scrollToPosition(monthAdapter.getItems().indexOf(it)) }
            }
        }

        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.exFourDayText
                val roundBgView = container.exFourRoundBgView

                textView.text = null
                textView.background = null
                roundBgView.makeInVisible()

                val startDate = startDate
                val endDate = endDate

                when (day.owner) {
                    DayOwner.THIS_MONTH -> {
                        textView.text = day.day.toString()
                        if (day.date.isBefore(today)) {
                            textView.textColorResource = R.color.colorCalendarTextDisabled
                            textView.alpha = 0.5f
                            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        } else {
                            textView.alpha = 1f
                            textView.paintFlags =
                                textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                            when {
                                startDate == day.date && endDate == null -> {
                                    textView.setTextColor(Color.WHITE)
                                    roundBgView.makeVisible()
                                    roundBgView.setBackgroundResource(R.drawable.single_selected_bg)
                                }
                                day.date == startDate -> {
                                    textView.setTextColor(Color.WHITE)
                                    textView.background = startBackground
                                }
                                startDate != null && endDate != null && (day.date > startDate && day.date < endDate) -> {
                                    textView.textColorResource = android.R.color.white
                                    textView.setBackgroundResource(R.drawable.continuous_selected_bg_middle)
                                }
                                day.date == endDate -> {
                                    textView.setTextColor(Color.WHITE)
                                    textView.background = endBackground
                                }
                                day.date == today -> {
                                    textView.textColorResource = android.R.color.white
                                    roundBgView.makeVisible()
                                    roundBgView.setBackgroundResource(R.drawable.today_bg)
                                }
                                else -> textView.textColorResource = R.color.colorCalendarTextDay
                            }
                        }
                    }
                    // Make the coloured selection background continuous on the invisible in and out dates across various months.
                    DayOwner.PREVIOUS_MONTH ->
                        if (startDate != null && endDate != null && isInDateBetween(
                                day.date,
                                startDate,
                                endDate
                            )
                        ) {
                            textView.setBackgroundResource(R.drawable.continuous_selected_bg_middle)
                        }else if (day.date.isBefore(today)) {
                            textView.textColorResource = R.color.colorCalendarTextDisabled
                            textView.alpha = 0.5f
                            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        }else{
                            textView.paintFlags =
                                textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                            textView.textColorResource = R.color.colorCalendarTextDisabled
                            textView.alpha = 0.5f
                        }
                    DayOwner.NEXT_MONTH ->
                        if (startDate != null && endDate != null && isOutDateBetween(
                                day.date,
                                startDate,
                                endDate
                            )
                        ) {
                            textView.setBackgroundResource(R.drawable.continuous_selected_bg_middle)
                        }else if (day.date.isBefore(today)) {
                            textView.textColorResource = R.color.colorCalendarTextDisabled
                            textView.alpha = 0.5f
                            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        }else{
                            textView.paintFlags =
                                textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                            textView.textColorResource = R.color.colorCalendarTextDisabled
                            textView.alpha = 0.5f
                        }
                }
            }
        }

    }

    private fun isInDateBetween(inDate: LocalDate, startDate: LocalDate, endDate: LocalDate): Boolean {
        if (startDate.yearMonth == endDate.yearMonth) return false
        if (inDate.yearMonth == startDate.yearMonth) return true
        if (inDate.yearMonth.plusMonths(1) == endDate.yearMonth) return true
        return inDate > startDate && inDate < endDate
    }

    private fun isOutDateBetween(outDate: LocalDate, startDate: LocalDate, endDate: LocalDate): Boolean {
        if (startDate.yearMonth == endDate.yearMonth) return false
        if (outDate.yearMonth == endDate.yearMonth) return true
        if (outDate.yearMonth.minusMonths(1) == startDate.yearMonth) return true
        return outDate > startDate && outDate < endDate
    }

    private fun bindSummaryViews() {
        if(startDate != null){
            viewModel.arrivalDate.value = finalDateFormatter.format(startDate)
            viewModel.departureDate.value = null
        }

        if(endDate != null){
            viewModel.departureDate.value = finalDateFormatter.format(endDate)
            viewModel
        }

        // Enable save button if a range is selected or no date is selected at all, Airbnb style.
        btnNext.isEnabled = endDate != null || (startDate == null && endDate == null)
    }

    fun setOnCalendarListener(calendarListener: CalendarListener){
        this.calendarListener = calendarListener
    }

    interface CalendarListener{
        fun onFinishDateListener(dateStart: String, dateEnd: String)
        fun onDismissCalendarDialog()
    }

    private fun addStyeAnimRecycler(){
        rvYears.setItemTransformer { item, position ->
            val closenessToCenter = 1f - abs(position)
            item?.let { view ->
                val textView = view.findViewById<TextView>(R.id.txtTitle)
                if(closenessToCenter < 1.0f){
                    textView.alpha = 0.5f
                    textView.setTypeface(textView.typeface, Typeface.NORMAL)
                    textView.textColorResource = R.color.colorCalendarTextDisabled
                }else {
                    textView.textColorResource = R.color.colorCalendarTextTitle
                    textView.setTypeface(textView.typeface, Typeface.BOLD)
                    textView.alpha = 1f
                }
            }
        }

        rvMoths.setItemTransformer { item, position ->
            val closenessToCenter = 1f - abs(position)
            val scale: Float = 0.8f + 0.2f * closenessToCenter
            item?.let { view ->
                item.scaleX = scale
                item.scaleY = scale
                val textView = view.findViewById<TextView>(R.id.txtTitle)
                if(closenessToCenter < 1.0f){
                    textView.alpha = 0.5f
                    textView.setTypeface(textView.typeface, Typeface.NORMAL)
                    textView.textColorResource = R.color.colorCalendarTextDisabled
                }else {
                    textView.textColorResource = R.color.colorCalendarTextTitle
                    textView.setTypeface(textView.typeface, Typeface.BOLD)
                    textView.alpha = 1f
                }
            }
        }
    }

    fun findLabel(key: String, result: (res: LangLabel?) -> Unit){
        doAsync {
            val label = viewModel.labelUseCase.findLabelOutLive(key)
            uiThread {
                result(label)
            }
        }
    }
    fun showWarning(key: String, newValue :String){
        findLabel(key){label ->
            label?.let {
                runOnUiThread {
                    val str = label.value?.replace("@",newValue)
                    val snack = showSnackBar(str)
                    snack?.addCallback(object : Snackbar.Callback(){
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            if(!isNightEnabled()){
                                setColorStatusBar(R.color.colorBackgroundTop)
                            }else{
                                setColorStatusBar(R.color.colorSnackStatusBar)
                            }
                        }

                        override fun onShown(sb: Snackbar?) {
                            super.onShown(sb)
                            if(!isNightEnabled()){
                                setColorStatusBar(R.color.color9_Snack)
                            }else{
                                setColorStatusBar(R.color.colorSnackNight)
                            }
                        }
                    })
                    snack?.show()
                }
            }
        }

    }

    companion object{
        var LIMIT_YEARS = 3
        const val LIMIT_MONTHS = 12
        const val TAG = "CalendarDialogFragment"
        private const val TRANSITION = "TRANSITION"

        fun newInstance(): CalendarDialogFragment{
            return CalendarDialogFragment()
        }
    }

}