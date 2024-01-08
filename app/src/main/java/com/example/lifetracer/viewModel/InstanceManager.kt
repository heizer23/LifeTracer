package com.example.lifetracer.viewModel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.Utilities.getCurrentTime
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.Task
import com.example.lifetracer.data.finish
import com.example.lifetracer.data.pause
import com.example.lifetracer.data.start
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InstanceManager(private val instanceRepository: InstanceRepository) {
    fun startInstance(instance: Instance, scope: CoroutineScope) {
        val currentTime = System.currentTimeMillis()
        val updatedInstance = instance.start(currentTime)
        updateInstance(updatedInstance, scope)
    }

    fun pauseInstance(instance: Instance, scope: CoroutineScope) {
        val currentTime = System.currentTimeMillis()
        val updatedInstance = instance.pause(currentTime)
        updateInstance(updatedInstance, scope)
    }

    fun updateInstance(instance: Instance, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                instanceRepository.updateInstance(instance)
            } catch (e: Exception) {
                Log.e("ViewModel", "Error updating instance: ${e.message}")
            }
        }
    }

    fun finishInstance(instanceWithTask: InstanceWithTask, inputQuality: String? = null, inputQuantity: String? = null, scope: CoroutineScope): Instance {
        // chartRepository.updateChartData and chartRepository.invalidateChartDataCache needs to be called in viewmodel
        val currentTime = System.currentTimeMillis()
        val updatedInstance = instanceWithTask.instance.finish(
            currentTime,
            inputQuality,
            inputQuantity,
            instanceWithTask.task.inputType
        )
        updateInstance(updatedInstance, scope)
        return updatedInstance
    }

    fun addInstance(task: Task, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                instanceRepository.addEmptyInstance(task.taskId, getCurrentDate(), getCurrentTime())
                // You can also post success or failure status to LiveData here, if needed
            } catch (e: Exception) {
                Log.e("InstanceManager", "Error adding instance: ${e.message}")
                // Handle any exceptions, such as updating LiveData with error status
            }
        }
    }
    fun deleteInstance(instance: Instance, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                instanceRepository.deleteInstance(instance)
            } catch (e: Exception) {
                Log.e("InstanceManager", "Error adding instance: ${e.message}")
            }
        }
    }


}