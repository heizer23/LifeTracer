import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.R
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
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_task_creation, null, false)
        binding.buttonAddTask.setOnClickListener { handleAddInstance() }
        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        return dialog
    }

    fun setTaskCreationListener(listener: TaskCreationListener) {
        this.listener = listener
    }

    private fun handleAddInstance() {
        val newInstance = createInstanceFromInput()
        newInstance?.let {
            viewModel.viewModelScope.launch(Dispatchers.IO) {
             //   viewModel.addInstance(it)
                listener?.onInstanceCreated(it)
            }
        }
    }

    private fun createInstanceFromInput(): InstanceWithTask? {
        // Gather inputs from binding and construct an InstanceWithTask object
        // ...
        return null // Replace with actual implementation
    }

    // Rest of the fragment code...
}
