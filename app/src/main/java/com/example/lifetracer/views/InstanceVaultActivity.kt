package com.example.lifetracer.views

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.R
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.viewModel.InstancesViewModelFactory
import kotlinx.coroutines.launch
import com.example.lifetracer.data.InstanceWithTask

class InstanceVaultActivity : AppCompatActivity() {

    private lateinit var adapter: VaultAdapter


    private val viewModel: InstancesViewModel by viewModels {
        InstancesViewModelFactory(
            instanceRepository = InstanceRepository(
                instanceDao = AppDatabase.getDatabase(applicationContext).instanceDao(),
            ),
            chartRepository = ChartRepository(AppDatabase.getDatabase(applicationContext).chartDataDao())
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instance_vault) // Use setContentView for non-DataBinding activities

        setupRecyclerView()
        loadInstances()

        // Enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<Button>(R.id.buttonGoToManageTasks).setOnClickListener {
            val taskCreationFragment = TaskCreationFragment.newInstance().apply {
                setTaskCreationListener(object : TaskCreationFragment.TaskCreationListener {
                    override fun onInstanceCreated(subTask: InstanceWithTask) {
                        lifecycleScope.launch {
                            viewModel.addInstance(subTask)
                            Toast.makeText(applicationContext, "Instance added successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
            taskCreationFragment.show(supportFragmentManager, "TaskCreationFragment")
        }


    }


    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.vaultRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Assuming viewModel is already initialized and provides a way to fetch instances
        // and has a method to handle the selection/start of an instance.

        // Initialize your VaultAdapter with necessary dependencies.
        adapter = VaultAdapter(
            lifecycleScope,
            viewModel,
            onMoveTaskFromVault = { instance ->
                lifecycleScope.launch {
                    viewModel.moveTaskFromVaultToMain(instance)
                    Toast.makeText(applicationContext, "Instance copied successfully", Toast.LENGTH_SHORT).show()
                }
            },
            onDeleteInstance = { instance -> // Handle delete action
                lifecycleScope.launch {
                    viewModel.deleteInstance(instance)
                    Toast.makeText(applicationContext, "Instance deleted successfully", Toast.LENGTH_SHORT).show()
                }
            }
        )

        recyclerView.adapter = adapter

        // Fetch instances and submit them to the adapter.
        // This is a simplified approach. You'd typically observe a LiveData<List<InstanceWithTask>> from your ViewModel.
        viewModel.allVaultedInstance.observe(this) { instances ->
            adapter.submitList(instances)
        }
    }


    private fun loadInstances() {
        // Fetch instances from your database or viewModel and update the adapter
        // This is a simplified example. In a real app, this would involve fetching data from a ViewModel or Repository.
       // val instances = listOf<InstanceWithTask>() // Get your list of instances here
       // adapter.updateData(instances)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Respond to the action bar's Up/Home button
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
