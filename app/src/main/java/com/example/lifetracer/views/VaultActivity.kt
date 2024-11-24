package com.example.lifetracer.views

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
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

class VaultActivity : AppCompatActivity() {

    private lateinit var adapter: ReusableAdapter

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
        setContentView(R.layout.activity_instance_vault)

        // Initialize RecyclerView and Adapter
        val recyclerView = findViewById<RecyclerView>(R.id.vaultRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReusableAdapter(
            scope = lifecycleScope,
            viewModel = viewModel,
            onDeleteInstance = { instance -> viewModel.deleteInstance(instance) },
            onRestoreOrFinishInstance = { instance -> viewModel.moveTaskToMain(instance, false) },
            useVaultLayout = true
        )

        recyclerView.adapter = adapter

        // Set up ItemTouchHelper for drag-and-swipe functionality
        ReusableAdapterUtil.attachItemTouchHelper(recyclerView, adapter)

        // Load instances
        loadInstances()

        // Enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Button listener for managing tasks
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

    private fun loadInstances() {
        viewModel.allVaultedInstance.observe(this) { instances ->
            adapter.submitList(instances) // This updates the adapter's list
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
