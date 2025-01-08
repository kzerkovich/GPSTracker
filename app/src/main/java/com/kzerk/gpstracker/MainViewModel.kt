package com.kzerk.gpstracker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kzerk.gpstracker.location.LocationModel

class MainViewModel : ViewModel() {
	val locationUpdates = MutableLiveData<LocationModel>()
	val timeData = MutableLiveData<String>()
}