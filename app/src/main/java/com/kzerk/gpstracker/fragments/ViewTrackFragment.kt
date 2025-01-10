package com.kzerk.gpstracker.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.kzerk.gpstracker.MainApp
import com.kzerk.gpstracker.MainViewModel
import com.kzerk.gpstracker.R
import com.kzerk.gpstracker.databinding.ViewTrackBinding
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class ViewTrackFragment : Fragment() {
	private var startPoint: GeoPoint? = null
	private lateinit var binding: ViewTrackBinding
	private val model: MainViewModel by activityViewModels {
		MainViewModel.ViewModelFactory((requireContext().applicationContext as MainApp).database)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		settingsOsm()
		binding = ViewTrackBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		getTrack()
		binding.fCenter.setOnClickListener {
			if (startPoint != null) binding.map.controller.animateTo(startPoint)
		}
	}

	private fun getTrack() = with(binding) {
		model.currentTrack.observe(viewLifecycleOwner) {
			val speed = "Average speed: ${it.speed}"
			val distance = "Distance: ${it.distance} km"
			val date = "Date: ${it.date}"
			tvDate.text = date
			tvTime.text = it.time
			tvAvgVelocity.text = speed
			tvDistance.text = distance
			val polyline = getPolyline(it.geoPoints)
			map.overlays.add(polyline)
			setMarkers(polyline.actualPoints)
			zoomStart(polyline.actualPoints[0])
			startPoint = polyline.actualPoints[0]
		}
	}

	private fun zoomStart(start: GeoPoint) {
		binding.map.controller.zoomTo(18.0)
		binding.map.controller.animateTo(start)
	}

	private fun setMarkers(list: List<GeoPoint>) = with(binding) {
		val startMarker = Marker(map)
		val finishMarker = Marker(map)
		startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
		finishMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
		startMarker.icon = getDrawable(requireContext(), R.drawable.ic_start)
		finishMarker.icon = getDrawable(requireContext(), R.drawable.ic_finish)
		startMarker.position = list[0]
		finishMarker.position = list[list.size - 1]
		map.overlays.add(startMarker)
		map.overlays.add(finishMarker)
	}

	private fun getPolyline(geoPoints: String) : Polyline{
		val polyline = Polyline()
		polyline.outlinePaint.color = Color.parseColor(
			PreferenceManager.getDefaultSharedPreferences(requireContext())
				.getString("color_key", "#FF000000")
		)
		val list = geoPoints.split("/")
		list.forEach {
			if (it.isEmpty()) return@forEach
			val point = it.split(",")
			polyline.addPoint(GeoPoint(point[0].toDouble(), point[1].toDouble()))
		}
		return polyline
	}

	private fun settingsOsm() {
		Configuration.getInstance().load(
			activity as AppCompatActivity,
			activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
		)
		Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME
	}

	companion object {
		@JvmStatic
		fun newInstance() = ViewTrackFragment()

	}
}