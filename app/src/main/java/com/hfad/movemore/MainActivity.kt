package com.hfad.movemore

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hfad.movemore.databinding.ActivityMainBinding
import com.hfad.movemore.fragments.MainFragment
import com.hfad.movemore.fragments.RoutesFragment
import com.hfad.movemore.utils.openFragment

class MainActivity : AppCompatActivity() {
    // add binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBottomNavClicks()
        openFragment(MainFragment.newInstance()) // open main fragment
    }
    // adding bottom nav menu buttons listeners
    private fun onBottomNavClicks() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                // switching between fragments
                R.id.id_walking -> openFragment(MainFragment.newInstance())
                R.id.id_routes -> openFragment(RoutesFragment.newInstance())
            }
            true
        }
    }
}