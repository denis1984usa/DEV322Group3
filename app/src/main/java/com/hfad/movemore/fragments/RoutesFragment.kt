package com.hfad.movemore.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hfad.movemore.R
import com.hfad.movemore.databinding.FragmentMainBinding
import com.hfad.movemore.databinding.RoutesBinding
import com.hfad.movemore.databinding.ViewRouteBinding

class RoutesFragment : Fragment() {
    private lateinit var binding: RoutesBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RoutesBinding
            .inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = RoutesFragment()
    }
}