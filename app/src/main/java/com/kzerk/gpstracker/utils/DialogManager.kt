package com.kzerk.gpstracker.utils

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.kzerk.gpstracker.R

object DialogManager {
	fun showLocationDialog(context: Context, listener: Listener) {
		val builder = AlertDialog.Builder(context)
		val dialog = builder.create()
		dialog.setTitle(R.string.location_disabled)
		dialog.setMessage(context.getString(R.string.location_dialog_message))
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok)) {
			_, _ -> listener.onClick()
		}
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.no)) {
				_, _ -> dialog.dismiss()
		}
		dialog.show()
	}

	interface Listener {
		fun onClick()
	}
}