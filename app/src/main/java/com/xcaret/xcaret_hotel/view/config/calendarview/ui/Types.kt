package com.xcaret.xcaret_hotel.view.config.calendarview.ui

import android.view.View
import com.xcaret.xcaret_hotel.view.config.calendarview.model.CalendarDay
import com.xcaret.xcaret_hotel.view.config.calendarview.model.CalendarMonth

open class ViewContainer(val view: View)

interface DayBinder<T : ViewContainer> {
    fun create(view: View): T
    fun bind(container: T, day: CalendarDay)
}

interface MonthHeaderFooterBinder<T : ViewContainer> {
    fun create(view: View): T
    fun bind(container: T, month: CalendarMonth)
}

typealias MonthScrollListener = (CalendarMonth) -> Unit
