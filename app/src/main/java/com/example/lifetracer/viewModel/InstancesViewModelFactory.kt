package com.example.lifetracer.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.lifetracer.model.InstanceRepository


class InstancesViewModelFactory(
    private val instanceRepository: InstanceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(InstancesViewModel::class.java)) {
            return InstancesViewModel(instanceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
