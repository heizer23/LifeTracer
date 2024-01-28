package com.example.lifetracer.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifetracer.R
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.ActivityMainBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.viewModel.InstancesViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: InstancesViewModel by viewModels {
        InstancesViewModelFactory(
            instanceRepository = InstanceRepository(
                instanceDao = AppDatabase.getDatabase(applicationContext).instanceDao(),
            ),
            chartRepository = ChartRepository(AppDatabase.getDatabase(applicationContext).chartDataDao())
        )
    }

    private lateinit var instanceAdapter: InstanceAdapter
    private var selectedInstanceFragment: SelectedInstanceFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInstanceAdapter()
        setupRecyclerView()
        setupItemTouchHelper(binding.recyclerViewInstances, instanceAdapter)
        setupViewModelObserver()
        setupButtonClickListeners()
        attachSelectedInstanceFragment()
    }

    private fun setupInstanceAdapter() {
        instanceAdapter = InstanceAdapter(
            lifecycleScope,
            viewModel,
            onDeleteInstance = { instanceWithTask ->
                viewModel.viewModelScope.launch {
                    viewModel.deleteInstance(instanceWithTask)
                }
            },
            onFinishInstance = { instanceWithTask ->
                viewModel.finishInstance(instanceWithTask)
            },
            fetchChartData = { taskId ->
                viewModel.getChartData(taskId)
            }
        )
    }

    private fun setupRecyclerView() {
        binding.recyclerViewInstances.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = instanceAdapter
        }

        instanceAdapter.onItemClickListener = { instance ->
            selectedInstanceFragment?.updateSelectedView(instance)
        }
    }

    private fun setupViewModelObserver() {
        viewModel.allActiveInstanceWithTask.observe(this) { instanceWithHistoryList ->
            instanceAdapter.submitList(instanceWithHistoryList)
        }
    }

    private fun setupButtonClickListeners() {
        binding.buttonGoToManageTasks.setOnClickListener {
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

    private fun attachSelectedInstanceFragment() {
        selectedInstanceFragment = supportFragmentManager.findFragmentById(R.id.selectedInstanceContainer) as? SelectedInstanceFragment
        if (selectedInstanceFragment == null) {
            selectedInstanceFragment = SelectedInstanceFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.selectedInstanceContainer, selectedInstanceFragment!!)
                .commit()
        }
    }
}
