package com.hfad.movemore.fragments

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hfad.movemore.MainApp
import com.hfad.movemore.MainViewModel
import com.hfad.movemore.R
import com.hfad.movemore.databinding.FragmentMainBinding
import com.hfad.movemore.db.RouteItem
import com.hfad.movemore.location.LocationService
import com.hfad.movemore.utils.DialogManager
import com.hfad.movemore.utils.TimeUtils
import com.hfad.movemore.utils.checkPermission
import com.hfad.movemore.utils.showToast
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Timer
import java.util.TimerTask
import com.hfad.movemore.location.LocationModel as LocationModel


class MainFragment : Fragment() {
    private var locationModel: LocationModel? = null
    private var pl: Polyline? = null // class Polyline
    private var isServiceRunning = false
    private var firstStart = true
    private var timer: Timer? = null // class Timer
    private var startTime = 0L // variable to store the start time
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var binding: FragmentMainBinding

    private val model: MainViewModel by activityViewModels{
        MainViewModel.ViewModelFactory((requireActivity().application as MainApp).database)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsOsm() // setup the library before loading the map markup
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPermissions()
        setOnClicks()
        checkServiceState()
        updateTime()
        registerLocReceiver()
        locationUpdates()
    }

    private fun setOnClicks() = with(binding) {
        val listener = onClicks()
        fStartStop.setOnClickListener(listener) // Ensure fStartStop is the correct ID
    }

    private fun onClicks(): View.OnClickListener {
        return View.OnClickListener {
            when (it.id) {
                R.id.fStartStop -> startStopService() // Ensure fStartStop is the correct ID
            }
        }
    }

    private fun locationUpdates() = with(binding){
        model.locationUpdates.observe(viewLifecycleOwner) {
            val distance = "Distance: ${String.format("%.1f", it.distance)} mi"
            val speed = "Speed: ${String.format("%.1f", 2.23694f * it.speed)} mph"
            val aSpeed = "Average Speed: ${getAverageSpeed((it.distance))} mph"
            tvDistance.text = distance
            tvSpeed.text = speed
            tvAvrSpeed.text = aSpeed
            locationModel = it
            updatePolyline(it.geoPointList)
        }
    }

    private fun updateTime() {
        model.timeData.observe(viewLifecycleOwner, Observer {
            binding.tvTime.text = it // Ensure tvTime is correctly referenced
        })
    }

