package com.kzerk.gpstracker.location

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.kzerk.gpstracker.MainActivity
import com.kzerk.gpstracker.R
import org.osmdroid.util.GeoPoint

class LocationService : Service() {
	private var distance = 0.0f
	private var lastLocation: Location? = null
	private lateinit var locProvider: FusedLocationProviderClient
	private lateinit var locRequest: LocationRequest
	private lateinit var geoPointsList: ArrayList<GeoPoint>

	override fun onBind(p0: Intent?): IBinder? {
		return null
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		startNotification()
		startLocUpdate()
		return START_STICKY
	}

	override fun onCreate() {
		super.onCreate()
		isRun = true
		geoPointsList = ArrayList()
		initLocation()
	}

	override fun onDestroy() {
		super.onDestroy()
		isRun = false
	}

	private val locCallback = object : LocationCallback() {
		override fun onLocationResult(lResult: LocationResult) {
			super.onLocationResult(lResult)
			val curLocation = lResult.lastLocation
			if (lastLocation != null && curLocation != null) {
				if (curLocation.speed > 0.35) {
					distance += lastLocation?.distanceTo(curLocation)!!
					geoPointsList.add(GeoPoint(curLocation.latitude, curLocation.longitude))
				}
				val locModel = LocationModel(
					curLocation.speed,
					distance,
					geoPointsList
				)
				sendLocData(locModel)
			}
			lastLocation = curLocation
		}
	}

	private fun sendLocData(locModel: LocationModel) {
		val i = Intent(LOC_MODEL_INTENT)
		i.putExtra(LOC_MODEL_INTENT, locModel)
		LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(i)
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

	private fun initLocation() {
		val updateInterval = PreferenceManager.getDefaultSharedPreferences(
			this
		).getString("update_time_key", "3000")?.toLong() ?: 3000L
		locRequest = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, updateInterval)
			.setMaxUpdateDelayMillis(updateInterval)
			.build()
		locProvider = LocationServices.getFusedLocationProviderClient(baseContext)
	}

	private fun startLocUpdate() {
		if (ActivityCompat.checkSelfPermission(
				this,
				Manifest.permission.ACCESS_FINE_LOCATION
			) != PackageManager.PERMISSION_GRANTED
		) return

		locProvider.requestLocationUpdates(
			locRequest,
			locCallback,
			Looper.myLooper()
		)
	}

	companion object {
		const val LOC_MODEL_INTENT = "loc_intent"
		const val CHANNEL_ID = "channel_1"
		var isRun = false
		var startTime = 0L
	}
}