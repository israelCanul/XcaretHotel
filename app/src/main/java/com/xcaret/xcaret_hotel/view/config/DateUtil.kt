package com.xcaret.xcaret_hotel.view.config

import android.content.Context
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

object DateUtil {
    const val DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss"
    const val DATE_TIME_FORMAT_FAV = "dd/MM/yyyy HH:mm"

    const val DATETIME_FORMAT_VISIT = "yyyyMMdd"
    const val DATE_FORMAT_WEATHER = "yyyy-MM-dd"
    const val DAY_WEEK_FORMAT_TEXT = "EEE"
    const val MONT_DAY_FORMAT_TEXT = "MMM dd"
    const val DAY_MONTH_FORMAT_TEXT = "dd MMM"
    const val FORMAT_WEATHER = "dd MMMM"
    const val QUOTES_FORMAT = "MMM dd, yyyy"
    const val QUOTES_FORMAT_LARGE = "EEE dd MMM, yyyy"

    const val YEAR = "yyyy"
    const val YEAR_SHORT = "yy"
    const val MONTH_SHORT = "MM"

    const val ORIGIN_FORMAT_RESERVATION = "yyyy-MM-dd'T'HH:mm:ss"
    const val FORMAT_RESERVATION = "dd MMM yyyy"

    fun getCalendar() : Calendar = Calendar.getInstance()

    fun getDateFormat(format: String): SimpleDateFormat {
        return SimpleDateFormat(format, Locale.getDefault())
    }

    fun getDateByFormat(format: String): String{
        val calendar = getCalendar()
        val dateFormat = getDateFormat(format)
        return dateFormat.format(calendar.time)
    }

    fun convertStringToDate(strDate: String, strFormat: String): Calendar{
        val sdf = getDateFormat(strFormat)
        val date = sdf.parse(strDate)
        val cal = getCalendar()
        cal.time = date
        return cal
    }

    fun changeFormatDate(strDate: String, fromFormat: String, toFormat: String): String{
        val calendar = convertStringToDate(strDate, fromFormat)
        val sdf = getDateFormat(toFormat)
        return sdf.format(calendar.time)
    }

    fun addDaysToCurrentDay(format: String = DATE_FORMAT_WEATHER, numDays: Int): String{
        val calendar = getCalendar()
        if(numDays > 0) calendar.add(Calendar.DATE, numDays)
        val dateFormat = getDateFormat(format)
        return dateFormat.format(calendar.time)
    }

    fun checkDeviceTimeFormat(context:Context,timeToReformat:String?):String?{
        val lTime = LocalTime.parse(timeToReformat)
        val is24hrsFormat = DateFormat.is24HourFormat(context)
        if(!is24hrsFormat) {
            val formatter = DateTimeFormatter.ofPattern("hh:mm a")
            return lTime?.format(formatter)
        }
        return  timeToReformat
    }
    fun getDayOfTheWeek(): Int {
        val calendar = getCalendar()
        return calendar.get(Calendar.DAY_OF_WEEK)
    }


}