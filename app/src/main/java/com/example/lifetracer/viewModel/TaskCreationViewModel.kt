package com.example.lifetracer.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.Utilities.getCurrentTime
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskCreationViewModel(
    private val instanceRepository: InstanceRepository
) : ViewModel() {

    fun addInstance(instance: InstanceWithTask, context: TaskScope, parentId: Long? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Determine status and timestamps based on context
                val updatedInstance = when (context) {
                    is TaskScope.Main -> instance.copy(
                        status = InstanceWithTask.STATUS_PLANNED,
                    )
                    is TaskScope.Review -> instance.copy(
                        status = InstanceWithTask.STATUS_FINISHED,
                        date = getCurrentDate(),
                        time = getCurrentTime()
                    )
                    else -> instance // Use default values for other contexts
                }

                // Insert the instance into the database
                val insertedId = instanceRepository.insertInstance(updatedInstance)
                val finalInstance = updatedInstance.copy(id = insertedId)
                instanceRepository.updateInstance(finalInstance)

                // Link to parent task if applicable
                if (parentId != null) {
                    instanceRepository.linkSubTask(parentId, insertedId)
                }
            } catch (e: Exception) {
                Log.e("TaskCreationViewModel", "Error adding instance: ${e.message}", e)
            }
        }
    }
}