    // Start Timer
    private fun startTimer() {
        timer?.cancel()
        timer = Timer()
        startTime = LocationService.startTime // start timer from current time in milliseconds
        timer?.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    model.timeData.value = getCurrentTime() // Ensure getCurrentTime() is defined
                }
            }
        }, 1, 1) // update timer every millisecond
    }

    // Calculate average speed
    // The average speed in mph: 2.23694f * meters per second (m/s).
    private fun getAverageSpeed(distance: Float) : String {
        return String.format("%.1f", 2.23694f * (distance / ((System.currentTimeMillis() - startTime) / 1000.0f)))
    }

    // Calculate difference between current time and start time
    private fun getCurrentTime(): String {
        return "Time: ${TimeUtils.getTime(System.currentTimeMillis() - startTime)}"
    }

    private fun geoPointsToString(list: List<GeoPoint>): String {
        val sb = StringBuilder()
        list.forEach {
            sb.append("${it.latitude}, ${it.longitude}/")
        }
        Log.d("MyLog", "Points: $sb")
        return sb.toString()
    }

    private fun startStopService() {
        if (!isServiceRunning) { // if service is not launched yet
            startLocService() // launch the service
            startTimer() // Start the timer when the service starts
        } else {
            activity?.stopService(Intent(activity, LocationService::class.java))
            binding.fStartStop.setImageResource(R.drawable.ic_play)
            timer?.cancel()
            val route = getRouteItem()
            DialogManager.showSaveDialog(requireContext(),
                route,
                object : DialogManager.Listener {
                override fun onClick() {
                    showToast("Route Saved!")
                    model.insertRoute(route)
                }
            })
        }
        isServiceRunning = !isServiceRunning
    }

    // Get Route Item
    private fun getRouteItem(): RouteItem {
        return RouteItem(
            null,
            getCurrentTime(),
            TimeUtils.getDate(),
            String.format("%.1f", locationModel?.distance?.div(1609.34f) ?: 0f),
            String.format("%.1f", 2.23694f * (locationModel?.distance ?: 0.0f) / ((System.currentTimeMillis() - startTime) / 1000.0f)),
            geoPointsToString(locationModel?.geoPointList ?: listOf())
        )
    }

    // Location service continues working after tapping on notification message
    private fun checkServiceState() {
        isServiceRunning = LocationService.isRunning
        if (isServiceRunning) {
            binding.fStartStop.setImageResource(R.drawable.ic_stop)
            startTimer()
        }
    }

    // Run location service
    private fun startLocService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(Intent(activity, LocationService::class.java))
        } else {
            activity?.startService(Intent(activity, LocationService::class.java))
        }
        binding.fStartStop.setImageResource(R.drawable.ic_stop)
        LocationService.startTime = System.currentTimeMillis()
        startTimer()
    }

    override fun onResume() {
        super.onResume()
        checkLocationPermission() // Launch it only after registering permission
    }

    private fun settingsOsm() {
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun initOSM() = with(binding) {
        pl = Polyline()
        pl?.outlinePaint?.color = Color.BLUE  // polyline color: Blue
        map.controller.setZoom(15.0)
        val myLocProvider = GpsMyLocationProvider(activity)
        val myLocOverlay = MyLocationNewOverlay(myLocProvider, map)
        myLocOverlay.enableMyLocation()
        myLocOverlay.enableFollowLocation()
        myLocOverlay.runOnFirstFix {
            map.overlays.clear()
            map.overlays.add(myLocOverlay)
            map.overlays.add(pl)
        }

        map.postDelayed({
            map.controller.setZoom(20.0)
        }, 1000)
    }

    private fun registerPermissions() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                initOSM()
                checkLocationEnabled()
            } else {
                showToast("MoveMore: Location permission not granted.")
            }
        }
    }

    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermissionAndroidTenAndGreater()
        } else {
            checkPermissionBeforeAndroidTen()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissionAndroidTenAndGreater() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            && checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            initOSM()
            checkLocationEnabled()
        } else {
            pLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        }
    }

    private fun checkPermissionBeforeAndroidTen() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            initOSM()
            checkLocationEnabled()
        } else {
            pLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            )
        }
    }

    private fun checkLocationEnabled() {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isEnabled) {
            DialogManager.showLocationEnableDialog(
                activity as AppCompatActivity,
                object : DialogManager.Listener {
                    override fun onClick() {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                }
            )
        } else {
            showToast("GPS is enabled")
        }
    }

    // Broadcast receiver
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, i: Intent?) {
            if (i?.action == LocationService.LOC_MODEL_INTENT ){
                val locModel = i.getSerializableExtra(LocationService.LOC_MODEL_INTENT) as LocationModel
                model.locationUpdates.value = locModel
            }
        }
    }

    // Specify what Intents we want to receive
    private fun registerLocReceiver(){
        val locFilter = IntentFilter(LocationService.LOC_MODEL_INTENT)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .registerReceiver(receiver, locFilter)
    }

    // Add points to draw a polyline
    private fun addPoint(list: List<GeoPoint>){
        pl?.addPoint(list[list.size - 1])
    }
    // Fill polyline from GeoPoint arrayList, also when the app is in background
    // This function should be launched only once
    private fun fillPolyline(list: List<GeoPoint>){
        list.forEach {
            pl?.addPoint(it)
        }
    }

    private fun updatePolyline(list : List<GeoPoint>){
        if (list.size > 1 && firstStart){
            fillPolyline(list)
            firstStart = false
        } else {
            addPoint(list)
        }
    }

    override fun onDetach() {
        super.onDetach()
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .unregisterReceiver(receiver)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
