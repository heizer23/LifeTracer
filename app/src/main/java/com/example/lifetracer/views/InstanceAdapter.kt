package com.example.lifetracer.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.charts.ChartManager
import com.example.lifetracer.data.InstanceWithHistory
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.ListItemInstanceBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections

class InstanceAdapter(
    private val viewModel: InstancesViewModel,
    private val onDeleteInstance: (InstanceWithTask) -> Unit,
    private val onFinishInstance: (InstanceWithTask) -> Unit
) : ListAdapter<InstanceWithHistory, InstanceAdapter.ViewHolder>(InstanceDiffCallback()),
    ItemTouchHelperAdapter, CoroutineScope by CoroutineScope(Dispatchers.Main)  {

    var onItemClickListener: ((InstanceWithTask) -> Unit)? = null
    var job: Job? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemInstanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onDeleteInstance, onFinishInstance)
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
            val instancesWithTask = currentList.map { it.instanceWithTask }
            viewModel.updateInstanceOrder(instancesWithTask)
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instanceWithHistory  = getItem(position)
        holder.bind(instanceWithHistory )
        holder.itemView.setOnClickListener {
            viewModel.selectAndStartInstance(instanceWithHistory.instanceWithTask )
        }
    }

    class ViewHolder(private val binding: ListItemInstanceBinding,
                     private val onDeleteInstance: (InstanceWithTask) -> Unit,
                     private val onFinishInstance: (InstanceWithTask) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(instanceWithHistory : InstanceWithHistory ) {
            binding.instanceWithTask = instanceWithHistory.instanceWithTask

            val chartManager = ChartManager(binding.barChart) // Assuming barChart is in your item layout
            chartManager.setupChart(instanceWithHistory.history)

            binding.buttonDeleteTask.setOnClickListener {
                onDeleteInstance(instanceWithHistory.instanceWithTask )
            }

            binding.buttonFinishInstance.setOnClickListener {
                onFinishInstance(instanceWithHistory.instanceWithTask )
            }

            binding.executePendingBindings()
        }
    }

    class InstanceDiffCallback : DiffUtil.ItemCallback<InstanceWithHistory>() {
        override fun areItemsTheSame(oldItem: InstanceWithHistory, newItem: InstanceWithHistory): Boolean {
            // Define logic to check if items are the same, usually based on unique IDs
            return oldItem.instanceWithTask.instance.id == newItem.instanceWithTask.instance.id
        }
        override fun areContentsTheSame(oldItem: InstanceWithHistory, newItem: InstanceWithHistory): Boolean {
            // Define logic to check if the content of items is the same
            return oldItem == newItem
        }
    }
}