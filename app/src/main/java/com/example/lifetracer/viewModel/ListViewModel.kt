package com.example.lifetracer.viewModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import android.util.Log
import androidx.lifecycle.*
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Parcelize
sealed class TaskScope : Parcelable {
    @Parcelize
    object Main : TaskScope()
    @Parcelize
    object Vault : TaskScope()
    @Parcelize
    object Review : TaskScope()
    @Parcelize
    object Historic : TaskScope()
    @Parcelize
    data class Sub(val parentId: Long) : TaskScope()
}

class ListViewModel(
    private val instanceRepository: InstanceRepository
) : ViewModel() {

    private val instanceManager = InstanceManager(instanceRepository)

    private val _instances = MediatorLiveData<List<InstanceWithTask>>()
    val instances: LiveData<List<InstanceWithTask>> get() = _instances

    private val _taskScope = MutableLiveData<TaskScope>(TaskScope.Main)
    val taskScope: LiveData<TaskScope> get() = _taskScope

    private var currentSource: LiveData<List<InstanceWithTask>>? = null

    fun selectDataSource(taskScope: TaskScope) {
        _taskScope.value = taskScope
        val newSource = when (taskScope) {
            is TaskScope.Main -> instanceRepository.allActiveInstancesWithTasks
            is TaskScope.Vault -> instanceRepository.getVaultedTasks()
            is TaskScope.Review -> instanceRepository.getFinishedInstancesForDay(getCurrentDate())
            is TaskScope.Historic -> instanceRepository.getHistoricTasks()
            is TaskScope.Sub -> instanceRepository.getSubtasksForParent(taskScope.parentId)
        }
        currentSource?.let { _instances.removeSource(it) }
        currentSource = newSource
        _instances.addSource(newSource) { data -> _instances.value = data }
    }

    private val _isDragging = MutableLiveData(false)
    val isDragging: LiveData<Boolean> get() = _isDragging

    fun setIsDragging(value: Boolean) {
        _isDragging.value = value
    }

    fun updatePriorities(updatedList: List<InstanceWithTask>) = viewModelScope.launch(Dispatchers.IO) {
        val safeList = ArrayList(updatedList)
        for (i in safeList.indices) {
            val instance = safeList[i]
            instanceManager.updateInstance(
                instance.copy(priority = i),
                scope = this
            )
        }
    }

    fun swipeLeftAction(instance: InstanceWithTask) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (_taskScope.value) {
                    is TaskScope.Vault -> instanceManager.deleteInstance(instance, this)
                    is TaskScope.Main -> instanceManager.moveMainToVault(instance, this)
                    is TaskScope.Review -> instanceManager.moveReviewToMain(instance, this)
                    is TaskScope.Historic -> instanceManager.deleteInstance(instance, this)
                    else -> Log.e("ListViewModel", "Invalid context for swipeLeftAction")
                }
            } catch (e: Exception) {
                Log.e("ListViewModel", "Error handling swipe left: ${e.message}", e)
            }
        }
    }

    fun swipeRightAction(instance: InstanceWithTask) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (_taskScope.value) {
                    is TaskScope.Vault -> instanceManager.moveVaultToMain(instance, this)
                    is TaskScope.Main -> instanceManager.finishInstance(instance, scope = this)
                    is TaskScope.Review -> instanceManager.deleteInstance(instance, this)
                    is TaskScope.Historic -> instanceManager.moveVaultToMain(instance, this)
                    else -> Log.e("ListViewModel", "Invalid context for swipeRightAction")
                }
            } catch (e: Exception) {
                Log.e("ListViewModel", "Error handling swipe right: ${e.message}", e)
            }
        }
    }
}
