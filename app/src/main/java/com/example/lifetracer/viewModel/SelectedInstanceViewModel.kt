package com.example.lifetracer.viewModel

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

class SelectedInstanceViewModel(
    private val instanceRepository: InstanceRepository
) : ViewModel() {

    private val _selectedInstance = MutableLiveData<InstanceWithTask>()
    val selectedInstance: LiveData<InstanceWithTask> = _selectedInstance

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
            if (instance.status == InstanceWithTask.STATUS_STARTED) {
                pauseInstance(instance)
            } else {
                startInstance(instance)
            }
        }
    }

    private fun startInstance(instance: InstanceWithTask) {
        val updatedInstance = instance.start(System.currentTimeMillis())
        _selectedInstance.value = updatedInstance // Update LiveData first
        viewModelScope.launch(Dispatchers.IO) {
            instanceRepository.updateInstance(updatedInstance) // Save to DB in a coroutine
        }
        startTimer(updatedInstance) // Start the timer
    }

    private fun pauseInstance(instance: InstanceWithTask) {
        val updatedInstance = instance.pause(System.currentTimeMillis())
        _selectedInstance.value = updatedInstance // Update LiveData first
        viewModelScope.launch(Dispatchers.IO) {
            instanceRepository.updateInstance(updatedInstance) // Save to DB in a coroutine
        }
        stopTimer() // Stop the timer
    }

}

