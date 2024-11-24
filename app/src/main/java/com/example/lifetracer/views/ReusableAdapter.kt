package com.example.lifetracer.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.databinding.ListItemVaultBinding
import com.example.lifetracer.viewModel.InstancesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.ListItemMainBinding

class ReusableAdapter(
    private val scope: CoroutineScope,
    private val viewModel: InstancesViewModel,
    val onDeleteInstance: (InstanceWithTask) -> Unit,
    val onRestoreOrFinishInstance: (InstanceWithTask) -> Unit,
    private val useVaultLayout: Boolean = false // Flag to decide layout
) : ListAdapter<InstanceWithTask, ReusableAdapter.ViewHolder>(InstanceDiffCallback()),
    CoroutineScope by CoroutineScope(Dispatchers.Main) {

    var job: Job? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (useVaultLayout) {
            val binding = ListItemVaultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(vaultBinding = binding, onDeleteInstance = onDeleteInstance, onRestoreOrFinishInstance = onRestoreOrFinishInstance)
        } else {
            val binding = ListItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(instanceBinding = binding, onDeleteInstance = onDeleteInstance, onRestoreOrFinishInstance = onRestoreOrFinishInstance)
        }
    }


    fun onItemMove(fromPosition: Int, toPosition: Int) {
        val currentList = currentList.toMutableList()
        Collections.swap(currentList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun onDragEnded() {
        job?.cancel()
        job = launch {
            delay(500) // Add delay for smoother experience
            val updatedList = currentList.map { it }
            viewModel.updateInstanceOrder(updatedList)
            submitList(updatedList) // Update RecyclerView
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instanceWithTask = getItem(position)
        holder.bind(instanceWithTask, scope)

        holder.itemView.setOnClickListener {
            viewModel.selectAndStartInstance(instanceWithTask )
        }

    }

    class ViewHolder(
        private val vaultBinding: ListItemVaultBinding? = null,
        private val instanceBinding: ListItemMainBinding? = null,
        private val onDeleteInstance: (InstanceWithTask) -> Unit,
        private val onRestoreOrFinishInstance: (InstanceWithTask) -> Unit,
    ) : RecyclerView.ViewHolder(vaultBinding?.root ?: instanceBinding?.root!!) {
        fun bind(instanceWithTask: InstanceWithTask, scope: CoroutineScope) {
            vaultBinding?.let { binding ->
                binding.instanceWithTask = instanceWithTask
                binding.executePendingBindings()
            }
            instanceBinding?.let { binding ->
                binding.instanceWithTask = instanceWithTask
                scope.launch {
                    // Fetch and display chart data if needed
                }
                binding.buttonDeleteTask.setOnClickListener {
                    onDeleteInstance(instanceWithTask)
                }
                binding.buttonFinishInstance.setOnClickListener {
                    onRestoreOrFinishInstance(instanceWithTask)
                }
                binding.executePendingBindings()
            }
        }
    }

    class InstanceDiffCallback : DiffUtil.ItemCallback<InstanceWithTask>() {
        override fun areItemsTheSame(oldItem: InstanceWithTask, newItem: InstanceWithTask): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InstanceWithTask, newItem: InstanceWithTask): Boolean {
            return oldItem == newItem
        }
    }
}
