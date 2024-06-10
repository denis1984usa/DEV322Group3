package com.hfad.movemore.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert
    suspend fun insertRoute(routeItem: RouteItem) // insert a Route item to a table
    @Query("SELECT * FROM route") // SQLite : get everything from the route table
    fun getAllRoutes(): Flow<List<RouteItem>> // checking database for updates
    @Delete
    suspend fun deleteRoute(routeItem: RouteItem) // remove a Route item from a table
}