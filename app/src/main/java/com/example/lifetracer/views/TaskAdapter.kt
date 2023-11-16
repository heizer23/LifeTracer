package com.example.lifetracer.views

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.R
import com.example.lifetracer.data.Task
import com.example.lifetracer.databinding.ListItemTaskBinding

class TaskAdapter(
    private var taskList: List<Task>,
    private val onDeleteTask: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = DataBindingUtil.inflate<ListItemTaskBinding>(
            LayoutInflater.from(parent.context), R.layout.list_item_task, parent, false
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

    fun updateList(newTaskList: List<Task>) {
        taskList = newTaskList
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(private val binding: ListItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.task = task
            binding.executePendingBindings()

            binding.buttonDeleteTask.setOnClickListener {
                // Lambda to have Databse deletion dealt with in the Activity
                onDeleteTask(task)
                //delete the
             //   taskList = taskList.toMutableList().apply { removeAt(adapterPosition) }
               // notifyItemRemoved(adapterPosition)
            }
        }
    }

}
