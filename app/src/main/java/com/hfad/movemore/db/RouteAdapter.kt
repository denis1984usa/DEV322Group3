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
class RouteAdapter : ListAdapter<RouteItem, RouteAdapter.Holder>(Comparator()) {
    class Holder(view : View) : RecyclerView.ViewHolder (view) {
        private val binding = RouteItemBinding.bind(view)
        fun bind(route: RouteItem) = with(binding) {
            val speed = "${route.speed} mph"
            val time = "${route.time}"
            val distance = "${route.distance} mi"
            tvDate.text = route.date
            tvTime.text = time
            tvSpeed.text = speed
            tvDistance.text = distance
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
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}