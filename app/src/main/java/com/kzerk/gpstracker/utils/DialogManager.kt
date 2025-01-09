package com.kzerk.gpstracker.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Toast
import com.kzerk.gpstracker.R
import com.kzerk.gpstracker.databinding.SaveDialogBinding

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

	fun showSaveDialog(context: Context, listener: Listener) {
		val builder = AlertDialog.Builder(context)
		val binding = SaveDialogBinding.inflate(LayoutInflater.from(context), null, false)
		builder.setView(binding.root)
		val dialog = builder.create()
		binding.apply {
			bSave.setOnClickListener {
				listener.onClick()
				dialog.dismiss()
			}
			bCancel.setOnClickListener {
				dialog.dismiss()
			}
		}
		dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		dialog.show()
	}

	interface Listener {
		fun onClick()
	}
}