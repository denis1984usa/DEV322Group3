package com.hfad.movemore.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

@SuppressLint("SimpleDateFormat")
object TimeUtils {
    val timeFormatter = SimpleDateFormat("HH:mm:ss:SSS")
    val dateFormatter = SimpleDateFormat("MM/dd/yyyy HH:mm")
    fun getTime(timeInMillis: Long): String {
        timeFormatter.timeZone = TimeZone.getTimeZone("UTC")
        val cv = Calendar.getInstance()
        cv.timeInMillis = timeInMillis
        return timeFormatter.format(cv.time)
    }

    fun getDate(): String {
        val cv = Calendar.getInstance()
        return dateFormatter.format(cv.time)
    }
}