import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.R
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.databinding.FragmentTaskCreationBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.viewModel.TaskScope
import com.example.lifetracer.viewModel.TaskCreationViewModel
import com.example.lifetracer.viewModel.TaskCreationViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskCreationFragment : DialogFragment() {

    interface TaskCreationListener {
        fun onInstanceCreated(instanceWithTask: InstanceWithTask)
    }

    private var listener: TaskCreationListener? = null
    private lateinit var binding: FragmentTaskCreationBinding

    val taskCreationViewModel: TaskCreationViewModel by viewModels {
        TaskCreationViewModelFactory(
            instanceRepository = InstanceRepository(AppDatabase.getDatabase(requireContext()).instanceDao()),
        )
    }

    companion object {
        private const val ARG_CONTEXT = "creationContext"

        fun newInstance(taskScope: TaskScope, parentId: Long? = null): TaskCreationFragment {
            val fragment = TaskCreationFragment()
            val args = Bundle().apply {
                putParcelable(ARG_CONTEXT, taskScope) // Ensure TaskScope implements Parcelable
                parentId?.let { putLong("parentId", it) }
            }
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
            taskCreationViewModel.viewModelScope.launch(Dispatchers.Main) {
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

        val dateOfCreation = getCurrentDate()

        val instance = if (name.isNotEmpty()) {
            InstanceWithTask(
                name = name,
                dateOfCreation = dateOfCreation,
                inputType = inputType,
                regularity = regularity,
                templateId = 0L,
                status = 0
            )
        } else {
            null
        }
        return instance
    }

}
