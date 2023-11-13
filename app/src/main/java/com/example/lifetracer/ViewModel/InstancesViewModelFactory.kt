package com.example.lifetracer.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.lifetracer.model.TaskRepository
import com.example.lifetracer.model.InstanceRepository


class InstancesViewModelFactory(
    private val taskRepository: TaskRepository,
    private val instanceRepository: InstanceRepository
) : ViewModelProvider.Factory {



    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(InstancesViewModel::class.java)) {
            return InstancesViewModel(taskRepository, instanceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
