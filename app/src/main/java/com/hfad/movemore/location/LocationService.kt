package com.hfad.movemore.location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.hfad.movemore.MainActivity
import com.hfad.movemore.R

// Hanna: Implement Location Service
class LocationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null // do not bind with activity
    }

    // START_STICKY: If a phone device exceeds available resources and the location
    // service is terminated, it will be relaunched after freeing up some resources.
    // If START_NOT_STICKY - the process will not be relaunched after freeing up resources.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification() // start notification
        isRunning = true
        return START_STICKY // service will relaunch but all variables will be lost.
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    private fun startNotification() {
        // Check if need to create notification channel, if Android OS 8+, no need to create it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create notification channel
            val nChannel = NotificationChannel(
                CHANNEL_ID,
                "Location Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nManager = getSystemService(NotificationManager::class.java) as NotificationManager
            nManager.createNotificationChannel(nChannel)
        }

        // Create notification intent for Location services
        val nIntent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(
            this,
            10,
            nIntent,
            PendingIntent.FLAG_UPDATE_CURRENT // Add the appropriate flag
        )
        val notification = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        ).setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Tracker is running!")
            .setContentIntent(pIntent).build()
        startForeground(99, notification)
    }

    // Create objects
    companion object {
        const val CHANNEL_ID = "channel_1"
        var isRunning = false
    }
}