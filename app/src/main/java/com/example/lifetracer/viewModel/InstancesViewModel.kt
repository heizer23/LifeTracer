package com.example.lifetracer.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.Utilities.getCurrentTime
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.Task
import com.example.lifetracer.data.TaskFilter
import com.example.lifetracer.data.finish
import com.example.lifetracer.data.pause
import com.example.lifetracer.data.start
import com.example.lifetracer.model.InstanceRepository
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InstancesViewModel(private val instanceRepository: InstanceRepository, private val chartRepository: ChartRepository) : ViewModel() {

    private val instanceManager = InstanceManager(instanceRepository)

    // Instances-----------------------------------------------------------------------------------
    val instanceWithLowestPrio: LiveData<InstanceWithTask> = instanceRepository.instanceWithTaskAndLowestPrio

    val allActiveInstanceWithTask: LiveData<List<InstanceWithTask>> = instanceRepository.allActiveInstancesWithTasks

    fun selectAndStartInstance(newInstanceWithTask: InstanceWithTask) {
        instanceWithLowestPrio.value?.let { instanceWithTask ->
            if (instanceWithTask.instance.status == Instance.STATUS_STARTED) {
                pauseInstance(instanceWithTask.instance)
            }
        }
        val currentPriority = instanceWithLowestPrio.value?.instance?.priority ?: 0
        val newPriority = currentPriority - 1

        // Update the priority of the new instance
        val updatedInstance = newInstanceWithTask.instance.copy(priority = newPriority)
        updateInstance(updatedInstance)

        // Start the new instance
        startInstance(updatedInstance)
    }

    fun toggleStartPauseInstance() {
        instanceWithLowestPrio.value?.let { instanceWithTask ->
            if (instanceWithTask.instance.status == Instance.STATUS_STARTED) {
                pauseInstance(instanceWithTask.instance)
            } else {
                startInstance(instanceWithTask.instance)
            }
        }
    }

    fun canFinishInstance(instanceWithTask: InstanceWithTask, inputQuality: String?, inputQuantity: String?): Boolean {
        return when (instanceWithTask.task.inputType) {
            1 -> !inputQuality.isNullOrEmpty()  // Task requires quality input
            2 -> !inputQuantity.isNullOrEmpty() // Task requires quantity input
            3 -> !inputQuality.isNullOrEmpty() && !inputQuantity.isNullOrEmpty() // Both inputs required
            else -> true // No input required
        }
    }

    fun finishActiveInstance(inputQuality: String? = null, inputQuantity: String? = null) {
        instanceWithLowestPrio.value?.let { instanceWithTask ->
            finishInstance(instanceWithTask, inputQuality, inputQuantity)
        }
    }

    fun finishInstance(instanceWithTask: InstanceWithTask, inputQuality: String? = null, inputQuantity: String? = null) {
        val updatedInstance = instanceManager.finishInstance(instanceWithTask, inputQuality, inputQuantity, viewModelScope)
        chartRepository.updateChartData(updatedInstance.taskId, viewModelScope)
        // Invalidate cache for the task's chart data
        chartRepository.invalidateChartDataCache(instanceWithTask.instance.taskId)
    }

    fun updateInstanceOrder(instances: List<InstanceWithTask>) {
        viewModelScope.launch {
            instances.forEachIndexed { index, instanceWithTask ->
                instanceRepository.updatePrio(instanceWithTask.instance.id, index)
            }
        }
    }

    fun deleteInstance(updatedInstance: Instance){
        instanceManager.deleteInstance(updatedInstance, viewModelScope)
    }

    private fun startInstance(updatedInstance: Instance){
        instanceManager.startInstance(updatedInstance, viewModelScope)
    }

    private fun updateInstance(updatedInstance: Instance){
        instanceManager.updateInstance(updatedInstance, viewModelScope)
    }

    private fun pauseInstance(updatedInstance: Instance){
        instanceManager.pauseInstance(updatedInstance, viewModelScope)
    }


    suspend fun getChartData(taskId: Long): List<BarEntry> {
        return chartRepository.getChartData(taskId)
    }

}
