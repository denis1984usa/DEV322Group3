package com.hfad.movemore.db

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hfad.movemore.R
import com.hfad.movemore.databinding.RouteItemBinding

// Create Recycle View adapter
class RouteAdapter(private val listener: Listener) : ListAdapter<RouteItem, RouteAdapter.Holder>(Comparator()) {
    class Holder(view : View, private val listener: Listener) : RecyclerView.ViewHolder (view), View.OnClickListener {
        private val binding = RouteItemBinding.bind(view)
        private var routeTemp: RouteItem? = null
        init {
            binding.ibDelete.setOnClickListener(this)
        }
        fun bind(route: RouteItem) = with(binding) {
            routeTemp = route
            val speed = "${route.speed} mph"
            val time = "${route.time}"
            val distance = "${route.distance} mi"
            tvDate.text = route.date
            tvTime.text = time
            tvSpeed.text = speed
            tvDistance.text = distance
        }

        // Delete a route
        override fun onClick(v: View?) {
            routeTemp?. let {listener.onClick(it) }
        }
    }

    class Comparator : DiffUtil.ItemCallback<RouteItem>() {
        override fun areItemsTheSame(oldItem: RouteItem, newItem: RouteItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RouteItem, newItem: RouteItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.route_item, parent, false)
        return Holder(view, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener {
        fun onClick(route: RouteItem)

    }
}