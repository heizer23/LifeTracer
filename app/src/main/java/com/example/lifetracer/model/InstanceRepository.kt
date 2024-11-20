package com.example.lifetracer.model

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.TaskRelation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InstanceRepository(private val instanceDao: InstanceDao) {

    val allActiveInstancesWithTasks: LiveData<List<InstanceWithTask>> = instanceDao.getActiveInstancesWithTasks()

    val allVaultedTasks: LiveData<List<InstanceWithTask>> = instanceDao.getVaultedTasks()


    val instanceWithTaskAndLowestPrio: LiveData<InstanceWithTask> = instanceDao.getLowestPriorityInstanceWithTask()

    suspend fun getInstance(instanceId: Long): InstanceWithTask {
        return instanceDao.getInstanceWithTask(instanceId)
    }

    // Instance-related operations
    suspend fun insertInstance(instance: InstanceWithTask): Long {
        return instanceDao.insert(instance)
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
        instanceDao.update(instance)
    }

    suspend fun deleteInstance(instance: InstanceWithTask) {
        instanceDao.delete(instance)
    }

    suspend fun updatePrio(instanceId: Long, priority: Int){
        instanceDao.updatePrio(instanceId, priority)
    }

    suspend fun addEmptyInstance(name: String, inputType: Int, regularity: Int, date: String, time: String) {
        val instance = InstanceWithTask(
            name = name,
            inputType = inputType,
            regularity = regularity,
            date = date,
            time = time
            // Other fields will use their default values
        )
        insertInstance(instance)
    }

    fun getFinishedInstancesForDay(date: String): LiveData<List<InstanceWithTask>> {
        return instanceDao.getFinishedInstancesForDay(InstanceWithTask.STATUS_FINISHED, date)
    }

    suspend fun moveTaskFromVaultToMain(instance: InstanceWithTask) {
        withContext(Dispatchers.IO) {
            if (instance.regularity == InstanceWithTask.Companion.Regularity.SINGLE) {
                // Singular task: Update status to 0 (planned)
                val updatedInstance = instance.copy(status = InstanceWithTask.STATUS_PLANNED)
                updateInstance(updatedInstance)
            } else if (instance.regularity == InstanceWithTask.Companion.Regularity.REGULAR) {
                // Regular task: Copy the task with a new ID and status 0
                val newInstance = instance.copy(
                    id = 0, // Auto-generate a new ID
                    dateOfCreation = getCurrentDate(),
                    status = InstanceWithTask.STATUS_PLANNED
                )
                insertInstance(newInstance)
            }
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


}
