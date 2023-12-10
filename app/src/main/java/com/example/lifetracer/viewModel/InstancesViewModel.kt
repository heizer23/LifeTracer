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
            val instance = instanceWithTask.instance
            val isFirstStart = instance.date == "0"  // Assuming '0' indicates not started

            // Calculate the additional pause time if the instance was paused
            val additionalPauseTime = if (instance.status == Instance.STATUS_PAUSED) {
                (currentTime - (instance.pauseStartTime ?: currentTime)) / 1000
            } else {
                0
            }

            val updatedInstance = if (isFirstStart) {
                // If it's the first start, update date, time, and activeStartTime
                instance.copy(
                    status = Instance.STATUS_STARTED,
                    date = getCurrentDate(),
                    time = getCurrentTime(),
                    activeStartTime = currentTime,
                    pauseStartTime = null // Reset pause start time
                )
            } else {
                // If not the first start, simply update status and activeStartTime
                instance.copy(
                    status = Instance.STATUS_STARTED,
                    totalPause = instance.totalPause + additionalPauseTime,
                    activeStartTime = currentTime,
                    pauseStartTime = null // Reset pause start time
                )
            }
            updateInstance(updatedInstance)
        }
    }

    fun pauseCurrentInstance() {
        val currentTime = System.currentTimeMillis()
        _selectedInstance.value?.let {
            val additionalDuration = if (it.instance.status == Instance.STATUS_STARTED) {
                // Calculate the additional duration only if the instance was started
                (currentTime - (it.instance.activeStartTime ?: currentTime)) / 1000
            } else {
                0
            }
            val updatedInstance = it.instance.copy(
                status = Instance.STATUS_PAUSED,
                duration = it.instance.duration + additionalDuration,
                pauseStartTime = currentTime,
                activeStartTime = null
            )
            updateInstance(updatedInstance)
        }
    }

    fun finishSelectedInstance() {
        val currentTime = System.currentTimeMillis()
        _selectedInstance.value?.let { instanceWithTask ->
            val instance = instanceWithTask.instance

            val updatedInstance = if (instance.status == Instance.STATUS_STARTED) {
                // If the instance is active, calculate the final active duration.
                val finalDuration = (currentTime - (instance.activeStartTime ?: currentTime)) / 1000
                instance.copy(
                    status = Instance.STATUS_FINISHED,
                    duration = instance.duration + finalDuration
                    // No need to update activeStartTime or pauseStartTime here.
                )
            } else {
                // If the instance is not active, simply mark it as finished.
                instance.copy(status = Instance.STATUS_FINISHED)
            }

            updateInstance(updatedInstance)
            // Optionally clear the selected instance. Consider how you want to handle this in the UI.
            _selectedInstance.value = null
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