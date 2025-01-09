package com.kzerk.gpstracker.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "Track")
data class TrackItem(
	@PrimaryKey (autoGenerate = true)
	val id: Int?,
	@ColumnInfo (name = "Time")
	val time: String,
	@ColumnInfo (name = "Date")
	val date: String,
	@ColumnInfo (name = "Distance")
	val distance: String,
	@ColumnInfo (name = "Speed")
	val speed: String,
	@ColumnInfo (name = "geo_points")
	val geoPoints:String
)
