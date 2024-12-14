package com.example.lifetracer.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.TaskRelation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InstanceRepository(private val instanceDao: InstanceDao) {

    val allActiveInstancesWithTasks: LiveData<List<InstanceWithTask>> = instanceDao.getActiveInstances()

    val allVaultedTasks: LiveData<List<InstanceWithTask>> = instanceDao.getVaultedTasks()


    val instanceWithTaskAndLowestPrio: LiveData<InstanceWithTask> = instanceDao.getLowestPriorityInstance()

    private val _parentId = MutableLiveData<Long>() // Observable parentId
    val parentId: LiveData<Long> get() = _parentId

    val subTaskWithLowestPrio = MediatorLiveData<InstanceWithTask>().apply {
        addSource(_parentId) { newParentId ->
            val liveData = instanceDao.getLowestPrioritySubTask(newParentId)
            addSource(liveData) { value ->
                this.value = value
                removeSource(liveData) // Clean up after fetching the data
            }
        }
    }

    fun getVaultedTasks(): LiveData<List<InstanceWithTask>> {
        return instanceDao.getVaultedTasks()
    }

    fun getSubtasksForParent(parentId: Long): LiveData<List<InstanceWithTask>> {
        return instanceDao.getSubtasksForParent(parentId) // This should return LiveData
    }

    fun getFinishedInstancesForDay(currentDate: String): LiveData<List<InstanceWithTask>> {
        return instanceDao.getFinishedInstancesForDay(
            finishedStatus = InstanceWithTask.STATUS_FINISHED,
            currentDate = currentDate
        )
    }


    fun seteParentId(paId: Long) {
        _parentId.value = paId // Dynamically update parentId
    }
    suspend fun getInstance(instanceId: Long): InstanceWithTask {
        return instanceDao.getInstanceById(instanceId)
    }



    // Instance-related operations
    suspend fun insertInstance(instance: InstanceWithTask): Long {
        return instanceDao.insert(instance)
    }

    suspend fun copyTaskWithSubtasks(originalParentId: Long, newParentInstance: InstanceWithTask) {
        // Insert the new parent instance and get its ID
        val newParentId = instanceDao.insert(newParentInstance)

        // Fetch original subtasks for the given parent (value from LiveData)
        val originalSubtasks = instanceDao.getSubtasksForParent(originalParentId).value

        // Ensure originalSubtasks is not null
        if (originalSubtasks != null) {
            // Copy each subtask, set the new parent ID, and insert them
            val newSubtaskIds = mutableListOf<Long>()
            for (originalSubtask in originalSubtasks) {
                val newSubtask = originalSubtask.copy(
                    id = 0, // New ID will be auto-generated
                    templateId = newParentId
                )
                val newSubtaskId = instanceDao.insert(newSubtask)
                newSubtaskIds.add(newSubtaskId)
            }

            // Link the new subtasks to the new parent in task_relation
            for (newSubtaskId in newSubtaskIds) {
                instanceDao.insertTaskRelation(TaskRelation(newParentId, newSubtaskId))
            }
        } else {
            Log.e("InstanceRepository", "No subtasks found for parentId: $originalParentId")
        }
    }



    suspend fun linkSubTask(parentId: Long, subTaskId: Long) {
        try {
            val taskRelation = TaskRelation(parentId, subTaskId)
            instanceDao.insertTaskRelation(taskRelation)
        } catch (e: Exception) {
            Log.e("InstanceRepository", "Error linking subtask: ${e.message}")
            // Handle any exceptions, such as updating LiveData with error status or rethrowing the exception
        }
    }

    suspend fun copyInstance(instance: InstanceWithTask) {
        withContext(Dispatchers.IO) {
            val newInstance = instance.copy(
                id = 0, // Reset the ID to create a new record
                dateOfCreation = getCurrentDate(), // Set the current date
                status = InstanceWithTask.STATUS_PLANNED // Set status to 0 (STATUS_PLANNED)
            )
            instanceDao.insert(newInstance)  // Insert the new instance into the database
        }
    }


    suspend fun updateInstance(instance: InstanceWithTask) {
        withContext(Dispatchers.IO) {
            instanceDao.update(instance)
        }
    }


    suspend fun deleteInstance(instance: InstanceWithTask) {
        instanceDao.delete(instance)
    }

    suspend fun updatePrio(instanceId: Long, priority: Int){
        instanceDao.updatePrio(instanceId, priority)
    }








}
