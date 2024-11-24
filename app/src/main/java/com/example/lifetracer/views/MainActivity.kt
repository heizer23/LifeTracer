package com.example.lifetracer.views

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private lateinit var reusableAdapter: ReusableAdapter
    private var mainSelectedFragment: MainSelectedFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        reusableAdapter = ReusableAdapter(
            scope = lifecycleScope,
            viewModel = viewModel,
            onDeleteInstance = { instance -> viewModel.deleteInstance(instance) },
            onRestoreOrFinishInstance = { instance -> viewModel.finishInstance(instance) },
            useVaultLayout = false
        )

        setupRecyclerView()
        ReusableAdapterUtil.attachItemTouchHelper(binding.recyclerViewInstances, reusableAdapter)
        setupViewModelObserver()
        setupButtonClickListeners()
        attachSelectedInstanceFragment()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewInstances.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = reusableAdapter
        }


    }

    private fun setupViewModelObserver() {
        viewModel.allActiveInstanceWithTask.observe(this) { instanceWithHistoryList ->
            reusableAdapter.submitList(instanceWithHistoryList)
        }
    }

       private fun setupButtonClickListeners() {
        binding.buttonGoToManageTasks.setOnClickListener {
            val taskCreationFragment = TaskCreationFragment.newInstance().apply {
                setTaskCreationListener(object : TaskCreationFragment.TaskCreationListener {
                    override fun onInstanceCreated(subTask: InstanceWithTask) {
                        lifecycleScope.launch {
                            viewModel.addInstance(subTask)
                        }
                    }
                })
            }
            taskCreationFragment.show(supportFragmentManager, "TaskCreationFragment")
        }

        binding.buttonGoToInstanceVault.setOnClickListener {
            startActivity(Intent(this, VaultActivity::class.java))
        }

        binding.buttonViewFinishedTasks.setOnClickListener {
            startActivity(Intent(this, ReviewActivity::class.java))
        }
    }

    private fun attachSelectedInstanceFragment() {
        mainSelectedFragment = supportFragmentManager.findFragmentById(R.id.selectedInstanceContainer) as? MainSelectedFragment
            ?: MainSelectedFragment().also {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.selectedInstanceContainer, it)
                    .commit()
                mainSelectedFragment = it
            }
    }
}
