package com.example.lifetracer.views

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lifetracer.R
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.ActivityMainBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.ListViewModel
import com.example.lifetracer.viewModel.ListViewModelFactory
import com.example.lifetracer.viewModel.SelectedInstanceViewModel
import com.example.lifetracer.viewModel.SelectedInstanceViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), RecyclerViewFragment.OnInstanceSelectedListener {

    private lateinit var binding: ActivityMainBinding

    // This is controlling the viewmodel and creationFragement
    val creationContext = "main"


    private val listViewModel: ListViewModel by viewModels {
        ListViewModelFactory(
            instanceRepository = InstanceRepository(
                instanceDao = AppDatabase.getDatabase(this).instanceDao()
            )
        )
    }

    private val selectedInstanceViewModel: SelectedInstanceViewModel by viewModels {
        SelectedInstanceViewModelFactory(
            instanceRepository = InstanceRepository(
                instanceDao = AppDatabase.getDatabase(this).instanceDao()
            )
        )
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use the binding class for the updated layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the data source to "main"
        listViewModel.selectDataSource(creationContext)

        // Add fragments if not already added
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.recyclerViewFragmentContainer, RecyclerViewFragment())
                .replace(R.id.selectedInstanceContainer, MainSelectedFragment())
                .commitNow()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        binding.buttonAddInstance.setOnClickListener {
            val taskCreationFragment = TaskCreationFragment.newInstance(context = creationContext).apply {
                setTaskCreationListener(object : TaskCreationFragment.TaskCreationListener {
                    override fun onInstanceCreated(subTask: InstanceWithTask) {
                        lifecycleScope.launch {
                            taskCreationViewModel.addInstance(subTask)
                        }
                    }
                })
            }
            taskCreationFragment.show(supportFragmentManager, "TaskCreationFragment")
        }

        binding.buttonViewFinishedTasks.setOnClickListener {
            startActivity(Intent(this, ReviewActivity::class.java))
        }

        binding.buttonGoToInstanceVault.setOnClickListener {
            startActivity(Intent(this, VaultActivity::class.java))
        }

        binding.buttonGoToTest.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }
    }
    override fun onInstanceSelected(instance: InstanceWithTask) {
        TODO("Not yet implemented")
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
