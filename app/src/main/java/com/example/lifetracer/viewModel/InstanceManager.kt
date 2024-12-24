package com.example.lifetracer.viewModel

import android.util.Log
import com.example.lifetracer.Utilities.getCurrentDate
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

    fun updateInstance(instance: InstanceWithTask, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                instanceRepository.updateInstance(instance)
            } catch (e: Exception) {
                Log.e("InstanceManager", "Error updating instance: ${e.message}")
            }
        }
    }

    fun deleteInstance(instance: InstanceWithTask, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                instanceRepository.deleteInstance(instance)
            } catch (e: Exception) {
                Log.e("InstanceManager", "Error deleting instance: ${e.message}")
            }
        }
    }

    fun finishInstance(
        instance: InstanceWithTask,
        inputQuality: String? = null,
        inputQuantity: String? = null,
        scope: CoroutineScope
    ) {
        val finishedInstance = instance.finish(
            currentTime = System.currentTimeMillis(),
            inputQuality = inputQuality,
            inputQuantity = inputQuantity,
            taskType = instance.inputType
        )
        updateInstance(finishedInstance, scope)
    }

    fun moveInstanceToTab(instance: InstanceWithTask, statusTab: Int, scope: CoroutineScope) {
        val updatedInstance = instance.copy(status = statusTab)
        updateInstance(updatedInstance, scope)
    }

    fun copyInstanceToTab(instance: InstanceWithTask, statusTab: Int, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                val newInstance = instance.copy(
                    id = 0, // Auto-generate a new ID
                    dateOfCreation = getCurrentDate(),
                    status = statusTab
                )
                instanceRepository.copyTaskWithSubtasks(instance.id, newInstance)
            } catch (e: Exception) {
                Log.e("InstanceManager", "Error copying instance to tab: ${e.message}")
            }
        }
    }

    fun moveReviewToMain(instance: InstanceWithTask, scope: CoroutineScope) {
        val updatedInstance = instance.copy(
            status = InstanceWithTask.Companion.STATUS_PAUSED
        )
        updateInstance(updatedInstance, scope)
    }

    fun moveMainToVault(instance: InstanceWithTask, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                if (instance.regularity == InstanceWithTask.Companion.Regularity.REGULAR) {
                    deleteInstance(instance, scope)
                } else if (instance.regularity == InstanceWithTask.Companion.Regularity.SINGLE) {
                    moveInstanceToTab(instance, InstanceWithTask.Companion.STATUS_VAULTED, scope)
                }
            } catch (e: Exception) {
                Log.e("InstanceManager", "Error moving main instance to vault: ${e.message}")
            }
        }
    }

    fun moveVaultToMain(instance: InstanceWithTask, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                if (instance.regularity == InstanceWithTask.Companion.Regularity.REGULAR) {
                    copyInstanceToTab(instance, InstanceWithTask.Companion.STATUS_PLANNED, scope)
                } else if (instance.regularity == InstanceWithTask.Companion.Regularity.SINGLE) {
                    moveInstanceToTab(instance, InstanceWithTask.Companion.STATUS_PLANNED, scope)
                }
            } catch (e: Exception) {
                Log.e("InstanceManager", "Error moving vault instance to main: ${e.message}")
            }
        }
    }

    fun startInstance(instance: InstanceWithTask, scope: CoroutineScope, onUpdate: (InstanceWithTask) -> Unit) {
        val updatedInstance = instance.start(System.currentTimeMillis())
        scope.launch(Dispatchers.IO) {
            instanceRepository.updateInstance(updatedInstance)
            withContext(Dispatchers.Main) {
                onUpdate(updatedInstance)
            }
        }
    }

    fun pauseInstance(instance: InstanceWithTask, scope: CoroutineScope, onUpdate: (InstanceWithTask) -> Unit) {
        val updatedInstance = instance.pause(System.currentTimeMillis())
        scope.launch(Dispatchers.IO) {
            instanceRepository.updateInstance(updatedInstance)
            withContext(Dispatchers.Main) {
                onUpdate(updatedInstance)
            }
        }
    }

    fun toggleStartPauseInstance(
        instance: InstanceWithTask,
        scope: CoroutineScope,
        onUpdate: (InstanceWithTask) -> Unit
    ) {
        if (instance.status == InstanceWithTask.STATUS_STARTED) {
            pauseInstance(instance, scope, onUpdate)
        } else {
            startInstance(instance, scope, onUpdate)
        }
    }

    fun incrementQuantity(instance: InstanceWithTask, scope: CoroutineScope, increment: Int , onCompletion: (InstanceWithTask) -> Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val updatedInstance = instance.copy(quantity = instance.quantity + increment) // Increment quantity
                instanceRepository.updateInstance(updatedInstance)
                withContext(Dispatchers.Main) {
                    onCompletion(updatedInstance) // Return updated instance
                }
            } catch (e: Exception) {
                Log.e("InstanceManager", "Error incrementing quantity: ${e.message}")
            }
        }
    }

}
