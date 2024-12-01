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
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.SubtaskActivityBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.SubTaskViewModelFactory
import com.example.lifetracer.views.ReusableAdapter

class ActivitySubTask : AppCompatActivity() {

    private lateinit var binding: SubtaskActivityBinding
    private lateinit var viewModel: SubTaskViewModel
    private lateinit var adapter: ReusableAdapter
    private var parentTaskId: Long = 0L // Retrieved from Intent

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
        viewModel = ViewModelProvider(
            this,
            SubTaskViewModelFactory(parentTaskId, instanceRepository)
        )[SubTaskViewModel::class.java]

        // Set up RecyclerView
        setupRecyclerView()

        // Observe subtasks LiveData
        viewModel.subtasks.observe(this) { subtasks ->
            adapter.submitList(subtasks)
        }

        // Add Subtask button listener
        binding.addSubtaskButton.setOnClickListener {
            showTaskCreationFragment()
        }
    }

    private fun setupRecyclerView() {
        adapter = ReusableAdapter(
            scope = lifecycleScope,
            viewModel = viewModel,
            onDeleteInstance = { subtask ->
                viewModel.deleteInstance(subtask)
                Toast.makeText(this, "Subtask deleted!", Toast.LENGTH_SHORT).show()
            },
            onRestoreOrFinishInstance = { subtask ->
                viewModel.updateInstance(subtask.copy(status = InstanceWithTask.STATUS_PLANNED))
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
                    viewModel.addSubtask(parentTaskId, instanceWithTask)
                    Toast.makeText(this@ActivitySubTask, "Subtask added!", Toast.LENGTH_SHORT).show()
                }
            })
        }
        taskCreationFragment.show(supportFragmentManager, "TaskCreationFragment")
    }
}
