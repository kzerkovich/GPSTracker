package com.kzerk.gpstracker.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kzerk.gpstracker.MainApp
import com.kzerk.gpstracker.MainViewModel
import com.kzerk.gpstracker.databinding.ViewTrackBinding
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig

class ViewTrackFragment : Fragment() {
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
		}
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