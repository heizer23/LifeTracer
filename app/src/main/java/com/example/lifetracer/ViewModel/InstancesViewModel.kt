package com.example.lifetracer.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.Task
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.model.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InstancesViewModel(private val taskRepository: TaskRepository, private val instanceRepository: InstanceRepository) : ViewModel() {

    // Initialization and setup here

         suspend fun addTaskAndInstance(task: Task) = withContext(Dispatchers.IO) {
            val newTaskId = taskRepository.insert(task)
            instanceRepository.addEmptyInstance(newTaskId, getCurrentDate(),getCurrentTime() )
        }

    fun getAllTasks(): LiveData<List<Task>> {
        return taskRepository.allTasks
    }

    suspend fun deleteTask(task: Task) {
        taskRepository.deleteTask(task)
    }

    fun getAllInstances(): LiveData<List<InstanceWithTask>> {
        return instanceRepository.allInstancesWithTasks
    }

    fun getCurrentDate(): String {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return currentDate.format(Date())
    }
    fun getCurrentTime(): String {
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return currentTime.format(Date())
    }


}