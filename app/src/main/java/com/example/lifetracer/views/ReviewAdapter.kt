package com.example.lifetracer.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.ListItemReviewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewAdapter(
    private val scope: CoroutineScope,
    private val viewModel: InstancesViewModel,
) : ListAdapter<InstanceWithTask, ReviewAdapter.ViewHolder>(InstanceDiffCallback()),
    CoroutineScope by CoroutineScope(Dispatchers.Main)  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instanceWithTask  = getItem(position)

        holder.bind(instanceWithTask, scope)
        holder.itemView.setOnClickListener {
            //todo: Start detail view
        }
    }

    class ViewHolder(private val binding: ListItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(instanceWithTask : InstanceWithTask, scope: CoroutineScope) {
            binding.instanceWithTask = instanceWithTask

            scope.launch {
            }
            binding.executePendingBindings()
        }
    }

    class InstanceDiffCallback : DiffUtil.ItemCallback<InstanceWithTask>() {
        override fun areItemsTheSame(oldItem: InstanceWithTask, newItem: InstanceWithTask): Boolean {
            // Define logic to check if items are the same, usually based on unique IDs
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: InstanceWithTask, newItem: InstanceWithTask): Boolean {
            // Define logic to check if the content of items is the same
            return oldItem == newItem
        }
    }
}