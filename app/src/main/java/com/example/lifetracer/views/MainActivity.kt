package com.example.lifetracer.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifetracer.R
import com.example.lifetracer.databinding.ActivityMainBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.viewModel.InstancesViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: InstancesViewModel by viewModels {
        InstancesViewModelFactory(InstanceRepository(
            AppDatabase.getDatabase(applicationContext).instanceDao(),
            AppDatabase.getDatabase(applicationContext).taskDao()
        ))
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
            viewModel,
            onDeleteInstance = { instanceWithTask ->
                viewModel.viewModelScope.launch {
                    viewModel.deleteInstance(instanceWithTask.instance)
                }
            },
            onFinishInstance = { instanceWithTask ->
                viewModel.finishSelectedInstance(instanceWithTask)
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
        viewModel.getAllInstances().observe(this) { instances ->
            instanceAdapter.submitList(instances)
        }
    }

    private fun setupButtonClickListeners() {
        binding.buttonGoToManageTasks.setOnClickListener {
            startActivity(Intent(this, ManageTasksActivity::class.java))
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
