package com.example.lifetracer.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.model.InstanceRepository

class TaskViewModelFactory(
    private val instanceRepository: InstanceRepository,
    private val chartRepository: ChartRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(instanceRepository, chartRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}