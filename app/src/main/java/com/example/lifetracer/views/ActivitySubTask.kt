import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifetracer.R
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.SubtaskActivityBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.SubTaskViewModelFactory
import com.example.lifetracer.views.ReusableAdapter

class ActivitySubTask : AppCompatActivity() {

    private lateinit var binding: SubtaskActivityBinding
    private lateinit var viewModel: SubTaskViewModel
    private lateinit var adapter: ReusableAdapter
    private var parentTaskId: Long = 0L // Retrieved from Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SubtaskActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve parent task ID from Intent
        parentTaskId = intent.getLongExtra("PARENT_TASK_ID", 0L)

        // Initialize ViewModel
        val database = AppDatabase.getDatabase(applicationContext)
        val instanceRepository = InstanceRepository(database.instanceDao())
        viewModel = ViewModelProvider(
            this,
            SubTaskViewModelFactory(parentTaskId, instanceRepository)
        ).get(SubTaskViewModel::class.java)

        // Set up RecyclerView
        setupRecyclerView()

        // Observe subtasks LiveData
        viewModel.subtasks.observe(this) { subtasks ->
            adapter.submitList(subtasks)
        }

        // Add Subtask button listener
        binding.addSubtaskButton.setOnClickListener {
            val newSubtask = InstanceWithTask(
                name = "New Subtask", // Placeholder name
                status = InstanceWithTask.STATUS_PLANNED,
                dateOfCreation = getCurrentDate() // Assuming a utility method to get the current date
            )
            viewModel.addSubtask(parentTaskId, newSubtask)
            Toast.makeText(this, "Subtask added!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = ReusableAdapter(
            scope = lifecycleScope,
            viewModel = viewModel,
            onDeleteInstance = { subtask ->
                viewModel.deleteInstance(subtask)
                Toast.makeText(this, "Subtask deleted!", Toast.LENGTH_SHORT).show()
            },
            onRestoreOrFinishInstance = { subtask ->
                viewModel.updateInstance(subtask.copy(status = InstanceWithTask.STATUS_PLANNED))
                Toast.makeText(this, "Subtask moved back to planned!", Toast.LENGTH_SHORT).show()
            },
            useVaultLayout = true // Reuse the vault layout for subtasks
        )

        binding.subtaskRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.subtaskRecyclerView.adapter = adapter
    }
}
