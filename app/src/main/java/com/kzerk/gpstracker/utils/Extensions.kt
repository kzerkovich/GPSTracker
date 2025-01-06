package com.kzerk.gpstracker.utils

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kzerk.gpstracker.R

fun Fragment.openFragment(fragment: Fragment) {
	(activity as AppCompatActivity).supportFragmentManager
		.beginTransaction()
		.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
		.replace(R.id.placeHolder, fragment)
		.commit()
}

fun AppCompatActivity.openFragment(fragment: Fragment) {
	if (supportFragmentManager.fragments.isNotEmpty()) {
		if (supportFragmentManager.fragments[0].javaClass == fragment.javaClass) return
	}
	supportFragmentManager
		.beginTransaction()
		.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
		.replace(R.id.placeHolder, fragment)
		.commit()
}

fun Fragment.showToast(message: String) {
	Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showToast(message: String) {
	Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.checkPermission(permission: String): Boolean {
	return when(PackageManager.PERMISSION_GRANTED) {
		ContextCompat.checkSelfPermission(activity as AppCompatActivity, permission) -> true
		else -> false
	}
}