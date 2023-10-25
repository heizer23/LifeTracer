package com.example.lifetracer


import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var taskNameEditText: EditText
    private lateinit var taskQualityEditText: EditText
    private lateinit var taskDateOfCreationEditText: EditText
    private lateinit var taskRegularityEditText: EditText
    private lateinit var taskFixedCheckBox: CheckBox
    private lateinit var addTaskButton: Button
    private lateinit var taskListView: ListView
    private lateinit var taskAdapter: TaskAdapter
    private val controller = TaskController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        taskNameEditText = findViewById(R.id.editTextTaskName)
        taskQualityEditText = findViewById(R.id.editTextTaskQuality)
        taskDateOfCreationEditText = findViewById(R.id.editTextTaskDateOfCreation)
        taskRegularityEditText = findViewById(R.id.editTextTaskRegularity)
        taskFixedCheckBox = findViewById(R.id.checkBoxTaskFixed)
        addTaskButton = findViewById(R.id.buttonAddTask)
        taskListView = findViewById(R.id.listViewTasks)
        taskAdapter = TaskAdapter(this, controller.getAllTasks(), controller)
        taskListView.adapter = taskAdapter

        // Handle the "Add Task" button click
        addTaskButton.setOnClickListener {
            val name = taskNameEditText.text.toString()
            val quality = taskQualityEditText.text.toString()
            val dateOfCreation = taskDateOfCreationEditText.text.toString()
            val regularity = taskRegularityEditText.text.toString().toIntOrNull() ?: 0
            val fixed = taskFixedCheckBox.isChecked

            if (name.isNotEmpty() && dateOfCreation.isNotEmpty()) {
                val task = Task(0, name, quality, dateOfCreation, regularity, fixed)
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
        taskQualityEditText.text.clear()
        taskDateOfCreationEditText.text.clear()
        taskRegularityEditText.text.clear()
        taskFixedCheckBox.isChecked = false
    }
}