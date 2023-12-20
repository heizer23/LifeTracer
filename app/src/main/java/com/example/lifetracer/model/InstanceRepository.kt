package com.example.lifetracer.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.lifetracer.charts.ChartData
import com.example.lifetracer.charts.ChartDataDao
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.InstanceWithHistory
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.Task
import com.example.lifetracer.data.TaskFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class InstanceRepository(private val instanceDao: InstanceDao, private val taskDao: TaskDao, private val chartDataDao: ChartDataDao) {

    val allActiveInstancesWithTasks: LiveData<List<InstanceWithTask>> = instanceDao.getActiveInstancesWithTasks()

    val allTasksWithoutOpenInst: LiveData<List<Task>> = taskDao.getAllTasksWithoutInstance()

    val instanceWithTaskAndLowestPrio: LiveData<InstanceWithTask> = instanceDao.getLowestPriorityInstanceWithTask()

    val allActiveInstanceWithHistory: LiveData<List<InstanceWithHistory>> = allActiveInstancesWithTasks.map { instances ->
        instances.map { instance ->
            InstanceWithHistory(instance, getHistoryData(instance.instance.taskId))
        }
    }


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

    suspend fun deleteInstance(instance: Instance) {
        instanceDao.delete(instance)
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
            quantity = 0.0, // default value
            quality = "", // default value
            comment = "", // default value
            status = Instance.STATUS_PLANNED, // default value, using the constant from Instance class
            priority = 0
            )
        insertInstance(instance)
    }

    //------History

    private val historyCache = mutableMapOf<Long, LiveData<List<ChartData>>>()

    fun getHistoryData(taskId: Long): LiveData<List<ChartData>> {
        historyCache[taskId]?.let {
            // Return cached data if available
            return it
        }

        // Data not in cache, fetch from database and update the cache
        val historyData = chartDataDao.getChartDataForTask(taskId)
        historyCache[taskId] = historyData
        return historyData
    }


    fun updateChartData(taskId: Long, scope: CoroutineScope) {
        scope.launch {
            val aggregatedData = chartDataDao.getAggregatedDataForTask(taskId)
            aggregatedData.forEach { data ->
                chartDataDao.insertOrUpdate(data)
            }
        }
    }



}
