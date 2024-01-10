import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.lifetracer.R
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.data.Task
import com.example.lifetracer.databinding.FragmentTaskCreationBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.TaskViewModel
import com.example.lifetracer.viewModel.TaskViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskCreationFragment : DialogFragment() {

    interface TaskCreationListener {
        fun onTaskCreated(task: Task)
    }

    private var parentTaskId: Long = 0L

    private var listener: TaskCreationListener? = null
    private lateinit var binding: FragmentTaskCreationBinding

    private val viewModel: TaskViewModel by viewModels {
        val appContext = requireActivity().applicationContext
        TaskViewModelFactory(
            instanceRepository = InstanceRepository(
                instanceDao = AppDatabase.getDatabase(appContext).instanceDao(),
                taskDao = AppDatabase.getDatabase(appContext).taskDao()
            ),
            chartRepository = ChartRepository(AppDatabase.getDatabase(appContext).chartDataDao())
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            parentTaskId = it.getLong(ARG_PARENT_TASK_ID, -1)
        }
    }

    companion object {
        private const val ARG_PARENT_TASK_ID = "parentTaskId"


        fun newInstance(parentTaskId: Long): TaskCreationFragment {
            val fragment = TaskCreationFragment()
            val args = Bundle()
            args.putLong(ARG_PARENT_TASK_ID, parentTaskId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflate the layout for this fragment using data binding
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_task_creation, null, false)

        // Setup click listeners and other UI interactions here
        binding.buttonAddTask.setOnClickListener { handleAddTask() }

        // Create the dialog
        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        return dialog
    }

    fun setTaskCreationListener(listener: TaskCreationListener) {
        this.listener = listener
    }


    private fun handleAddTask() {
        val subTask = createTaskFromInput()
        addSubTask(subTask)
    }

    private fun createTaskFromInput(): Task? {
        val name = binding.editTextTaskName.text.toString()
        val quality = binding.editTextTaskQuality.text.toString()
        val regularity = binding.editTextTaskRegularity.text.toString().toIntOrNull() ?: 0
        val taskType = binding.editTextTaskType.text.toString().toIntOrNull() ?: 0
        val fixed = binding.checkBoxTaskFixed.isChecked

        return if (name.isNotEmpty()) {
            val dateOfCreation = getCurrentDate()
            Task(0, null,  name, quality, dateOfCreation, regularity, taskType, fixed)
        } else {
            null
        }
    }

    private fun addSubTask(task: Task?) {
        task?.let { nonNullTask ->
            CoroutineScope(Dispatchers.Main).launch {
                val newTaskId = viewModel.addTask(nonNullTask)
                viewModel.linkSubTask(parentTaskId, newTaskId)
            }
        } ?: run {
            // Handle the case where task is null if necessary
            Log.e("addSubTask", "Task is null, cannot add subtask")
        }
    }


    // Rest of your fragment code...
}
