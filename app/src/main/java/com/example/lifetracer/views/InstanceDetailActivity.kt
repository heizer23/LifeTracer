package com.example.lifetracer.views

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.databinding.InstanceDetailBinding
import com.example.lifetracer.viewModel.InstanceDetailViewModel
import com.example.lifetracer.viewModel.InstanceDetailViewModelFactory
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.viewModel.InstancesViewModelFactory

class InstanceDetailActivity : AppCompatActivity() {

    private lateinit var binding: InstanceDetailBinding
    private lateinit var viewModel: InstanceDetailViewModel

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
                taskDao = AppDatabase.getDatabase(applicationContext).taskDao()
            ),
            chartRepository = ChartRepository(AppDatabase.getDatabase(applicationContext).chartDataDao())
        )).get(InstanceDetailViewModel::class.java)

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
            // Implement logic to add a task
            onAddTaskClicked(instanceId)
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
