package com.hfad.movemore.location

import org.osmdroid.util.GeoPoint

// Hanna: Create variables for tracking data
data class LocationModel(
    val speed: Float = 0.0f,
    val distance: Float = 0.0f,
    val geoPointList: ArrayList<GeoPoint>

)
