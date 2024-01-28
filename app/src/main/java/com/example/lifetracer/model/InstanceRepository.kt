package com.example.lifetracer.model

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.TaskRelation

class InstanceRepository(private val instanceDao: InstanceDao) {

    val allActiveInstancesWithTasks: LiveData<List<InstanceWithTask>> = instanceDao.getActiveInstancesWithTasks()


    val instanceWithTaskAndLowestPrio: LiveData<InstanceWithTask> = instanceDao.getLowestPriorityInstanceWithTask()

    suspend fun getInstance(instanceId: Long): InstanceWithTask {
        return instanceDao.getInstanceWithTask(instanceId)
    }

    // Instance-related operations
    private suspend fun insertInstance(instance: InstanceWithTask) {
        instanceDao.insert(instance)
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
