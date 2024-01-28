package com.example.lifetracer.views

import TaskCreationFragment
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.databinding.InstanceDetailBinding
import com.example.lifetracer.viewModel.InstanceDetailViewModel
import com.example.lifetracer.viewModel.InstanceDetailViewModelFactory

class InstanceDetailActivity : AppCompatActivity() {

    private lateinit var binding: InstanceDetailBinding
    private lateinit var viewModel: InstanceDetailViewModel
    private var parentTaskId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get instance ID passed from previous activity
        val instanceId = intent.getLongExtra("INSTANCE_ID_EXTRA", -1)
        if (instanceId == -1L) {
            // Handle the error, instanceId should never be -1
            finish()
            return
        }

        // Initialize ViewModel here with the retrieved instanceId
        viewModel = ViewModelProvider(this, InstanceDetailViewModelFactory(
            instanceId,
            instanceRepository = InstanceRepository(
                instanceDao = AppDatabase.getDatabase(applicationContext).instanceDao(),
            ),
            chartRepository = ChartRepository(AppDatabase.getDatabase(applicationContext).chartDataDao())
        )).get(InstanceDetailViewModel::class.java)

        viewModel.currentInstance.observe(this, Observer { instanceWithTask ->
            parentTaskId = instanceWithTask.id
            // Now parentTaskId will be updated whenever currentInstance changes
        })


        binding = InstanceDetailBinding.inflate(layoutInflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setContentView(binding.root)

        // Enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Fetch and display instance details
        fetchInstanceDetails(instanceId)

        // Set up the add task button listener
        binding.addTaskButton.setOnClickListener {

            val taskCreationFragment = TaskCreationFragment.newInstance().apply {
                setTaskCreationListener(object : TaskCreationFragment.TaskCreationListener {
                    override fun onInstanceCreated(subTask: InstanceWithTask) {
                        // Handle the created subtask, linking it to the parent task
                      //  subTask.parentTaskId = parentTaskId
                        // Proceed with saving the subtask or whatever else needs to be done
                    }
                })
            }
            taskCreationFragment.show(supportFragmentManager, "TaskCreationFragment")
        }
    }

    private fun fetchInstanceDetails(instanceId: Long) {}

    private fun onAddTaskClicked(instanceId: Long) {
        // Implement the logic to add a task to this instance
        // You might start another activity or show a dialog here
    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Respond to the action bar's Up/Home button
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
