package com.hfad.movemore.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hfad.movemore.MainApp
import com.hfad.movemore.MainViewModel
import com.hfad.movemore.databinding.ViewRouteBinding
import com.hfad.movemore.databinding.RoutesBinding
import com.hfad.movemore.databinding.FragmentMainBinding
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig

class ViewRoutesFragment : Fragment() {
    private lateinit var binding: ViewRouteBinding
    private val model: MainViewModel by activityViewModels{
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
    }

    private fun getRoute() = with (binding){
        model.currentRoute.observe(viewLifecycleOwner) {
            val speed = "Average speed: ${it.speed} mph"
            val distance = "Distance: ${it.distance} mi"
            val date = "Date: ${it.date} mi"
            tvDate.text = date
            tvTime.text = it.time
            tvAvrSpeed.text = speed
            tvDistance.text = distance
        }
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