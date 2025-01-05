package com.kzerk.gpstracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kzerk.gpstracker.databinding.FragmentViewtrackBinding

class ViewTrackFragment : Fragment() {
	private lateinit var binding: FragmentViewtrackBinding

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentViewtrackBinding.inflate(inflater, container, false)
		return binding.root
	}

	companion object {
		@JvmStatic
		fun newInstance() = ViewTrackFragment()

	}
}