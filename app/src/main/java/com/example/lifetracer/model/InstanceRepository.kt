package com.example.lifetracer.model

import androidx.lifecycle.LiveData
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.Task

class InstanceRepository(private val instanceDao: InstanceDao, private val taskDao: TaskDao) {

    val allActiveInstancesWithTasks: LiveData<List<InstanceWithTask>> = instanceDao.getActiveInstancesWithTasks()
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    // Task-related operations
    suspend fun insertTask(task: Task): Long = taskDao.insert(task)

    suspend fun updateTask(task: Task) = taskDao.update(task)

    suspend fun deleteTask(task: Task) {
        instanceDao.deleteInstancesByTaskId(task.taskId)
        taskDao.delete(task)
    }

    fun getTaskById(taskId: Long): LiveData<Task> = taskDao.getTaskById(taskId)

    // Instance-related operations
    private suspend fun insertInstance(instance: Instance) {
        instanceDao.insert(instance)
    }

    suspend fun updateInstance(instance: Instance) {
        instanceDao.update(instance)
    }

    suspend fun addEmptyInstance(taskId: Long, date: String, time: String) {
        val instance = Instance(
            taskId = taskId,
            date = date,
            time = time,
            duration = 0, // default value
            totalPause = 0, // default value
            quantity = 0, // default value
            quality = "", // default value
            comment = "", // default value
            status = Instance.STATUS_PLANNED // default value, using the constant from Instance class
        )
        insertInstance(instance)
    }
}
