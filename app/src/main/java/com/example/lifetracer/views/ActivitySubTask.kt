package com.example.lifetracer.views

import SubTaskViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifetracer.R
import com.example.lifetracer.Utilities.Mode
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.SubtaskActivityBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.viewModel.InstancesViewModelFactory
import com.example.lifetracer.viewModel.SubTaskViewModelFactory
import com.example.lifetracer.views.ReusableAdapter

class ActivitySubTask : AppCompatActivity() {

    private lateinit var binding: SubtaskActivityBinding
    private lateinit var subTaskViewModel: SubTaskViewModel
    private lateinit var instancesViewModel: InstancesViewModel
    private lateinit var adapter: ReusableAdapter
    private var parentTaskId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SubtaskActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve parent task ID from Intent
        parentTaskId = intent.getLongExtra("PARENT_TASK_ID", 0L)

        if (parentTaskId == 0L) {
            Toast.makeText(this, "Invalid Parent Task ID", Toast.LENGTH_SHORT).show()
            finish() // Exit if parentTaskId is invalid
            return
        }

        // Initialize ViewModel
        val database = AppDatabase.getDatabase(applicationContext)
        val instanceRepository = InstanceRepository(database.instanceDao())

        subTaskViewModel = ViewModelProvider(
            this,
            SubTaskViewModelFactory(parentTaskId, instanceRepository)
        )[SubTaskViewModel::class.java]

        // Initialize InstancesViewModel
        val chartRepository = ChartRepository(AppDatabase.getDatabase(applicationContext).chartDataDao())
        val instancesFactory = InstancesViewModelFactory(instanceRepository, chartRepository)
        instancesViewModel = ViewModelProvider(this, instancesFactory)[InstancesViewModel::class.java]


        // Attach the MainSelectedFragment for the lowest-priority subtask
        attachSelectedSubTaskFragment()

        // Set up RecyclerView
        setupRecyclerView()

        // Observe subtasks LiveData
        subTaskViewModel.subtasks.observe(this) { subtasks ->
            adapter.submitList(subtasks)
        }

        // Add Subtask button listener
        binding.addSubtaskButton.setOnClickListener {
            showTaskCreationFragment()
        }
    }

    private fun attachSelectedSubTaskFragment() {
        val subTaskFragment = supportFragmentManager.findFragmentById(R.id.subtaskFragmentContainer) as? MainSelectedFragment
            ?: MainSelectedFragment.newInstance(Mode.SUBTASKS, parentTaskId).also {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.subtaskFragmentContainer, it)
                    .commit()
            }
    }

    private fun setupRecyclerView() {
        adapter = ReusableAdapter(
            scope = lifecycleScope,
            viewModel = subTaskViewModel,
            onDeleteInstance = { subtask ->
                subTaskViewModel.deleteInstance(subtask)
                Toast.makeText(this, "Subtask deleted!", Toast.LENGTH_SHORT).show()
            },
            onRestoreOrFinishInstance = { subtask ->
                subTaskViewModel.updateInstance(subtask.copy(status = InstanceWithTask.STATUS_PLANNED))
                Toast.makeText(this, "Subtask moved back to planned!", Toast.LENGTH_SHORT).show()
            },
            onCircleClick = { instance ->
                val intent = Intent(this, ActivitySubTask::class.java).apply {
                    putExtra("PARENT_TASK_ID", instance.id)
                }
                startActivity(intent)
            },
            useVaultLayout = true // Reuse the vault layout for subtasks
        )

        binding.subtaskRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.subtaskRecyclerView.adapter = adapter
    }


    private fun showTaskCreationFragment() {
        val taskCreationFragment = TaskCreationFragment.newInstance(parentTaskId).apply {
            setTaskCreationListener(object : TaskCreationFragment.TaskCreationListener {
                override fun onInstanceCreated(instanceWithTask: InstanceWithTask) {
                    // Add the new subtask to the database via the ViewModel
                    subTaskViewModel.addSubtask(parentTaskId, instanceWithTask)
                    Toast.makeText(this@ActivitySubTask, "Subtask added!", Toast.LENGTH_SHORT).show()
                }
            })
        }
        taskCreationFragment.show(supportFragmentManager, "TaskCreationFragment")
    }
}
