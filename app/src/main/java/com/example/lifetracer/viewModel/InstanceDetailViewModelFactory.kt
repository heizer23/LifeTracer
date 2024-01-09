package com.example.lifetracer.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.charts.ChartRepository

class InstanceDetailViewModelFactory(private val instanceId: Long,
                                     private val instanceRepository: InstanceRepository,
                                     private val chartRepository: ChartRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstanceDetailViewModel::class.java)) {
            return InstanceDetailViewModel(instanceId, instanceRepository, chartRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

