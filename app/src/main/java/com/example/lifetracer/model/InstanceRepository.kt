package com.example.lifetracer.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.Task

class InstanceRepository(private val instanceDao: InstanceDao, private val taskDao: TaskDao) {

    private val _currentFilter = MutableLiveData<Int>().apply { value = Task.REGULARITY_ALL }
    val currentFilter: LiveData<Int> = _currentFilter

    val allActiveInstancesWithTasks: LiveData<List<InstanceWithTask>> = instanceDao.getActiveInstancesWithTasks()

    val tasks: LiveData<List<Task>> = _currentFilter.switchMap() { filter ->
        when (filter) {
            Task.REGULARITY_ALL -> taskDao.getAllTasks()
            Task.TYPE_ONE_OFF -> taskDao.getRegularUnfinshedTasks()
            else -> taskDao.getTasksByRegularity(filter)
        }
    }

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

    suspend fun updatePrio(instanceId: Long, priority: Int){
        Log.d("DD up Rep", "$instanceId $priority")
        instanceDao.updatePrio(instanceId, priority)
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
            status = Instance.STATUS_PLANNED,
            priority = 0
        )
        insertInstance(instance)
    }

    fun setFilter(taskType: Int) {
        _currentFilter.value = taskType
    }

}
