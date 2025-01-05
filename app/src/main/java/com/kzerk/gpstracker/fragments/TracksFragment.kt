package com.kzerk.gpstracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kzerk.gpstracker.databinding.FragmentTracksBinding

class TracksFragment : Fragment() {
	private lateinit var binding: FragmentTracksBinding

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentTracksBinding.inflate(inflater, container, false)
		return binding.root
	}

	companion object {

		@JvmStatic
		fun newInstance() = TracksFragment()
	}
}