package com.example.lifetracer.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.ListItemTaskBinding
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TaskAdapter(
    private val scope: CoroutineScope,
    private var tasks: List<InstanceWithTask>,
    private val onDeleteTask: (InstanceWithTask) -> Unit,
    private val onTaskAction: (InstanceWithTask) -> Unit,
    private val fetchChartData: suspend (Long) -> List<BarEntry>
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ListItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task, fetchChartData, scope)
    }

    fun updateList(newTasks: List<InstanceWithTask>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(private val binding: ListItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.buttonDeleteTask.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteTask(tasks[position])
                }
            }
            binding.buttonAddInstance.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTaskAction(tasks[position])
                }
            }
        }

        fun bind(task: InstanceWithTask, fetchChartData: suspend (Long) -> List<BarEntry>, scope: CoroutineScope) {
            binding.instanceWithTask = task

            scope.launch {
                //todo Chart
/*                val barEntries = fetchChartData(task.id)
                binding.weekChartView.setWeekData(barEntries)*/
            }
            binding.executePendingBindings()
        }
    }
}
