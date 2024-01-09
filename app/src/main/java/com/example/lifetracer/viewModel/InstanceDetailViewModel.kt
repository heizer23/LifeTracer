package com.example.lifetracer.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.model.InstanceRepository

class InstanceDetailViewModel(private val instanceId: Long, private val instanceRepository: InstanceRepository, private val chartRepository: ChartRepository) : ViewModel() {

    private val instanceManager = InstanceManager(instanceRepository)

    val currentInstance: LiveData<InstanceWithTask> = liveData {
        emit(instanceRepository.getInstance(instanceId))
    }

}