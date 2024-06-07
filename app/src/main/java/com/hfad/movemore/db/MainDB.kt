package com.hfad.movemore.db

import android.content.Context
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Room
import androidx.room.RoomDatabase

// Create Database access
@Database(entities = [RouteItem::class], version = 1)
abstract class MainDB : RoomDatabase() {
    abstract fun getDao(): Dao // Dao interface (needed for saving routeItem to database)
    companion object {
        @Volatile
        public var INSTANCE: MainDB? = null

        fun getDatabase(context: Context) : MainDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDB :: class.java,
                    "GpsTracker.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}