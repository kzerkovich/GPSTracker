package com.kzerk.gpstracker.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

@SuppressLint("SimpleDateFormat")
object TimeUtils {
	private val timeFormatter = SimpleDateFormat("HH:mm:ss")
	private val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm")

	fun getTime(timeInMS: Long): String {
		val cv = Calendar.getInstance()
		timeFormatter.timeZone = TimeZone.getTimeZone("UTC")
		cv.timeInMillis = timeInMS
		return timeFormatter.format(cv.time)
	}

	fun getDate() : String {
		val cv = Calendar.getInstance()
		return dateFormatter.format(cv.time)
	}
}