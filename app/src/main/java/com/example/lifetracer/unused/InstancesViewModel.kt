package com.example.lifetracer.unused

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.InstanceManager
import com.example.lifetracer.viewModel.InterfacerViewModelAdapter
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class InstancesViewModel(
    private val instanceRepository: InstanceRepository,
    private val chartRepository: ChartRepository
) : ViewModel(), InterfacerViewModelAdapter {

    private val instanceManager = InstanceManager(instanceRepository)

    private val _selectedMode = MutableLiveData<Mode>().apply { value = Mode.INSTANCES }

    private val _instanceWithLowestPrio = MediatorLiveData<InstanceWithTask>()

    val instanceWithLowestPrio: LiveData<InstanceWithTask> = _instanceWithLowestPrio

    init {
        _selectedMode.observeForever { mode ->
            _instanceWithLowestPrio.apply {
                // Clear existing sources to avoid redundancy
                this.removeSource(instanceRepository.instanceWithTaskAndLowestPrio)
                this.removeSource(instanceRepository.subTaskWithLowestPrio)

                // Add the appropriate source based on the mode
                when (mode) {
                    Mode.INSTANCES -> addSource(instanceRepository.instanceWithTaskAndLowestPrio) {
                        value = it
                    }
                    Mode.SUBTASKS -> addSource(instanceRepository.subTaskWithLowestPrio) {
                        value = it
                    }
                }
            }
        }
    }


    enum class Mode {
        INSTANCES,
        SUBTASKS
    }

    val allActiveInstanceWithTask: LiveData<List<InstanceWithTask>> = instanceRepository.allActiveInstancesWithTasks

    val allVaultedInstance: LiveData<List<InstanceWithTask>> = instanceRepository.allVaultedTasks

    private val _subtasks = MutableLiveData<List<InstanceWithTask>>()
    val subtasks: LiveData<List<InstanceWithTask>> get() = _subtasks




    override fun selectAndStartInstance(newInstanceWithTask: InstanceWithTask) {
        instanceWithLowestPrio.value?.let { instanceWithTask ->
            if (instanceWithTask.status == InstanceWithTask.STATUS_STARTED) {
//                pauseInstance(instanceWithTask)
            }
        }
        val currentPriority = instanceWithLowestPrio.value?.priority ?: 0
        val newPriority = currentPriority - 1

        // Update the priority of the new instance
        val updatedInstance = newInstanceWithTask.copy(priority = newPriority)
        updateInstance(updatedInstance)

        // Start the new instance
//          startInstance(updatedInstance)
    }

    fun toggleStartPauseInstance() {
        instanceWithLowestPrio.value?.let { instanceWithTask ->
            if (instanceWithTask.status == InstanceWithTask.STATUS_STARTED) {
//                  pauseInstance(instanceWithTask)
            } else {
//                  startInstance(instanceWithTask)
            }
        }
    }

    fun canFinishInstance(instanceWithTask: InstanceWithTask, inputQuality: String?, inputQuantity: String?): Boolean {
        return when (instanceWithTask.inputType) {
            1 -> !inputQuality.isNullOrEmpty()  // Task requires quality input
            2 -> !inputQuantity.isNullOrEmpty() // Task requires quantity input
            3 -> !inputQuality.isNullOrEmpty() && !inputQuantity.isNullOrEmpty() // Both inputs required
            else -> true // No input required
        }
    }

    fun finishActiveInstance(instanceWithTask: InstanceWithTask, inputQuality: String? = null, inputQuantity: String? = null) {

        val canFinish : Boolean = when (instanceWithTask.inputType) {
            1 -> !inputQuality.isNullOrEmpty()  // Task requires quality input
            2 -> !inputQuantity.isNullOrEmpty() // Task requires quantity input
            3 -> !inputQuality.isNullOrEmpty() && !inputQuantity.isNullOrEmpty() // Both inputs required
            else -> true // No input required
        }

        if(canFinish){
            instanceWithLowestPrio.value?.let { instanceWithTask ->
                finishInstance(instanceWithTask, inputQuality, inputQuantity)
            }
        }else{

        }

    }

    fun finishInstance(instanceWithTask: InstanceWithTask, inputQuality: String? = null, inputQuantity: String? = null) {
        val updatedInstance = instanceManager.finishInstance(instanceWithTask, inputQuality, inputQuantity, viewModelScope)
       // chartRepository.updateChartData(updatedInstance.taskId, viewModelScope)
        // Invalidate cache for the task's chart data
        //todo fix chart
        // chartRepository.invalidateChartDataCache(instanceWithTask.taskId)
    }

    override fun updateInstanceOrder(instances: List<InstanceWithTask>) {
        viewModelScope.launch {
            instances.forEachIndexed { index, instanceWithTask ->
                instanceRepository.updatePrio(instanceWithTask.id, index)
            }
        }
    }

    override fun deleteInstance(instanceWithTask: InstanceWithTask) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                instanceRepository.deleteInstance(instanceWithTask)
            } catch (e: Exception) {
                Log.e("InstancesViewModel", "Error deleting instance: ${e.message}")
            }
        }
    }


    override fun updateInstance(updatedInstance: InstanceWithTask){
        instanceManager.updateInstance(updatedInstance, viewModelScope)
    }




    suspend fun getChartData(taskId: Long): List<BarEntry> {
        return chartRepository.getChartData(taskId)
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










}
