package com.example.lifetracer.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.Utilities.getCurrentTime
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.Task
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

    fun startCurrentInstance() {
        _selectedInstance.value?.let {
            val updatedInstance = it.instance.copy(status = Instance.STATUS_STARTED)
            updateInstance(updatedInstance)
        }
    }

    fun pauseCurrentInstance() {
        _selectedInstance.value?.let {
            val updatedInstance = it.instance.copy(status = Instance.STATUS_PAUSED)
            updateInstance(updatedInstance)
        }
    }

    fun finishSelectedInstance() {
        _selectedInstance.value?.let { instanceWithTask ->
            val updatedInstance = instanceWithTask.instance.copy(status = Instance.STATUS_FINISHED)
            updateInstance(updatedInstance)
            _selectedInstance.value = null  // Optionally clear the selected instance
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
        viewModelScope.launch(Dispatchers.IO) {
            instanceRepository.updateInstance(instance)
        }
    }


         suspend fun addTaskAndInstance(task: Task) = withContext(Dispatchers.IO) {
            val newTaskId = instanceRepository.insertTask(task)
            instanceRepository.addEmptyInstance(newTaskId, getCurrentDate(),getCurrentTime() )
        }

    fun getAllTasks(): LiveData<List<Task>> {
        return instanceRepository.tasks
    }

    fun updateTaskFilter(taskType: Int){
        instanceRepository.setFilter(taskType)
    }

    suspend fun deleteTask(task: Task) {
        instanceRepository.deleteTask(task)
    }

    fun getAllInstances(): LiveData<List<InstanceWithTask>> {
        return instanceRepository.allActiveInstancesWithTasks
    }
}