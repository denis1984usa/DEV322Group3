package com.hfad.movemore

import androidx.lifecycle.*
import com.hfad.movemore.db.MainDB
import com.hfad.movemore.db.RouteItem
import com.hfad.movemore.location.LocationModel
import kotlinx.coroutines.launch

class MainViewModel(private val database: MainDB) : ViewModel() {

    val routes: LiveData<List<RouteItem>> = database.getDao().getAllRoutes().asLiveData()
    val locationUpdates = MutableLiveData<LocationModel>()
    val timeData = MutableLiveData<String>()

    fun insertRoute(route: RouteItem) {
        viewModelScope.launch {
            database.getDao().insertRoute(route)
        }
    }

    class ViewModelFactory(private val database: MainDB) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
