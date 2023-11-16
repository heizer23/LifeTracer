package com.example.lifetracer.views

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.R
import com.example.lifetracer.ViewModel.InstancesViewModel
import com.example.lifetracer.ViewModel.InstancesViewModelFactory
import com.example.lifetracer.data.Task
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private lateinit var viewModel: InstancesViewModel


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


        // Get the singleton database instance
        val database = AppDatabase.getDatabase(applicationContext)

        // Get DAOs from the database
        val instanceDao = database.instanceDao()
        val taskDao = database.taskDao()
        val instanceRepository = InstanceRepository(instanceDao, taskDao)

        // Get viewModel
        val factory = InstancesViewModelFactory(instanceRepository)
        viewModel = ViewModelProvider(this, factory).get(InstancesViewModel::class.java)


        taskAdapter = TaskAdapter(emptyList()) { task ->
            // Launch a coroutine in the ViewModel's scope
            viewModel.viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    instanceRepository.deleteTask(task)
                }
            }
        }

        // Observe LiveData from ViewModel and update the adapter
        viewModel.getAllTasks().observe(this, Observer { returnedTasks ->
            // Update your adapter's data
            taskAdapter.updateList(returnedTasks)
        })

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

            if (name.isNotEmpty()) {
                val task = Task(0, name, quality, dateOfCreation, regularity, fixed)

                lifecycleScope.launch {
                    viewModel.addTaskAndInstance(task)
                    updateTaskList()
                }

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
        viewModel.getAllTasks().observe(this, Observer { tasks ->
            taskAdapter.updateList(tasks)
        })
    }

    private fun preFillValues() {
        taskNameEditText.setText("Task1")
        taskQualityEditText.setText("Good")
    }
}
