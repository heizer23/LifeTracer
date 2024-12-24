package com.example.lifetracer.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.Utilities.Mode
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.pause
import com.example.lifetracer.data.start
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectedInstanceViewModel(
    private val instanceRepository: InstanceRepository
) : ViewModel() {

    private val instanceManager = InstanceManager(instanceRepository)

    // Allow null values
    private val _selectedInstance = MutableLiveData<InstanceWithTask?>()
    val selectedInstance: LiveData<InstanceWithTask?> get() = _selectedInstance

    private val _displayDuration = MutableLiveData<Long>()
    val displayDuration: LiveData<Long> = _displayDuration

    private var timerJob: Job? = null

    fun setSelectedInstance(instance: InstanceWithTask) {
        _selectedInstance.value = instance
        startTimerIfNeeded(instance)
    }

    private fun startTimerIfNeeded(instance: InstanceWithTask) {
        if (instance.status == InstanceWithTask.STATUS_STARTED) {
            startTimer(instance)
        } else {
            stopTimer()
            _displayDuration.value = instance.duration
        }
    }

    private fun startTimer(instance: InstanceWithTask) {
        stopTimer() // Stop any existing timer
        val startTime = instance.activeStartTime ?: System.currentTimeMillis()
        val baseDuration = instance.duration

        timerJob = viewModelScope.launch {
            while (true) {
                val currentDuration = baseDuration + (System.currentTimeMillis() - startTime) / 1000
                _displayDuration.postValue(currentDuration)
                delay(1000) // Update every second
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel() // Cancel any ongoing job
        timerJob = null
    }

    fun toggleStartPauseInstance() {
        selectedInstance.value?.let { instance ->
            instanceManager.toggleStartPauseInstance(instance, viewModelScope) { updatedInstance ->
                _selectedInstance.value = updatedInstance
                if (updatedInstance.status == InstanceWithTask.STATUS_STARTED) {
                    startTimer(updatedInstance)
                } else {
                    stopTimer()
                }
            }
        }
    }


    fun swipeLeftAction(instance: InstanceWithTask) {
        viewModelScope.launch(Dispatchers.IO) {
            instanceRepository.deleteInstance(instance)
            withContext(Dispatchers.Main) {
                clearSelectedInstance() // Clear selected task after deletion
            }
        }
    }

    fun swipeRightAction(instance: InstanceWithTask) {
        viewModelScope.launch(Dispatchers.IO) {
            instanceManager.finishInstance(instance, scope = this)
            withContext(Dispatchers.Main) {
                clearSelectedInstance() // Clear selected task after deletion
            }
        }
    }

    fun clearSelectedInstance() {
        _selectedInstance.value = null
    }

    fun incrementQuantity(increment: Int) {
        val currentInstance = _selectedInstance.value
        if (currentInstance != null) {
            instanceManager.incrementQuantity(currentInstance, viewModelScope, increment) { updatedInstance ->
                _selectedInstance.value = updatedInstance // Update LiveData with the new quantity
            }
        }
    }

}
