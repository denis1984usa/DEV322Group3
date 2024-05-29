package com.hfad.movemore.utils

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.hfad.movemore.R

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

    // Hanna: Creating an interface
    interface Listener {
        fun onClick()
    }
}