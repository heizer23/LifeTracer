package com.example.lifetracer.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel(private val instanceRepository: InstanceRepository) : ViewModel() {

    private val _instances = MediatorLiveData<List<InstanceWithTask>>()
    val instances: LiveData<List<InstanceWithTask>> get() = _instances

    // Dynamically sets the source LiveData
    fun selectDataSource(keyword: String, context: String? = null) {
        when (keyword) {
            "vault" -> {
                _instances.addSource(instanceRepository.getVaultedTasks()) { data ->
                    _instances.value = data
                }
            }
            "main" -> {
                _instances.addSource(instanceRepository.allActiveInstancesWithTasks) { data ->
                    _instances.value = data
                }
            }
            "review" -> {
                context?.let { date ->
                    _instances.addSource(instanceRepository.getFinishedInstancesForDay(date)) { data ->
                        _instances.value = data
                    }
                } ?: throw IllegalArgumentException("Current date is required for review.")
            }
            "sub" -> {
                context?.let { parentId ->
                    viewModelScope.launch {
                        val data = instanceRepository.getSubtasksForParent(parentId.toLong())
                        _instances.postValue(data.value)
                    }
                } ?: throw IllegalArgumentException("Parent ID is required for 'sub'")
            }
            else -> throw IllegalArgumentException("Invalid keyword: $keyword")
        }
    }


    // Flag to indicate if dragging is in progress
    private val _isDragging = MutableLiveData(false)
    val isDragging: LiveData<Boolean> get() = _isDragging

    fun setIsDragging(value: Boolean) {
        _isDragging.value = value
    }


    fun updatePriorities(updatedList: List<InstanceWithTask>) = viewModelScope.launch(Dispatchers.IO) {
        // Make a copy of the list to avoid ConcurrentModificationException
        val safeList = ArrayList(updatedList)

        for (i in safeList.indices) {
            val instance = safeList[i]
            // Update instance in the database
            instanceRepository.updatePrio(instance.id, instance.priority)
        }
    }



    // Updated method to delete an instance
    fun deleteInstance(instanceWithTask: InstanceWithTask) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                instanceRepository.deleteInstance(instanceWithTask)
            } catch (e: Exception) {
                Log.e("InstancesViewModel", "Error deleting instance: ${e.message}")
            }
        }
    }

    fun moveTaskToMain(instance: InstanceWithTask, isFromReview: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isFromReview || instance.regularity == InstanceWithTask.Companion.Regularity.SINGLE) {
                    // Just update the status to planned
                    val updatedInstance = instance.copy(status = InstanceWithTask.STATUS_PLANNED)
                    updateInstance(updatedInstance)
                } else if (instance.regularity == InstanceWithTask.Companion.Regularity.REGULAR) {
                    // Create a new instance and insert directly
                    val newInstance = instance.copy(
                        id = 0, // Auto-generate a new ID
                        dateOfCreation = getCurrentDate(),
                        status = InstanceWithTask.STATUS_PLANNED
                    )
                    instanceRepository.copyTaskWithSubtasks(instance.id, newInstance)
                }
            } catch (e: Exception) {
                Log.e("InstancesViewModel", "Error moving task to main: ${e.message}")
            }
        }
    }

    fun updateInstance(updatedInstance: InstanceWithTask){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                instanceRepository.updateInstance(updatedInstance)
            } catch (e: Exception) {
                Log.e("ViewModel", "Error updating instance: ${e.message}")
            }
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
