package com.hfad.movemore.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

object TimeUtils {
    @SuppressLint("SimpleDateFormat")
    fun getTime(timeInMillis: Long): String {
        val timeFormatter = SimpleDateFormat("HH:mm:ss:SSS")
        timeFormatter.timeZone = TimeZone.getTimeZone("UTC")
        val cv = Calendar.getInstance()
        cv.timeInMillis = timeInMillis
        return timeFormatter.format(cv.time)
    }
}