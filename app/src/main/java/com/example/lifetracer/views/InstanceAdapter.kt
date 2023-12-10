package com.example.lifetracer.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.ListItemInstanceBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections

class InstanceAdapter(private val viewModel: InstancesViewModel) :
    ListAdapter<InstanceWithTask, InstanceAdapter.ViewHolder>(InstanceDiffCallback()),
    ItemTouchHelperAdapter, CoroutineScope by CoroutineScope(Dispatchers.Main)  {

    var onItemClickListener: ((InstanceWithTask) -> Unit)? = null
    var job: Job? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemInstanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    //Drag and Drop funtionality
    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val currentList = currentList.toMutableList()
        Collections.swap(currentList, fromPosition, toPosition)
        submitList(currentList)
        onDragEnded()
    }

    // the db update is a bit delayed and in a coroutine for quicker updates
    fun onDragEnded() {
        job?.cancel() // Cancel any existing job
        job = launch {
            delay(500) // Debounce delay
            viewModel.updateInstanceOrder(currentList)
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instance = getItem(position)
        holder.bind(instance)
        holder.itemView.setOnClickListener {
            viewModel.selectAndStartInstance(instance)
        }
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