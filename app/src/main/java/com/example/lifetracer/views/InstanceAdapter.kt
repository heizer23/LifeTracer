package com.example.lifetracer.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.ListItemInstanceBinding

class InstanceAdapter : ListAdapter<InstanceWithTask, InstanceAdapter.ViewHolder>(InstanceDiffCallback()) {

    var onItemClickListener: ((InstanceWithTask) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemInstanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instance = getItem(position)
        holder.bind(instance)
        holder.itemView.setOnClickListener { onItemClickListener?.invoke(instance) }
    }

    class ViewHolder(private val binding: ListItemInstanceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(instance: InstanceWithTask) {
            binding.instanceWithTask = instance
            binding.executePendingBindings()
        }
    }

    class InstanceDiffCallback : DiffUtil.ItemCallback<InstanceWithTask>() {
        override fun areItemsTheSame(oldItem: InstanceWithTask, newItem: InstanceWithTask): Boolean {
            // Define logic to check if items are the same, usually based on unique IDs
            return oldItem.instance.id == newItem.instance.id
        }
        override fun areContentsTheSame(oldItem: InstanceWithTask, newItem: InstanceWithTask): Boolean {
            // Define logic to check if the content of items is the same
            return oldItem == newItem
        }
    }
}