package com.example.lifetracer.views

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.viewModel.InstancesViewModelFactory
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.ActivityManageTasksBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class ManageTasksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageTasksBinding

    private val viewModel: InstancesViewModel by viewModels {
        InstancesViewModelFactory(
            instanceRepository = InstanceRepository(
                instanceDao = AppDatabase.getDatabase(applicationContext).instanceDao(),
            ),
            chartRepository = ChartRepository(AppDatabase.getDatabase(applicationContext).chartDataDao())
        )
    }


    private lateinit var instanceAdapter: InstanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupRecyclerView()
        observeInstanceListChanges()
        binding.buttonAddTask.setOnClickListener { handleAddInstance() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        //todo adapter
/*        instanceAdapter = InstanceAdapter(
            lifecycleScope,
            instance = emptyList(),
            onDeleteInstance = { instance ->
                deleteInstance(instance)
            },
            onInstanceAction = { instance ->
                onInstanceActionExecution(instance)
            },
            fetchChartData = { instanceId ->
                viewModel.getChartData(instanceId)
            }
        )
        binding.recyclerViewInstances.apply {
            layoutManager = LinearLayoutManager(this@ManageTasksActivity)
            adapter = instanceAdapter
        }*/
    }

    private fun onInstanceActionExecution(instance: InstanceWithTask) {
        CoroutineScope(Dispatchers.IO).launch {

        }
    }

    private fun observeInstanceListChanges() {
        //todo adapter
/*        viewModel.getAllInstances().observe(this) { instances ->
            instanceAdapter.updateList(instances)
        }*/
    }

    private fun handleAddInstance() {
        val instance = createInstanceFromInput()
        if (instance != null) {
            addNewInstance(instance)
        }
    }

    private fun createInstanceFromInput(): InstanceWithTask? {
        // todo Gather input data and create an Instance object
        return null
    }

    private fun addNewInstance(instance: InstanceWithTask) {
        CoroutineScope(Dispatchers.Main).launch {
/*            viewModel.addInstance(instance)
            updateInstanceList()*/
        }
    }

    private fun deleteInstance(instance: InstanceWithTask) {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.deleteInstance(instance)
        }
    }

    private fun updateInstanceList() {
/*        viewModel.getAllInstances().observe(this) { instances ->
            instanceAdapter.updateList(instances)
        }*/
    }
}
