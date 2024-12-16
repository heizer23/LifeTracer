package com.example.lifetracer.viewModel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.Utilities.getCurrentTime
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.finish
import com.example.lifetracer.data.pause
import com.example.lifetracer.data.start
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InstanceManager(private val instanceRepository: InstanceRepository) {

    fun startInstance(
        instance: InstanceWithTask,
        scope: CoroutineScope,
        onUpdate: (InstanceWithTask) -> Unit
    ) {
        val updatedInstance = instance.start(System.currentTimeMillis())
        scope.launch(Dispatchers.IO) {
            instanceRepository.updateInstance(updatedInstance)
            onUpdate(updatedInstance)
        }
    }


    fun pauseInstance(
        instance: InstanceWithTask,
        scope: CoroutineScope,
        onUpdate: (InstanceWithTask) -> Unit
    ) {
        val updatedInstance = instance.pause(System.currentTimeMillis())
        scope.launch(Dispatchers.IO) {
            instanceRepository.updateInstance(updatedInstance)
            onUpdate(updatedInstance)
        }
    }

    fun updateInstance(instance: InstanceWithTask, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                instanceRepository.updateInstance(instance)
            } catch (e: Exception) {
                Log.e("ViewModel", "Error updating instance: ${e.message}")
            }
        }
    }

    fun finishInstance(instanceWithTask: InstanceWithTask, inputQuality: String? = null, inputQuantity: String? = null, scope: CoroutineScope): InstanceWithTask {
        // chartRepository.updateChartData and chartRepository.invalidateChartDataCache needs to be called in viewmodel
        val currentTime = System.currentTimeMillis()
        val updatedInstance = instanceWithTask.finish(
            currentTime,
            inputQuality,
            inputQuantity,
            instanceWithTask.inputType
        )
        updateInstance(updatedInstance, scope)
        return updatedInstance
    }


}