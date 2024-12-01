import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.R
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.databinding.FragmentTaskCreationBinding
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.viewModel.InstancesViewModelFactory
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.data.InstanceWithTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskCreationFragment : DialogFragment() {

    interface TaskCreationListener {
        fun onInstanceCreated(instanceWithTask: InstanceWithTask)
    }

    private var parentTaskId: Long = 0L
    private var listener: TaskCreationListener? = null
    private lateinit var binding: FragmentTaskCreationBinding

    private val viewModel: InstancesViewModel by viewModels {
        InstancesViewModelFactory(
            instanceRepository = InstanceRepository(AppDatabase.getDatabase(requireContext()).instanceDao()),
            chartRepository = ChartRepository(AppDatabase.getDatabase(requireContext()).chartDataDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TaskCreationFragment", "onCreate called")
        parentTaskId = arguments?.getLong(ARG_PARENT_TASK_ID, -1L) ?: -1L
    }


    companion object {
        private const val ARG_PARENT_TASK_ID = "parentTaskId"

        // Factory method to create a new instance of TaskCreationFragment with parentTaskId
        fun newInstance(parentTaskId: Long = -1L): TaskCreationFragment {
            val fragment = TaskCreationFragment()
            val args = Bundle()
            args.putLong(ARG_PARENT_TASK_ID, parentTaskId) // Pass the parentTaskId as an argument
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_task_creation, null, false)
        binding.buttonAddTask.setOnClickListener { handleAddInstance() }
        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        return dialog
    }

    fun setTaskCreationListener(listener: TaskCreationListener) {
        this.listener = listener
    }

    private var isProcessing = false // Prevent double processing

    private fun handleAddInstance() {
        if (isProcessing) return // Skip if already processing
        isProcessing = true

        val newInstance = createInstanceFromInput()
        newInstance?.let { instance ->
            viewModel.viewModelScope.launch(Dispatchers.Main) {
                Log.d("DoubleCreation", "TaskCreation: handleAddInstance")
                listener?.onInstanceCreated(instance) // Notify the listener
                dismiss() // Close the fragment
                isProcessing = false // Reset flag
            }
        } ?: run {
            isProcessing = false // Reset flag if no instance was created
        }
    }


    private fun createInstanceFromInput(): InstanceWithTask? {
        val name = binding.editTextTaskName.text.toString()
        val inputType = binding.editTextTaskType.text.toString().toIntOrNull() ?: 0
        val regularity = binding.editTextTaskRegularity.text.toString().toIntOrNull() ?: 0

        return if (name.isNotEmpty()) {
            val dateOfCreation = getCurrentDate()
            InstanceWithTask(
                name = name,
                dateOfCreation = dateOfCreation,
                inputType = inputType,
                regularity = regularity,
                templateId = 0L,
                status = 98
            )
        } else {
            null
        }
    }
}
