package com.example.lifetracer.viewModel

import androidx.lifecycle.*
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel(private val instanceRepository: InstanceRepository) : ViewModel() {

    // LiveData for the list of instances
    private val _instances = MutableLiveData<List<InstanceWithTask>>()
    val instances: LiveData<List<InstanceWithTask>> = instanceRepository.allActiveInstancesWithTasks

    // Flag to indicate if dragging is in progress
    private val _isDragging = MutableLiveData(false)
    val isDragging: LiveData<Boolean> get() = _isDragging

    fun setIsDragging(value: Boolean) {
        _isDragging.value = value
    }

    fun updatePriorities(instances: List<InstanceWithTask>) {
        viewModelScope.launch(Dispatchers.IO) {
            for (instance in instances) {
                instanceRepository.updatePrio(instance.id, instance.priority)
            }
        }
    }

    // Updated method to delete an instance
    fun deleteInstance(instanceWithTask: InstanceWithTask) {
        viewModelScope.launch(Dispatchers.IO) {
            instanceRepository.deleteInstance(instanceWithTask) // Pass the whole object
        }
    }

    // Updated method to finish an instance
    fun finishInstance(instanceWithTask: InstanceWithTask) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedInstance = instanceWithTask.copy(status = InstanceWithTask.STATUS_FINISHED)
            instanceRepository.updateInstance(updatedInstance) // Update the repository
        }
    }
}
