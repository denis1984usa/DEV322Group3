package com.hfad.movemore.fragments

import android.os.Bundle
import android.util.Log
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
import com.hfad.movemore.db.RouteItem
import com.hfad.movemore.utils.openFragment

class RoutesFragment : Fragment(), RouteAdapter.Listener {
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
        adapter = RouteAdapter(this@RoutesFragment)
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = RoutesFragment()
    }

    override fun onClick(route: RouteItem, type: RouteAdapter.ClickType) {
        when(type) {
            RouteAdapter.ClickType.DELETE -> model.deleteRoute(route) // delete card view
            RouteAdapter.ClickType.OPEN -> {
                model.currentRoute.value = route
                openFragment(ViewRoutesFragment.newInstance())
            } // open card view
        }
    }
}