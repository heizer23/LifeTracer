package com.example.lifetracer.views

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.R
import com.example.lifetracer.data.Task
import com.example.lifetracer.databinding.ListItemTaskBinding

class TaskAdapter(
    private val context: Context,
    private var taskList: LiveData<List<Task>>,
    private val taskController: Controller
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = DataBindingUtil.inflate<ListItemTaskBinding>(
            layoutInflater, R.layout.list_item_task, parent, false
        )
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task)
    }

    fun updateTasks(newTaskList: List<Task>) {
        taskList = newTaskList
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(private val binding: ListItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.task = task
            binding.executePendingBindings()

            binding.buttonDeleteTask.setOnClickListener {
                Controller.deleteTask(task)
                deleteTask(adapterPosition)
            }
        }
    }

    private fun deleteTask(position: Int) {
        if (position >= 0 && position < taskList.size) {
            taskList = taskList.toMutableList().apply { removeAt(position) }
            notifyItemRemoved(position)
        }
    }
}
