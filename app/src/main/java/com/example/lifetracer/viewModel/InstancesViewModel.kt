package com.example.lifetracer.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.Utilities.getCurrentTime
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.Task
import com.example.lifetracer.data.TaskFilter
import com.example.lifetracer.data.finish
import com.example.lifetracer.data.pause
import com.example.lifetracer.data.start
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class InstancesViewModel(private val instanceRepository: InstanceRepository) : ViewModel() {
    private val _selectedInstance = MutableLiveData<InstanceWithTask?>()
    val selectedInstance: LiveData<InstanceWithTask?> = _selectedInstance

    fun selectAndStartInstance(instanceWithTask: InstanceWithTask) {
        if (_selectedInstance.value != instanceWithTask) {
            pauseCurrentInstance()
            _selectedInstance.value = instanceWithTask
            startCurrentInstance()
        }
    }


    fun toggleStartPauseInstance(instanceWithTask: InstanceWithTask) {
        if (instanceWithTask.instance.status == Instance.STATUS_STARTED) {
            pauseCurrentInstance()
        } else {
            startCurrentInstance()
        }
    }
    fun startCurrentInstance() {
        val currentTime = System.currentTimeMillis()
        _selectedInstance.value?.let { instanceWithTask ->
            val updatedInstance = instanceWithTask.instance.start(currentTime)
            updateInstance(updatedInstance)
        }
    }

    fun pauseCurrentInstance() {
        val currentTime = System.currentTimeMillis()
        _selectedInstance.value?.let { instanceWithTask ->
            val updatedInstance = instanceWithTask.instance.pause(currentTime)
            updateInstance(updatedInstance)
        }
    }

    fun finishSelectedInstance(inputQuality: String?, inputQuantity: String?) {
        val currentTime = System.currentTimeMillis()
        _selectedInstance.value?.let { instanceWithTask ->
            val updatedInstance = instanceWithTask.instance.finish(
                currentTime,
                inputQuality,
                inputQuantity,
                instanceWithTask.task.taskType
            )
            updateInstance(updatedInstance)
            _selectedInstance.value = null // Optionally clear the selected instance
        }
    }


    fun canFinishInstance(instanceWithTask: InstanceWithTask, inputQuality: String?, inputQuantity: String?): Boolean {
        return when (instanceWithTask.task.taskType) {
            1 -> !inputQuality.isNullOrEmpty()  // Task requires quality input
            2 -> !inputQuantity.isNullOrEmpty() // Task requires quantity input
            3 -> !inputQuality.isNullOrEmpty() && !inputQuantity.isNullOrEmpty() // Both inputs required
            else -> true // No input required
        }
    }

    fun updateInstanceOrder(instances: List<InstanceWithTask>) {
        viewModelScope.launch {
            instances.forEachIndexed { index, instanceWithTask ->
                instanceRepository.updatePrio(instanceWithTask.instance.id, index)
            }
        }
    }


    private fun updateInstance(instance: Instance) {
        //todo this breaks the Single source of truth principle - woulld be better to observe the data instead of having of copy (_selectedInstance)
        viewModelScope.launch(Dispatchers.IO) {
            instanceRepository.updateInstance(instance)
            // Switch back to the main thread to update LiveData
            withContext(Dispatchers.Main) {
                // Check if the updated instance is the same as the selected instance
                _selectedInstance.value?.let { currentInstanceWithTask ->
                    if (currentInstanceWithTask.instance.id == instance.id) {
                        // Update _selectedInstance with the new instance details
                        _selectedInstance.value = currentInstanceWithTask.copy(instance = instance)
                    }
                }
            }
        }
    }


         suspend fun addTask(task: Task) = withContext(Dispatchers.IO) {
            val newTaskId = instanceRepository.insertTask(task)
        }

    suspend fun addInstance(task: Task) = withContext(Dispatchers.IO) {
        instanceRepository.addEmptyInstance(task.taskId, getCurrentDate(), getCurrentTime())
    }

    fun getAllTasks(): LiveData<List<Task>> {
        return instanceRepository.filteredTasks
    }

    suspend fun deleteTask(task: Task) {
        instanceRepository.deleteTask(task)
    }

    fun setTaskFilter(filter: TaskFilter) {
        instanceRepository.setFilter(filter)
    }

    fun getAllInstances(): LiveData<List<InstanceWithTask>> {
        return instanceRepository.allActiveInstancesWithTasks
    }
}