package com.hfad.movemore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hfad.movemore.db.MainDB
import com.hfad.movemore.db.RouteItem
import com.hfad.movemore.location.LocationModel
import kotlinx.coroutines.launch

// Hanna: Database
@Suppress("UNCHECKED_CAST")
class MainViewModel(db: MainDB) : ViewModel() {
    val dao = db.getDao()
    val locationUpdates = MutableLiveData<LocationModel>()
    val timeData = MutableLiveData<String>()
    val routes = dao.getAllRoutes().asLiveData()

    // Insert route
    fun insertRoute(routeItem: RouteItem) = viewModelScope.launch {
        dao.insertRoute(routeItem)
    }

    // Delete route
    fun deleteRoute(routeItem: RouteItem) = viewModelScope.launch {
        dao.deleteRoute(routeItem)
    }

    class ViewModelFactory(private val db: MainDB) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(db) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}