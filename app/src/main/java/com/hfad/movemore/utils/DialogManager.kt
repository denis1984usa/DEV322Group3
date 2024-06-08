package com.hfad.movemore.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Toast
import com.hfad.movemore.R
import com.hfad.movemore.databinding.SaveDialogBinding
import com.hfad.movemore.db.RouteItem

// Hanna
object DialogManager {
    // Hanna: Create standard dialog using builder
    fun showLocationEnableDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(R.string.location_disabled)
        dialog.setMessage(context.getString(R.string.location_dialog_message))
        // Set buttons
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES") { _, _ ->
            Toast.makeText(context, "YES", Toast.LENGTH_SHORT).show()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO") { _, _ ->
            Toast.makeText(context, "NO", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    // Show Save Routes dialog (with custom layout)
    fun showSaveDialog(context: Context, item: RouteItem?, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val binding = SaveDialogBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val dialog = builder.create()
        binding.apply {
            tvTimeDialog.text = "${item?.time} h"
            tvSpeedDialog.text = "${item?.speed} mph"
            tvDistanceDialog.text = "${item?.distance} mi"
            bSave.setOnClickListener {
                listener.onClick()
                dialog.dismiss()
            }
            bCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show() // Launch the dialog
    }

    // Hanna: Creating an interface
    interface Listener {
        fun onClick()
    }
}