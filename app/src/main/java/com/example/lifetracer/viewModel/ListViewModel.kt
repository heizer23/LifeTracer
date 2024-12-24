package com.example.lifetracer.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel(
    private val instanceRepository: InstanceRepository
) : ViewModel() {

    private val instanceManager = InstanceManager(instanceRepository)

    private val _instances = MediatorLiveData<List<InstanceWithTask>>()
    val instances: LiveData<List<InstanceWithTask>> get() = _instances


    private var currentSource: LiveData<*>? = null
    private var sourceActivity: String = "n/a"

    // Dynamically sets the source LiveData
    fun selectDataSource(keyword: String, context: String? = null) {
        currentSource?.let { _instances.removeSource(it) }

        sourceActivity = keyword

        val newSource = when (sourceActivity) {
            "vault" -> instanceRepository.getVaultedTasks()
            "main" -> instanceRepository.allActiveInstancesWithTasks
            "review" -> context?.let { instanceRepository.getFinishedInstancesForDay(it) }
                ?: throw IllegalArgumentException("Current date is required for review.")
            "sub" -> context?.let { instanceRepository.getSubtasksForParent(it.toLong()) }
                ?: throw IllegalArgumentException("Parent ID is required for 'sub'")
            else -> throw IllegalArgumentException("Invalid keyword: $keyword")
        }

        currentSource = newSource
        _instances.addSource(newSource) { data -> _instances.value = data }
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
            instanceManager.updateInstance(
                instance.copy(priority = i),
                scope = this
            )
        }
    }

    fun swipeLeftAction(instance: InstanceWithTask) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (sourceActivity) {
                    "vault" -> {
                        instanceManager.deleteInstance(instance, this)
                    }
                    "main" -> {
                        instanceManager.moveMainToVault(instance, this)
                    }
                    "review" -> {
                        instanceManager.moveReviewToMain(instance, this)
                    }
                    else -> {
                        Log.e("ListViewModel", "Invalid source provided: $sourceActivity")
                    }
                }
            } catch (e: Exception) {
                Log.e("ListViewModel", "Error handling swipe left: ${e.message}")
            }
        }
    }

    fun swipeRightAction(instance: InstanceWithTask) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (sourceActivity) {
                    "vault" -> {
                        instanceManager.moveVaultToMain(instance, this)
                    }
                    "main" -> {
                        instanceManager.finishInstance(instance, scope = this)
                    }
                    "review" -> {
                        instanceManager.deleteInstance(instance, this)
                    }
                    else -> {
                        Log.e("ListViewModel", "Invalid source provided: $sourceActivity")
                    }
                }
            } catch (e: Exception) {
                Log.e("ListViewModel", "Error handling swipe right: ${e.message}")
            }
        }
    }
}
