package com.hfad.movemore.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hfad.movemore.MainApp
import com.hfad.movemore.MainViewModel
import com.hfad.movemore.R
import com.hfad.movemore.databinding.ViewRouteBinding
import com.hfad.movemore.databinding.RoutesBinding
import com.hfad.movemore.databinding.FragmentMainBinding
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class ViewRoutesFragment : Fragment() {
    private var startPoint: GeoPoint? = null
    private lateinit var binding: ViewRouteBinding
    private val model: MainViewModel by activityViewModels {
        MainViewModel.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsOsm() // setup the library before loading the map markup
        binding = ViewRouteBinding
            .inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRoute()
        // Move to start point when clicking Current Location button on the View Route fragment.
        binding.fCenter.setOnClickListener {
            if (startPoint != null) binding.map.controller.animateTo(startPoint)
        }
    }

    private fun getRoute() = with (binding){
        model.currentRoute.observe(viewLifecycleOwner) {
            val speed = "Average speed: ${it.speed} mph"
            val distance = "Distance: ${it.distance} mi"
            val date = "Date: ${it.date}"
            tvDate.text = date
            tvTime.text = it.time
            tvAvrSpeed.text = speed
            tvDistance.text = distance
            val polyline = getPolyline(it.geoPoints)
            if (polyline.actualPoints.isEmpty()) return@observe
            map.overlays.add(polyline)
            setMarkers(polyline.actualPoints)
            goToStartPosition(polyline.actualPoints[0])
            startPoint = polyline.actualPoints[0] // first point
        }
    }

    // Move map to start position
    private fun goToStartPosition(startPosition: GeoPoint) {
        binding.map.controller.zoomTo(16.0)
        binding.map.controller.animateTo(startPosition)
    }

    // Add first and last markers for drawn routes
    private fun setMarkers(list: List<GeoPoint>) = with(binding) {
        if(list.size < 2) return@with // code will not start if there are less than 2 points
        val startMarker = Marker(map)
        val finishMarker = Marker(map)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        finishMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.icon = getDrawable(requireContext(), R.drawable.ic_start_position)
       finishMarker.icon = getDrawable(requireContext(), R.drawable.ic_finish_position)
        startMarker.position = list[0]
        finishMarker.position = list[list.size - 1]
        map.overlays.add(startMarker)
        map.overlays.add(finishMarker)
    }

    // Add Polyline overlay to draw over the routes in a map
    private fun getPolyline(geoPoints: String): Polyline {
        val polyline = Polyline()
        val list = geoPoints.split("/")
        list.forEach {
            if (it.isEmpty()) return@forEach
            val points = it.split(",")
            polyline.addPoint(GeoPoint(points[0].toDouble(), points[1].toDouble()))
        }
        return polyline
    }

    private fun settingsOsm() {
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    companion object {
        @JvmStatic
        fun newInstance() = ViewRoutesFragment()
    }
}