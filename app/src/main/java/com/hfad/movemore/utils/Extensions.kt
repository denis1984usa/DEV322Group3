package com.hfad.movemore.utils

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.hfad.movemore.R

// switching between fragments function
fun Fragment.openFragment(f: Fragment) {
    (activity as AppCompatActivity).supportFragmentManager // AppCompatActivity is main activity
        .beginTransaction()
        // adding animation between fragments
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.placeholder, f).commit()
}

fun AppCompatActivity.openFragment(f: Fragment) {
    supportFragmentManager
        .beginTransaction()
        //.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.placeholder, f).commit()
}

fun Fragment.showToast(s: String) {
    Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showToast(s: String) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}

// Check if permission granted or not (true/false).
// If 0  - the permission has been granted.
// If other number - permission hasn't granted.
fun Fragment.checkPermission(p: String): Boolean {
    return when(PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(activity as AppCompatActivity, p) -> true
        else -> false
    }
}