package com.example.lifetracer.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lifetracer.model.InstanceRepository

class SelectedInstanceViewModelFactory(
    private val instanceRepository: InstanceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SelectedInstanceViewModel::class.java)) {
            return SelectedInstanceViewModel(instanceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
