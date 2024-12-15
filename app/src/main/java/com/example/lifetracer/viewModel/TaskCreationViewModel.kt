package com.example.lifetracer.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskCreationViewModel(
    private val instanceRepository: InstanceRepository
) : ViewModel() {

    fun addInstance(instance: InstanceWithTask, parentId: Long? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val insertedId = instanceRepository.insertInstance(instance)
                val updatedInstance = instance.copy(id = insertedId)
                instanceRepository.updateInstance(updatedInstance)

                if (parentId != null) {
                    instanceRepository.linkSubTask(parentId, insertedId)
                }
            } catch (e: Exception) {
                Log.e("TaskCreationViewModel", "Error adding instance: ${e.message}", e)
            }
        }
    }
}
