package com.example.lifetracer

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var taskNameEditText: EditText
    private lateinit var taskDateEditText: EditText
    private lateinit var addTaskButton: Button
    private lateinit var taskListView: ListView
    private lateinit var taskAdapter: TaskAdapter
    private val controller = TaskController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        taskNameEditText = findViewById(R.id.editTextTaskName)
        taskDateEditText = findViewById(R.id.editTextTaskDate)
        addTaskButton = findViewById(R.id.buttonAddTask)
        taskListView = findViewById(R.id.listViewTasks)
        taskAdapter = TaskAdapter(this, controller.getAllTasks())
        taskListView.adapter = taskAdapter

        // Handle the "Add Task" button click
        addTaskButton.setOnClickListener {
            val name = taskNameEditText.text.toString()
            val date = taskDateEditText.text.toString()

            if (name.isNotEmpty() && date.isNotEmpty()) {
                val task = Task(name, date)
                controller.addTask(task)
                updateTaskList()
                clearInputFields()
            }
        }
    }

    private fun updateTaskList() {
        taskAdapter.updateTasks(controller.getAllTasks())
    }

    private fun clearInputFields() {
        taskNameEditText.text.clear()
        taskDateEditText.text.clear()
    }
}
