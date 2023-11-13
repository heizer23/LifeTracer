package com.example.lifetracer.model

import androidx.lifecycle.LiveData
import com.example.lifetracer.data.Task

class TaskRepository(private val taskDao: TaskDao) {

    // Room executes all database queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task): Long {
        return taskDao.insert(task)
    }

    suspend fun update(task: Task) {
        // You can perform background tasks here if needed
        taskDao.update(task)
    }

    suspend fun deleteTask(task: Task) {
        // You can perform background tasks here if needed
        taskDao.delete(task)
    }

    fun getTaskById(taskId: Long): LiveData<Task> {
        return taskDao.getTaskById(taskId)
    }
}
