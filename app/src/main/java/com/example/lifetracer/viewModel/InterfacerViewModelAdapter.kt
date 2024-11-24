package com.example.lifetracer.viewModel

import com.example.lifetracer.data.InstanceWithTask

interface InterfacerViewModelAdapter {
    fun deleteInstance(instance: InstanceWithTask)
    fun updateInstance(instance: InstanceWithTask)
    fun updateInstanceOrder(instances: List<InstanceWithTask>)
    fun selectAndStartInstance(instance: InstanceWithTask)
}