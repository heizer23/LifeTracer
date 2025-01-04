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

            val currentContext = if (listViewModel.sourceActivity == "Sub") "Sub"
            else binding.contextSpinner.selectedItem.toString()
            val parentId = if (currentContext == "Sub") listViewModel.parentId.value else null

            val taskCreationFragment = TaskCreationFragment.newInstance(context = currentContext).apply {
                setTaskCreationListener(object : TaskCreationFragment.TaskCreationListener {
                    override fun onInstanceCreated(subTask: InstanceWithTask) {
                        lifecycleScope.launch {
                            taskCreationViewModel.addInstance(subTask, parentId) // ViewModel handles instance addition
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
                val selectedContext = parent.getItemAtPosition(position).toString()
                listViewModel.selectDataSource(selectedContext) // Directly update the ViewModel
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
        listViewModel.selectDataSource("Sub", parentId.toString())

        // Optionally update the UI to indicate the Sub context
    }

}
