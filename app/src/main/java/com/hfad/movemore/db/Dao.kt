package com.hfad.movemore.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow // Correct import for Flow
import com.hfad.movemore.db.RouteItem

@Dao
interface Dao {
    @Query("SELECT * FROM route") // sql request to select all data from the route table
    fun getAllRoutes(): Flow<List<RouteItem>> // Corrected Flow import
    @Insert
    suspend fun insertRoute(routeItem: RouteItem) // insert a Route item to a table
    @Delete
    suspend fun deleteRoute(routeItem: RouteItem) // delete a Route item to a table

}