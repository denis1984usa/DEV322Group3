package com.hfad.movemore.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.movemore.MainApp
import com.hfad.movemore.MainViewModel
import com.hfad.movemore.R
import com.hfad.movemore.databinding.FragmentMainBinding
import com.hfad.movemore.databinding.RoutesBinding
import com.hfad.movemore.databinding.ViewRouteBinding
import com.hfad.movemore.db.RouteAdapter

class RoutesFragment : Fragment() {
    private lateinit var binding: RoutesBinding
    private lateinit var adapter: RouteAdapter
    private val model: MainViewModel by activityViewModels{
        MainViewModel.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RoutesBinding
            .inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        getRoutes()
    }

    private fun getRoutes() {
        model.routes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.tvEmpty.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun initRcView() = with(binding) {// Init Recycle View
        adapter = RouteAdapter()
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = RoutesFragment()
    }
}