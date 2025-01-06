package com.kzerk.gpstracker.fragments

import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kzerk.gpstracker.databinding.FragmentMainBinding
import com.kzerk.gpstracker.utils.checkPermission
import com.kzerk.gpstracker.utils.showToast
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainFragment : Fragment() {
	private lateinit var pLaucnher: ActivityResultLauncher<Array<String>>
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
		checkLocPermission()
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
		pLaucnher = registerForActivityResult(
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
			pLaucnher.launch(
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
			pLaucnher.launch(
				arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
			)
		}
	}

	private fun checkLocationEnabled() {
		val lManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
		val isEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

		if (!isEnabled) {
			showToast("GPS deactivated")
		}
	}

	companion object {
		@JvmStatic
		fun newInstance() = MainFragment()

	}
}