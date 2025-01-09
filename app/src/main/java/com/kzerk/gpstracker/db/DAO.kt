package com.kzerk.gpstracker.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DAO {
	@Insert
	suspend fun insertTrack(trackItem: TrackItem)

	@Query("select * from Track")
	fun getAllTracks(): Flow<List<TrackItem>>

	@Delete
	suspend fun deleteTrack(trackItem: TrackItem)
}