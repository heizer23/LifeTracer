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
import com.example.lifetracer.databinding.ListItemVaultBinding
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections

class VaultAdapter(
    private val scope: CoroutineScope,
    private val viewModel: InstancesViewModel,
    private val onMoveTaskFromVault: (InstanceWithTask) -> Unit,
    private val onDeleteInstance: (InstanceWithTask) -> Unit
) : ListAdapter<InstanceWithTask, VaultAdapter.ViewHolder>(InstanceDiffCallback()),
    CoroutineScope by CoroutineScope(Dispatchers.Main)  {

    var onItemClickListener: ((InstanceWithTask) -> Unit)? = null
    var job: Job? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemVaultBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding, onMoveTaskFromVault, onDeleteInstance)
    }




    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instanceWithTask  = getItem(position)

        holder.bind(instanceWithTask, scope)
        holder.itemView.setOnClickListener {
            onMoveTaskFromVault(instanceWithTask )
        }
    }

    class ViewHolder(private val binding: ListItemVaultBinding,
                     private val onMoveTaskFromVault: (InstanceWithTask) -> Unit,
                     private val onDeleteInstance: (InstanceWithTask) -> Unit
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(instanceWithTask : InstanceWithTask, scope: CoroutineScope) {
            binding.instanceWithTask = instanceWithTask

            scope.launch {
                //todo make Graph work
                // val barEntries = fetchChartData(instanceWithTask.instance.taskId)
                // binding.weekChartView.setWeekData(barEntries)
            }

            binding.buttonDeleteTask.setOnClickListener { // Add delete button functionality
                onDeleteInstance(instanceWithTask)
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