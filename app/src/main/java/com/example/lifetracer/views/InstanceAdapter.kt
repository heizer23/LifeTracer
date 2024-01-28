package com.example.lifetracer.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.charts.ChartManager
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.ListItemInstanceBinding
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections

class InstanceAdapter(
    private val scope: CoroutineScope,
    private val viewModel: InstancesViewModel,
    private val onDeleteInstance: (InstanceWithTask) -> Unit,
    private val onFinishInstance: (InstanceWithTask) -> Unit,
    private val fetchChartData: suspend (Long) -> List<BarEntry>
) : ListAdapter<InstanceWithTask, InstanceAdapter.ViewHolder>(InstanceDiffCallback()),
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
            val instancesWithTask = currentList.map { it }
            viewModel.updateInstanceOrder(instancesWithTask)
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instanceWithTask  = getItem(position)

        holder.bind(instanceWithTask, fetchChartData, scope)
        holder.itemView.setOnClickListener {
            viewModel.selectAndStartInstance(instanceWithTask )
        }
    }

    class ViewHolder(private val binding: ListItemInstanceBinding,
                     private val onDeleteInstance: (InstanceWithTask) -> Unit,
                     private val onFinishInstance: (InstanceWithTask) -> Unit
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(instanceWithTask : InstanceWithTask, fetchChartData: suspend (Long) -> List<BarEntry>, scope: CoroutineScope) {
            binding.instanceWithTask = instanceWithTask

            scope.launch {
                //todo make Graph work
                // val barEntries = fetchChartData(instanceWithTask.instance.taskId)
                // binding.weekChartView.setWeekData(barEntries)
            }

            binding.buttonDeleteTask.setOnClickListener {
                onDeleteInstance(instanceWithTask )
            }

            binding.buttonFinishInstance.setOnClickListener {
                onFinishInstance(instanceWithTask )
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