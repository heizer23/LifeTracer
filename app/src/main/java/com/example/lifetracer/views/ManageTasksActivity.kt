package com.example.lifetracer.views

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifetracer.R
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.ViewModel.InstancesViewModel
import com.example.lifetracer.ViewModel.InstancesViewModelFactory
import com.example.lifetracer.data.Task
import com.example.lifetracer.data.TaskFilter
import com.example.lifetracer.databinding.ActivityManageTasksBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ManageTasksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageTasksBinding

    private val viewModel: InstancesViewModel by viewModels {
        InstancesViewModelFactory(InstanceRepository(
            AppDatabase.getDatabase(applicationContext).instanceDao(),
            AppDatabase.getDatabase(applicationContext).taskDao()
        ))
    }
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupRecyclerView()
        observeTaskListChanges()
        setupAddTaskButtonListener()
        someFunctionToSetFilter()
    }

    private fun someFunctionToSetFilter() {
        // Assuming you have determined the filter criteria
        val filter = TaskFilter(regularities = listOf(1, 2, 3))
        viewModel.setTaskFilter(filter)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle the back button action
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(emptyList()) { task ->
            deleteTask(task)
        }
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(this@ManageTasksActivity)
            adapter = taskAdapter
        }
    }

    private fun observeTaskListChanges() {
        viewModel.getAllTasks().observe(this) { tasks ->
            taskAdapter.updateList(tasks)
        }
    }

    private fun setupAddTaskButtonListener() {
        binding.buttonAddTask.setOnClickListener { handleAddTask() }
    }

    private fun handleAddTask() {
        val task = createTaskFromInput()
        if (task != null) {
            addNewTask(task)
            preFillValues()
            finish()
        }
    }

    private fun createTaskFromInput(): Task? {
        val name = binding.editTextTaskName.text.toString()
        val quality = binding.editTextTaskQuality.text.toString()
        val regularity = binding.editTextTaskRegularity.text.toString().toIntOrNull() ?: 0
        val fixed = binding.checkBoxTaskFixed.isChecked

        return if (name.isNotEmpty()) {
            val dateOfCreation = getCurrentDate()
            Task(0, name, quality, dateOfCreation, regularity, fixed)
        } else {
            null
        }
    }


    private fun addNewTask(task: Task) {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.addTaskAndInstance(task)
            updateTaskList()
        }
    }

    private fun deleteTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.deleteTask(task)
        }
    }

    private fun updateTaskList() {
        viewModel.getAllTasks().observe(this) { tasks ->
            taskAdapter.updateList(tasks)
        }
    }

    private fun preFillValues() {
        binding.editTextTaskName.setText("Task1")
        binding.editTextTaskQuality.setText("Good")
    }
}
