package com.example.lifetracer.viewModel

import SubTaskViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lifetracer.model.InstanceRepository

class SubTaskViewModelFactory(
    private val instanceId: Long,
    private val instanceRepository: InstanceRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubTaskViewModel::class.java)) {
            return SubTaskViewModel(instanceId, instanceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
