package com.example.lifetracer.views

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.Controller
import com.example.lifetracer.R
import com.example.lifetracer.data.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ManageTasksActivity : AppCompatActivity() {
    private lateinit var taskNameEditText: EditText
    private lateinit var taskQualityEditText: EditText
    private lateinit var taskRegularityEditText: EditText
    private lateinit var taskFixedCheckBox: CheckBox
    private lateinit var addTaskButton: Button
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val controller = Controller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_tasks)

        // Initialize UI elements
        taskNameEditText = findViewById(R.id.editTextTaskName)
        taskQualityEditText = findViewById(R.id.editTextTaskQuality)
        taskRegularityEditText = findViewById(R.id.editTextTaskRegularity)
        taskFixedCheckBox = findViewById(R.id.checkBoxTaskFixed)
        addTaskButton = findViewById(R.id.buttonAddTask)

        taskRecyclerView = findViewById(R.id.recyclerViewTasks)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(this, emptyList(), controller)
        taskRecyclerView.adapter = taskAdapter

        // Create a formatted date string
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Handle the "Add Task" button click
        addTaskButton.setOnClickListener {
            val name = taskNameEditText.text.toString()
            val quality = taskQualityEditText.text.toString()
            val currentDate = LocalDate.now()
            val dateOfCreation = currentDate.format(formatter)
            val regularity = taskRegularityEditText.text.toString().toIntOrNull() ?: 0
            val fixed = taskFixedCheckBox.isChecked

            preFillValues()

            if (name.isNotEmpty() && dateOfCreation.isNotEmpty()) {
                val task = Task(0, name, quality, dateOfCreation, regularity, fixed)
                Controller.addTaskAndInstance(task)
                updateTaskList()
                preFillValues()

                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateTaskList()
    }

    private fun updateTaskList() {
   // todo    val tasks = Controller.getAllTasks()
    //    taskAdapter.updateTasks(tasks)
    }

    private fun preFillValues() {
        taskNameEditText.setText("Task1")
        taskQualityEditText.setText("Good")
    }
}
