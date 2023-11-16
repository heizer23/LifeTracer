package com.example.lifetracer.model

import androidx.lifecycle.LiveData
import androidx.room.Transaction
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.Task

class InstanceRepository(private val instanceDao: InstanceDao, private val taskDao: TaskDao) {

    // LiveData to observe instances with associated tasks
    val allInstancesWithTasks: LiveData<List<InstanceWithTask>> = instanceDao.getInstancesWithTasks()

    // Insert an instance
    suspend fun insertInstance(instance: Instance) {
        instanceDao.insert(instance)
    }

    suspend fun addEmptyInstance(taskId: Long, date: String, time: String) {
        val instance = Instance(
            taskId = taskId,
            date = date,
            time = time,
            duration = 0,
            totalPause = 0,
            quantity = 0,
            quality = "",
            comment = "",
            status = 0
        )
        insertInstance(instance)
    }


    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task): Long {
        return taskDao.insert(task)
    }

    suspend fun update(task: Task) {
        // You can perform background tasks here if needed
        taskDao.update(task)
    }


    @Transaction
    suspend fun deleteTask(task: Task) {
        // You can perform background tasks here if needed
        instanceDao.deleteInstancesByTaskId(task.taskId)
        taskDao.delete(task)
    }

    fun getTaskById(taskId: Long): LiveData<Task> {
        return taskDao.getTaskById(taskId)
    }

}
