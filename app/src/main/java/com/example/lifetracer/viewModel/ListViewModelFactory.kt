package com.example.lifetracer.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.lifetracer.model.InstanceRepository

class ListViewModelFactory(private val instanceRepository: InstanceRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListViewModel(instanceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
