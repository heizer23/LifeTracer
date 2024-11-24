import android.util.Log
import androidx.lifecycle.*
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.InterfacerViewModelAdapter
import kotlinx.coroutines.launch

class SubTaskViewModel(
    private val parentTaskId: Long,
    private val instanceRepository: InstanceRepository
) : ViewModel(), InterfacerViewModelAdapter {

    private val _subtasks = MutableLiveData<List<InstanceWithTask>>()
    val subtasks: LiveData<List<InstanceWithTask>> get() = _subtasks

    init {
        loadSubtasks(parentTaskId)
    }

    // Fetch subtasks for the given parent task
    private fun loadSubtasks(parentId: Long) {
        viewModelScope.launch {
            try {
                val fetchedSubtasks = instanceRepository.getSubtasksForParent(parentId)
                _subtasks.postValue(fetchedSubtasks)
            } catch (e: Exception) {
                // Log or handle the error
                Log.e("SubTaskViewModel", "Error loading subtasks: ${e.message}")
            }
        }
    }

    // Add a new subtask and link it to the parent task
    fun addSubtask(parentId: Long, subtask: InstanceWithTask) {
        viewModelScope.launch {
            try {
                val subtaskId = instanceRepository.insertInstance(subtask)
                instanceRepository.linkSubTask(parentId, subtaskId)
                loadSubtasks(parentId) // Refresh the list of subtasks
            } catch (e: Exception) {
                Log.e("SubTaskViewModel", "Error adding subtask: ${e.message}")
            }
        }
    }

    override fun deleteInstance(instance: InstanceWithTask) {
        TODO("Not yet implemented")
    }

    override fun updateInstance(instance: InstanceWithTask) {
        TODO("Not yet implemented")
    }

    override fun updateInstanceOrder(instances: List<InstanceWithTask>) {
        TODO("Not yet implemented")
    }

    override fun selectAndStartInstance(instance: InstanceWithTask) {
        TODO("Not yet implemented")
    }
}
