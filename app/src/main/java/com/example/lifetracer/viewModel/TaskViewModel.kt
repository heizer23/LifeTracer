package com.example.lifetracer.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.data.Task
import com.example.lifetracer.data.TaskFilter
import com.example.lifetracer.model.InstanceRepository
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskViewModel(private val instanceRepository: InstanceRepository, private val chartRepository: ChartRepository) : ViewModel() {

    private val instanceManager = InstanceManager(instanceRepository)

    fun getAllTasks(): LiveData<List<Task>> {
        return instanceRepository.filteredTasks
    }
    fun linkSubTask(task: Task, parentId: Int){
    }

    suspend fun addTask(task: Task) = withContext(Dispatchers.IO) {
        val newTaskId = instanceRepository.insertTask(task)
    }

    suspend fun deleteTask(task: Task) {
        instanceRepository.deleteTask(task)
    }

    fun setTaskFilter(filter: TaskFilter) {
        instanceRepository.setFilter(filter)
    }

    suspend fun getChartData(taskId: Long): List<BarEntry> {
        return chartRepository.getChartData(taskId)
    }

    fun addInstance(task: Task) {
        instanceManager.addInstance(task, viewModelScope)
    }

}

