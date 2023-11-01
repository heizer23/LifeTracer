
package com.example.lifetracer
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val context: Context,
    private var taskList: List<Task>,
    private val taskController: Controller
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textTaskName)
        val qualityTextView: TextView = itemView.findViewById(R.id.textTaskQuality)
        val dateOfCreationTextView: TextView = itemView.findViewById(R.id.textTaskDateOfCreation)
        val regularityTextView: TextView = itemView.findViewById(R.id.textTaskRegularity)
        val fixedTextView: TextView = itemView.findViewById(R.id.textTaskFixed)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_task, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]

        holder.nameTextView.text = task.name
        holder.qualityTextView.text = task.taskQuality
        holder.dateOfCreationTextView.text = task.dateOfCreation
        holder.regularityTextView.text = task.regularity.toString()
        holder.fixedTextView.text = if (task.fixed) "Yes" else "No"

        holder.deleteButton.setOnClickListener {
            // Handle the delete action here
            // You can call a method to delete the task from the list or database
            taskController.deleteTask(task)
            deleteTask(position)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    // Function to delete a task from the list
    private fun deleteTask(position: Int) {
        if (position >= 0 && position < taskList.size) {
            taskList = taskList.toMutableList().apply { removeAt(position) }
            notifyDataSetChanged()
        }
    }

    fun updateTasks(newTaskList: List<Task>) {
        taskList = newTaskList
        notifyDataSetChanged()
    }
}
