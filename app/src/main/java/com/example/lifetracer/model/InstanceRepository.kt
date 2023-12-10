package com.example.lifetracer.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.Task
import com.example.lifetracer.data.TaskFilter

class InstanceRepository(private val instanceDao: InstanceDao, private val taskDao: TaskDao) {

    val allActiveInstancesWithTasks: LiveData<List<InstanceWithTask>> = instanceDao.getActiveInstancesWithTasks()

    val allTasksWithoutOpenInst: LiveData<List<Task>> = taskDao.getAllTasksWithoutInstance()

    private val _currentFilter = MutableLiveData<TaskFilter>()

    val filteredTasks: LiveData<List<Task>> = MediatorLiveData<List<Task>>().apply {
        addSource(allTasksWithoutOpenInst) { tasks ->
            value = filterTasks(tasks, _currentFilter.value)
        }
        addSource(_currentFilter) { filter ->
            value = filterTasks(allTasksWithoutOpenInst.value ?: emptyList(), filter)
        }
    }

    private fun filterTasks(tasks: List<Task>, filter: TaskFilter?): List<Task> {
        filter ?: return tasks // Return all tasks if filter is null

        return tasks.filter { task ->
            val regularityMatch = filter.regularities.contains(task.regularity)
          //  val instanceMatch = !filter.noInstances || !taskWithInfo.hasInstances
            //  regularityMatch && instanceMatch
            regularityMatch
        }
    }

    fun setFilter(filter: TaskFilter) {
        _currentFilter.value = filter
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
        instanceDao.updatePrio(instanceId, priority)
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
            status = Instance.STATUS_PLANNED, // default value, using the constant from Instance class
            priority = 0
            )
        insertInstance(instance)
    }
}
