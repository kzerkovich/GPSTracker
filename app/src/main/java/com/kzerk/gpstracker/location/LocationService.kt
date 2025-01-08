package com.kzerk.gpstracker.location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.kzerk.gpstracker.MainActivity
import com.kzerk.gpstracker.R

class LocationService : Service() {
	override fun onBind(p0: Intent?): IBinder? {
		return null
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		startNotification()
		return START_STICKY
	}

	override fun onCreate() {
		super.onCreate()
		isRun = true
	}

	override fun onDestroy() {
		super.onDestroy()
		isRun = false
	}

	private fun startNotification() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val nChannel = NotificationChannel(
				CHANNEL_ID,
				"Location Service",
				NotificationManager.IMPORTANCE_DEFAULT
			)
			val nManager = getSystemService(NotificationManager::class.java) as NotificationManager
			nManager.createNotificationChannel(nChannel)
		}

		val nIntent = Intent(this, MainActivity::class.java)
		val pIntent = PendingIntent.getActivity(
			this,
			10,
			nIntent,
			PendingIntent.FLAG_MUTABLE
		)
		val notification = NotificationCompat.Builder(
			this,
			CHANNEL_ID
		).setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle("Tracker Running")
			.setContentIntent(pIntent)
			.build()
		startForeground(99, notification)
	}

	companion object {
		const val CHANNEL_ID = "channel_1"
		var isRun = false
	}
}