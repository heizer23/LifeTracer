package com.example.lifetracer.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifetracer.R
import com.example.lifetracer.databinding.ActivityMainBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.ViewModel.InstancesViewModel
import com.example.lifetracer.ViewModel.InstancesViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: InstancesViewModel by viewModels {
        InstancesViewModelFactory(InstanceRepository(
            AppDatabase.getDatabase(applicationContext).instanceDao(),
            AppDatabase.getDatabase(applicationContext).taskDao()
        ))
    }
    private val instanceAdapter: InstanceAdapter by lazy { InstanceAdapter(viewModel) }
    private var selectedInstanceFragment: SelectedInstanceFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupViewModelObserver()
        setupButtonClickListeners()
        attachSelectedInstanceFragment()
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
        viewModel.getAllInstances().observe(this, { instances ->
            instanceAdapter.submitList(instances)
        })
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
