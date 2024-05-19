package com.hfad.movemore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hfad.movemore.databinding.ViewRouteBinding
import com.hfad.movemore.databinding.RoutesBinding
import com.hfad.movemore.databinding.FragmentMainBinding

class ViewRoutesFragment : Fragment() {
    private lateinit var binding: ViewRouteBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewRouteBinding
            .inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ViewRoutesFragment()
    }
}