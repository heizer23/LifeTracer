
package com.example.lifetracer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class TaskAdapter(private val context: Context, private var taskList: List<Task>,private val taskController: TaskController) : BaseAdapter() {

    override fun getCount(): Int {
        return taskList.size
    }

    override fun getItem(position: Int): Task {
        return taskList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_task, parent, false)
        val task = getItem(position)

        val nameTextView = view.findViewById<TextView>(R.id.textTaskName)
        val qualityTextView = view.findViewById<TextView>(R.id.textTaskQuality)
        val dateOfCreationTextView = view.findViewById<TextView>(R.id.textTaskDateOfCreation)
        val regularityTextView = view.findViewById<TextView>(R.id.textTaskRegularity)
        val fixedTextView = view.findViewById<TextView>(R.id.textTaskFixed)

        nameTextView.text = task.name
        qualityTextView.text = task.quality
        dateOfCreationTextView.text = task.dateOfCreation
        regularityTextView.text = task.regularity.toString()
        fixedTextView.text = if (task.fixed) "Yes" else "No"

        val deleteButton = view.findViewById<Button>(R.id.buttonDeleteTask)
        deleteButton.setOnClickListener {
            // Handle the delete action here
            // You can call a method to delete the task from the list or database
            taskController.deleteTask(task)
            deleteTask(position)
        }


        return view
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