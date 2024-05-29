package com.hfad.movemore.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.hfad.movemore.R
import com.hfad.movemore.databinding.FragmentMainBinding
import com.hfad.movemore.databinding.RoutesBinding
import com.hfad.movemore.databinding.ViewRouteBinding
import com.hfad.movemore.utils.DialogManager
import com.hfad.movemore.utils.checkPermission
import com.hfad.movemore.utils.showToast
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainFragment : Fragment() {
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var binding: FragmentMainBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsOsm() // setup the library before loading the map markup
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    // This function runs after the map markup has been downloaded to memory
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPermissions()
    }

    // Hanna: Updating location status when returning to the app
    override fun onResume(){
        super.onResume()
        checkLocationPermission() //launch it only after register permission
    }

    override fun onPause() {
        super.onPause()
    }

    // Hanna: Function that allows to download maps from the Internet
    private fun settingsOsm() {
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    // Hanna: Connecting GPS provider for getting current location and location tracking
    private fun initOSM() = with(binding) {
        map.controller.setZoom(15.0)
       // map.controller.animateTo(GeoPoint(47.5853, -122.1480))
        val myLocProvider = GpsMyLocationProvider(activity)
        val myLocOverlay = MyLocationNewOverlay(myLocProvider, map)
        myLocOverlay.enableMyLocation() // enable my location
        // enable location tracking, map will move to the current location
        myLocOverlay.enableFollowLocation()
        // this function will launch as soon as after receiving the first location point
        myLocOverlay.runOnFirstFix {
            map.overlays.clear() // clear all layouts
            map.overlays.add(myLocOverlay) // add new layout
        }

        // Schedule a task to zoom in further after a short delay
        map.postDelayed({
            map.controller.setZoom(20.0)
        }, 1000) // 1 second delay
    }

    // Hanna: Register permissions function
    private fun registerPermissions() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) {
            // check if there is a permission to access location
            // Of true - location access has been granted
            // If false - location access has not been granted
            if(it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                initOSM() // load the map
                checkLocationEnabled()
            } else {
                showToast("MoveMore: Location permission not granted.")
            }
        }
    }

    // Hanna: Function that check the Android version and asks for location permission
    private fun checkLocationPermission() {
        // check Android version if greater or equal Android 10 version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermissionAndroidTenAndGreater()
        } else {
            checkPermissionBeforeAndroidTen()
        }
    }

    // Hanna: checking both permissions in Android 10 and greater (newer)
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissionAndroidTenAndGreater() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            && checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            initOSM() // map initialisation
            checkLocationEnabled()
        } else {
            // Calling a dialog if only one permission or none permissions are granted
            pLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        }
    }

    // Hanna: checking only one permission in Android 9 and lesser (older)
    private fun checkPermissionBeforeAndroidTen() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            initOSM() // map initialisation
            checkLocationEnabled()
        } else {
            // Calling a dialog if the permission wasn't granted
            pLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            )
        }
    }

    // Hanna: Check whether GPS has enabled on the device
    private fun checkLocationEnabled() {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isEnabled) {
            // call function from DialogManager
            DialogManager.showLocationEnableDialog(
                activity as AppCompatActivity,
                // Calling interface from DialogManger
                object: DialogManager.Listener {
                    override fun onClick() {
                        // Open location settings on a device
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }

                }
            )
        } else {
            showToast("GPS is enabled")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}