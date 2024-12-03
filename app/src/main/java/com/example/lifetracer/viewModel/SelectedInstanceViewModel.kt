package com.example.lifetracer.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.Utilities.Mode
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectedInstanceViewModel(
    private val instanceRepository: InstanceRepository
) : ViewModel() {

    private val _selectedMode = MutableLiveData<Mode>().apply { value = Mode.INSTANCES }
    private val _parentTaskId = MutableLiveData<Long>()

    private val _instanceWithLowestPrio = MediatorLiveData<InstanceWithTask>()
    val instanceWithLowestPrio: LiveData<InstanceWithTask> = _instanceWithLowestPrio

    init {
        _selectedMode.observeForever { mode ->
            _instanceWithLowestPrio.apply {
                removeSource(instanceRepository.instanceWithTaskAndLowestPrio)
                removeSource(instanceRepository.subTaskWithLowestPrio)

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

    fun setModeAndParentId(mode: Mode, parentId: Long = -1) {
        _selectedMode.value = mode
        if (mode == Mode.SUBTASKS) _parentTaskId.value = parentId
        instanceRepository.seteParentId(parentId)
    }

    fun updateSelectedInstance(instance: InstanceWithTask) {
        viewModelScope.launch(Dispatchers.IO) {
            instanceRepository.updateInstance(instance)
        }
    }

    fun toggleStartPauseInstance(){

    }


    fun canFinishInstance(currentTask: InstanceWithTask, qualityInput: String, quantityInput: String): Boolean {
        return true
    }

    fun finishActiveInstance(qualityInput: String, quantityInput: String) {

    }
}
