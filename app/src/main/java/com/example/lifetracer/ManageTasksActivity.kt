package com.example.lifetracer

import android.content.Intent
import android.os.Bundle


import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ManageTasksActivity : AppCompatActivity() {
    private lateinit var taskNameEditText: EditText
    private lateinit var taskQualityEditText: EditText
    private lateinit var taskDateOfCreationEditText: EditText
    private lateinit var taskRegularityEditText: EditText
    private lateinit var taskFixedCheckBox: CheckBox
    private lateinit var addTaskButton: Button
    private lateinit var taskListView: ListView
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


        taskListView = findViewById(R.id.listViewTasks)

        // Initialize TaskAdapter with an empty list of tasks
        taskAdapter = TaskAdapter(this, emptyList(), controller)
        taskListView.adapter = taskAdapter


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
               controller.addTaskAndInstance(task)
                updateTaskList()
                preFillValues()


                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                // Finish the ManageTasksActivity to remove it from the back stack
                finish()

               // clearInputFields()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Update the task list when the activity resumes
        val tasks = controller.getAllTasks()
        taskAdapter.updateTasks(tasks)
    }

    private fun updateTaskList() {
        val tasks = controller.getAllTasks()
        taskAdapter.updateTasks(tasks)
    }

    private fun clearInputFields() {
        taskNameEditText.text.clear()
        taskQualityEditText.text.clear()
        taskDateOfCreationEditText.text.clear()
        taskRegularityEditText.text.clear()
        taskFixedCheckBox.isChecked = false
    }


    private fun preFillValues(){
        taskNameEditText.setText("Task1")
        taskQualityEditText.setText("Good")
    }

}