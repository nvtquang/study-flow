package com.example.studyflow.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.databinding.ItemDashboardTextBinding

class DashboardItemAdapter : RecyclerView.Adapter<DashboardItemAdapter.ViewHolder>() {
    private val items = mutableListOf<DashboardItem>()

    fun submitList(nextItems: List<DashboardItem>) {
        items.clear()
        items.addAll(nextItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDashboardTextBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: ItemDashboardTextBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DashboardItem) {
            binding.markerTextView.text = item.marker
            binding.titleTextView.text = item.title
        }
    }
}

data class DashboardItem(
    val marker: String,
    val title: String
)
