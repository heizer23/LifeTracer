package com.example.lifetracer.views

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lifetracer.R
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.ActivityInstanceVaultBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.ListViewModel
import com.example.lifetracer.viewModel.ListViewModelFactory
import kotlinx.coroutines.launch

class ActivitySubTask : AppCompatActivity(), RecyclerViewFragment.OnInstanceSelectedListener {

    private lateinit var binding: ActivityInstanceVaultBinding
    private var parentId: Long = -1L // Default invalid value

    private val listViewModel: ListViewModel by viewModels {
        ListViewModelFactory(
            instanceRepository = InstanceRepository(
                instanceDao = AppDatabase.getDatabase(applicationContext).instanceDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentId = intent.getLongExtra("PARENT_TASK_ID", -1L)
        if (parentId == -1L) {
            finish() // Exit if the parentId is invalid
            return
        }

        listViewModel.selectDataSource("sub", context = parentId.toString())

        // Initialize binding
        binding = ActivityInstanceVaultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.recyclerViewContainer, RecyclerViewFragment())
                .replace(R.id.detailViewContainer, MainSelectedFragment())
                .commitNow()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.buttonAddInstance.setOnClickListener {
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
    }

    override fun onInstanceSelected(instance: InstanceWithTask) {
        val detailFragment = supportFragmentManager.findFragmentById(R.id.detailViewContainer) as? MainSelectedFragment
        detailFragment?.updateUi(instance)
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
