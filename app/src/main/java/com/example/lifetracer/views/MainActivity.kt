package com.example.lifetracer.views

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
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
import com.example.lifetracer.viewModel.TaskScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnSubTaskRequestedListener {

    private lateinit var binding: ActivityMainBinding

    private val listViewModel: ListViewModel by viewModels {
        ListViewModelFactory(
            InstanceRepository(AppDatabase.getDatabase(this).instanceDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize spinner
        setupSpinner()

        // Load default fragments
        if (savedInstanceState == null) {
            loadFragments()
        }

        // Add task button logic
        binding.buttonAddInstance.setOnClickListener {
            // Determine the current context and parentId
            val currentScope = listViewModel.taskScope.value ?: TaskScope.Main
            val parentId = if (currentScope is TaskScope.Sub) currentScope.parentId else null

            // Create TaskCreationFragment
            val taskCreationFragment = TaskCreationFragment.newInstance(taskScope = currentScope).apply {
                setTaskCreationListener(object : TaskCreationFragment.TaskCreationListener {
                    override fun onInstanceCreated(subTask: InstanceWithTask) {
                        lifecycleScope.launch {
                            taskCreationViewModel.addInstance(subTask, currentScope, parentId)// Pass parentId for Sub context
                        }
                    }
                })
            }
            taskCreationFragment.show(supportFragmentManager, "TaskCreationFragment")
        }
    }

    private fun setupSpinner() {
        val spinner: Spinner = binding.contextSpinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedContext = when (parent.getItemAtPosition(position).toString()) {
                    "Main" -> TaskScope.Main
                    "Vault" -> TaskScope.Vault
                    "Review" -> TaskScope.Review
                    "Historic" -> TaskScope.Historic
                    else -> TaskScope.Main // Default to Main if invalid
                }
                listViewModel.selectDataSource(selectedContext)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadFragments() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.recyclerViewFragmentContainer, RecyclerViewFragment())
            .replace(R.id.selectedInstanceContainer, MainSelectedFragment())
            .commit()
    }

    // Callback from RecyclerViewFragment when a SubTask is requested
    override fun onSubTaskRequested(parentId: Long) {
        // Update the ViewModel to show SubTasks for the given parentId
        listViewModel.selectDataSource(TaskScope.Sub(parentId))
    }
}

