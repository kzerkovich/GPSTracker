package com.kzerk.gpstracker.fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.kzerk.gpstracker.MainViewModel
import com.kzerk.gpstracker.R
import com.kzerk.gpstracker.databinding.FragmentMainBinding
import com.kzerk.gpstracker.location.LocationModel
import com.kzerk.gpstracker.location.LocationService
import com.kzerk.gpstracker.utils.DialogManager
import com.kzerk.gpstracker.utils.TimeUtils
import com.kzerk.gpstracker.utils.checkPermission
import com.kzerk.gpstracker.utils.showToast
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Timer
import java.util.TimerTask

class MainFragment : Fragment() {
	private var isServiceRun = false
	private var timer: Timer? = null
	private var startTime = 0L

	private val model : MainViewModel by activityViewModels()
	private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
	private lateinit var binding: FragmentMainBinding

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		settingsOsm()
		binding = FragmentMainBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		registerPermission()
		setOnClicks()
		checkService()
		updateTime()
		registerLogReceiver()
		locationUpdates()
	}

	override fun onResume() {
		super.onResume()
		checkLocPermission()
	}

	private fun setOnClicks() = with(binding) {
		val listener = onClicks()
		fStartStop.setOnClickListener(listener)
	}

	private fun onClicks(): View.OnClickListener {
		return View.OnClickListener {
			when (it.id) {
				R.id.fStartStop -> startStopService()
			}
		}
	}

	private fun updateTime() {
		model.timeData.observe(viewLifecycleOwner) {
			binding.tvTime.text = it
		}
	}


	@SuppressLint("DefaultLocale")
	private fun locationUpdates() =  with(binding) {
		model.locationUpdates.observe(viewLifecycleOwner) {
			val distance = "Distance: ${String.format("%.1f", it.distance)} m"
			val speed = "Velocity: ${String.format("%.1f", 3.6f * it.velocity)} km/h"
			val aSpeed = "Average Velocity ${getAverageSpeed(it.distance)}"
			tvDistance.text = distance
			tvVelocity.text = speed
			tvAvgVelocity.text = aSpeed
		}
	}

	private fun startTimer() {
		timer?.cancel()
		timer = Timer()
		startTime = LocationService.startTime
		timer?.schedule(object : TimerTask() {
			override fun run() {
				activity?.runOnUiThread {
					model.timeData.value = getCurrentTime()
				}
			}
		}, 1000, 1000)
	}

	private fun getAverageSpeed(distance: Float) : String {
		return String.format("%1.f", 3.6f * (distance / ((System.currentTimeMillis() - startTime) / 1000.0f)))
	}

	private fun getCurrentTime(): String {
		return "Time: ${TimeUtils.getTime(System.currentTimeMillis() - startTime)}"
	}

	private fun startStopService() {
		if (!isServiceRun) {
			startService()
		} else {
			activity?.stopService(Intent(activity, LocationService::class.java))
			binding.fStartStop.setImageResource(R.drawable.ic_play)
			timer?.cancel()
		}
		isServiceRun = !isServiceRun
	}

	private fun startService() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			activity?.startForegroundService(Intent(activity, LocationService::class.java))
		} else {
			activity?.startService(Intent(activity, LocationService::class.java))
		}
		binding.fStartStop.setImageResource(R.drawable.ic_stop)
		LocationService.startTime = System.currentTimeMillis()
		startTimer()
	}

	private fun checkService() {
		isServiceRun = LocationService.isRun
		if (isServiceRun) {
			binding.fStartStop.setImageResource(R.drawable.ic_stop)
			startTimer()
		}
	}

	private fun settingsOsm() {
		Configuration.getInstance().load(
			activity as AppCompatActivity,
			activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
		)
		Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME
	}

	private fun initOSM() = with(binding) {
		map.controller.setZoom(20.0)
		val myLocProvider = GpsMyLocationProvider(activity)
		val myLocOverlay = MyLocationNewOverlay(myLocProvider, map)
		myLocOverlay.enableMyLocation()
		myLocOverlay.enableFollowLocation()
		myLocOverlay.runOnFirstFix {
			map.overlays.clear()
			map.overlays.add(myLocOverlay)
		}
	}

	private fun registerPermission() {
		pLauncher = registerForActivityResult(
			ActivityResultContracts.RequestMultiplePermissions()
		) {
			if (it[android.Manifest.permission.ACCESS_FINE_LOCATION] == true) {
				initOSM()
				checkLocationEnabled()
			} else {
				showToast("Нет доступа к геолокации")
			}
		}
	}

	private fun checkLocPermission() {
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
			checkAfter10()
		} else {
			checkBefore10()
		}
	}

	@RequiresApi(Build.VERSION_CODES.Q)
	private fun checkAfter10() {
		if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
			&& checkPermission(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
		) {
			initOSM()
			checkLocationEnabled()
		} else {
			pLauncher.launch(
				arrayOf(
					android.Manifest.permission.ACCESS_FINE_LOCATION,
					android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
				)
			)
		}
	}

	private fun checkBefore10() {
		if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
			initOSM()
			checkLocationEnabled()
		} else {
			pLauncher.launch(
				arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
			)
		}
	}

	private fun checkLocationEnabled() {
		val lManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
		val isEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

		if (!isEnabled) {
			DialogManager.showLocationDialog(
				activity as AppCompatActivity,
				object : DialogManager.Listener {
					override fun onClick() {
						startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
					}
				}
			)
		}
	}

	private val receiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, i: Intent?) {
			if (i?.action == LocationService.LOC_MODEL_INTENT) {
				val locModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
					i.getSerializableExtra(LocationService.LOC_MODEL_INTENT, LocationModel::class.java)
				} else {
					@Suppress("DEPRECATION")
					i.getSerializableExtra(LocationService.LOC_MODEL_INTENT) as LocationModel
				}
				model.locationUpdates.value = locModel
			}
		}
	}

	private fun registerLogReceiver() {
		val locFilter = IntentFilter(LocationService.LOC_MODEL_INTENT)
		LocalBroadcastManager.getInstance(activity as AppCompatActivity)
			.registerReceiver(receiver, locFilter)
	}

	companion object {
		@JvmStatic
		fun newInstance() = MainFragment()

	}
}