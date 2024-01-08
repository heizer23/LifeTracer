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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InstancesViewModel(private val instanceRepository: InstanceRepository, private val chartRepository: ChartRepository) : ViewModel() {

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

    private fun startInstance(instance: Instance) {
        val currentTime = System.currentTimeMillis()
        val updatedInstance = instance.start(currentTime)
        updateInstance(updatedInstance)
    }

    private fun pauseInstance(instance: Instance) {
        val currentTime = System.currentTimeMillis()
        val updatedInstance = instance.pause(currentTime)
        updateInstance(updatedInstance)
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
        val currentTime = System.currentTimeMillis()
        val updatedInstance = instanceWithTask.instance.finish(
            currentTime,
            inputQuality,
            inputQuantity,
            instanceWithTask.task.inputType
        )
        updateInstance(updatedInstance)

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

    private fun updateInstance(instance: Instance) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                instanceRepository.updateInstance(instance)
            } catch (e: Exception) {
                Log.e("ViewModel", "Error updating instance: ${e.message}")
            }
        }
        chartRepository.updateChartData(instance.taskId, viewModelScope)
    }

    suspend fun deleteInstance(instance: Instance) {
        withContext(Dispatchers.IO) {
            instanceRepository.deleteInstance(instance)
        }
    }

    suspend fun addInstance(task: Task) = withContext(Dispatchers.IO) {
        instanceRepository.addEmptyInstance(task.taskId, getCurrentDate(), getCurrentTime())
    }

    // Tasks---------------------------------------------------------------------------------------
    fun getAllTasks(): LiveData<List<Task>> {
        return instanceRepository.filteredTasks
    }

    fun linkSubTask(task: Task, parentId: Int){

    }

    suspend fun addTask(task: Task) = withContext(Dispatchers.IO) {
        val newTaskId = instanceRepository.insertTask(task)
    }

    suspend fun deleteTask(task: Task) {
        instanceRepository.deleteTask(task)
    }

    fun setTaskFilter(filter: TaskFilter) {
        instanceRepository.setFilter(filter)
    }

    suspend fun getChartData(taskId: Long): List<BarEntry> {
        return chartRepository.getChartData(taskId)
    }

}
