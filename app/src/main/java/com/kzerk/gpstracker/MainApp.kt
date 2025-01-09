package com.kzerk.gpstracker

import android.app.Application
import com.kzerk.gpstracker.db.MainDB

class MainApp : Application() {
	val database by lazy {
		MainDB.getDatabase(this)
	}
}