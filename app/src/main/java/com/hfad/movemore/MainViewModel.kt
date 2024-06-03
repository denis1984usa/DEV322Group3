package com.hfad.movemore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfad.movemore.location.LocationModel

// Hanna
class MainViewModel : ViewModel() {
    val locationUpdates = MutableLiveData<LocationModel>()
    val timeData = MutableLiveData<String>()
}