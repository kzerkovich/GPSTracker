package com.kzerk.gpstracker.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

object TimeUtils {
	@SuppressLint("SimpleDateFormat")
	private val timeFormatter = SimpleDateFormat("HH:mm:ss")

	fun getTime(timeInMS: Long): String {
		val cv = Calendar.getInstance()
		timeFormatter.timeZone = TimeZone.getTimeZone("UTC")
		cv.timeInMillis = timeInMS
		return timeFormatter.format(cv.time)
	}
}