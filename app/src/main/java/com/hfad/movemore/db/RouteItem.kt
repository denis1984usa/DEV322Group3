package com.hfad.movemore.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Hanna: Create Database Entity
@Entity (tableName = "route")
data class RouteItem(
    @PrimaryKey (autoGenerate = true)
    val id: Int?,
    @ColumnInfo (name = "time")
    val time: String,
    @ColumnInfo (name = "date")
    val date: String,
    @ColumnInfo (name = "distance")
    val distance: String,
    @ColumnInfo (name = "speed")
    val speed: String,
    @ColumnInfo (name = "geo_points")
    val geoPoints: String,
)
