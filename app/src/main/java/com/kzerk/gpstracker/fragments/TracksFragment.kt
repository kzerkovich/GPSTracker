package com.kzerk.gpstracker.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kzerk.gpstracker.MainApp
import com.kzerk.gpstracker.MainViewModel
import com.kzerk.gpstracker.databinding.FragmentTracksBinding
import com.kzerk.gpstracker.db.TrackAdapter
import com.kzerk.gpstracker.db.TrackItem
import com.kzerk.gpstracker.utils.openFragment

class TracksFragment : Fragment(), TrackAdapter.Listener {
	private lateinit var binding: FragmentTracksBinding
	private lateinit var adapter: TrackAdapter
	private val model: MainViewModel by activityViewModels {
		MainViewModel.ViewModelFactory((requireContext().applicationContext as MainApp).database)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentTracksBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initRcView()
		getTracks()
	}

	private fun getTracks() {
		model.tracks.observe(viewLifecycleOwner) {
			adapter.submitList(it)
			binding.tvEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
		}
	}

	private fun initRcView() =  with(binding) {
		adapter = TrackAdapter(this@TracksFragment)
		rcView.layoutManager = LinearLayoutManager(requireContext())
		rcView.adapter = adapter
	}

	companion object {
		@JvmStatic
		fun newInstance() = TracksFragment()
	}

	override fun onClick(track: TrackItem, type: TrackAdapter.ClickType) {
		when (type) {

			TrackAdapter.ClickType.DELETE -> model.deleteTrack(track)
			TrackAdapter.ClickType.OPEN -> {
				model.currentTrack.value = track
				openFragment(ViewTrackFragment.newInstance())
			}
		}
	}
}