package com.hfad.movemore.location

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.hfad.movemore.MainActivity
import com.hfad.movemore.R
import org.osmdroid.util.GeoPoint

// Hanna: Implement Location Service
class LocationService : Service() {
    // Global Variables
    private var distance = 0.0f;
    private var lastLocation: Location? = null
    private lateinit var locProvider: FusedLocationProviderClient
    private lateinit var locRequest: LocationRequest
    private lateinit var geoPointsList: ArrayList<GeoPoint>
    override fun onBind(intent: Intent?): IBinder? {
        return null // do not bind with activity
    }

    // START_STICKY: If a phone device exceeds available resources and the location
    // service is terminated, it will be relaunched after freeing up some resources.
    // If START_NOT_STICKY - the process will not be relaunched after freeing up resources.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification() // start notification
        startLocationUpdates() // if location permissions granted
        isRunning = true
        return START_STICKY // service will relaunch but all variables will be lost.
    }

    override fun onCreate() {
        super.onCreate()
        geoPointsList = java.util.ArrayList()
        initLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        locProvider.removeLocationUpdates(locCalBack)// Stop receiving Request location information
    }

    private val locCalBack = object : LocationCallback() {
        override fun onLocationResult(lResult: LocationResult) {
            super.onLocationResult(lResult)
            val currentLocation = lResult.lastLocation
            if (lastLocation != null && currentLocation != null) {
                // measure from old to new location
                distance += lastLocation?.distanceTo(currentLocation)!!
                geoPointsList.add(GeoPoint(currentLocation.latitude, currentLocation.longitude))
                // Save location information
                val locModel = LocationModel(
                    currentLocation.speed,
                    distance,
                    geoPointsList
                )
                sendLocData(locModel)
            }
            lastLocation = currentLocation

            // Log.d("MyLog", "Distance: $distance") // Filtered Logcat logs
        }
    }
    // Send locations information saved in LocationModel
    private fun sendLocData(locModel: LocationModel) {
        // Create Intent
        val i = Intent(LOC_MODEL_INTENT)
        i.putExtra(LOC_MODEL_INTENT, locModel)
        // Send Intent via Broadcast
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(i)
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

    private fun initLocation(){
        locRequest = LocationRequest.create()
        locRequest.interval = 5000 // update every 5 sec
        locRequest.fastestInterval = 5000
        locRequest.priority = PRIORITY_HIGH_ACCURACY
        locProvider = LocationServices.getFusedLocationProviderClient(baseContext)
    }

    // Hanna: Request location information
    private fun startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        locProvider.requestLocationUpdates(
            locRequest,
            locCalBack,
            Looper.myLooper() // always repeat request location
        )
    }

    // Create objects
    companion object {
        const val LOC_MODEL_INTENT = "loc_intent"
        const val CHANNEL_ID = "channel_1"
        var isRunning = false
        var startTime = 0L
    }
}