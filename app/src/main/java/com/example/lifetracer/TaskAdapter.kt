package com.example.lifetracer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class TaskAdapter(private val context: Context, private var taskList: List<Task>) : BaseAdapter() {

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
        val dateTextView = view.findViewById<TextView>(R.id.textTaskDate)

        nameTextView.text = task.name
        dateTextView.text = task.date

        return view
    }

    fun updateTasks(newTaskList: List<Task>) {
        taskList = newTaskList
        notifyDataSetChanged()
    }
}
