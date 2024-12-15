package com.example.lifetracer.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.Utilities.Mode
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectedInstanceViewModel(
    private val instanceRepository: InstanceRepository
) : ViewModel() {

    private val _selectedInstance = MutableLiveData<InstanceWithTask>()
    val selectedInstance: LiveData<InstanceWithTask> = _selectedInstance

    fun setSelectedInstance(instance: InstanceWithTask) {
        _selectedInstance.value = instance
    }

}
