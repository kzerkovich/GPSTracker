package com.kzerk.gpstracker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kzerk.gpstracker.db.MainDB
import com.kzerk.gpstracker.db.TrackItem
import com.kzerk.gpstracker.location.LocationModel
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class MainViewModel(db: MainDB) : ViewModel() {
	val dao = db.getDao()
	val locationUpdates = MutableLiveData<LocationModel>()
	val currentTrack = MutableLiveData<TrackItem>()
	val timeData = MutableLiveData<String>()
	val tracks = dao.getAllTracks().asLiveData()

	fun insertTrack(trackItem: TrackItem) = viewModelScope.launch {
		dao.insertTrack(trackItem)
	}
	fun deleteTrack(trackItem: TrackItem) = viewModelScope.launch {
		dao.deleteTrack(trackItem)
	}

	class ViewModelFactory(private val db: MainDB) : ViewModelProvider.Factory {
		override fun <T : ViewModel> create(modelClass: Class<T>): T {
			if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
				return MainViewModel(db) as T
			}
			throw IllegalArgumentException("Unknown ViewModel class")
		}
	}
}