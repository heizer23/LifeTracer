package com.example.lifetracer.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.launch
import com.example.lifetracer.data.InstanceWithTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class InstanceDetailViewModel(
    private val instanceId: Long,
    private val instanceRepository: InstanceRepository,
    private val chartRepository: ChartRepository
) : ViewModel() {

    private val _instance = MutableLiveData<InstanceWithTask>()
    val instance: LiveData<InstanceWithTask> = _instance

    // Editable fields
    val name = MutableLiveData<String>()
    val inputType = MutableLiveData<Int>()
    val quantity = MutableLiveData<Double>()
    val quality = MutableLiveData<String>()
    val comment = MutableLiveData<String>()
    val regularity = MutableLiveData<Int>()

    init {
        fetchInstanceDetails()
    }



    private fun fetchInstanceDetails() {
        viewModelScope.launch {
            val instanceDetails = instanceRepository.getInstance(instanceId)
            _instance.postValue(instanceDetails)

            // Populate editable fields
            name.postValue(instanceDetails.name)
            inputType.postValue(instanceDetails.inputType)
            quantity.postValue(instanceDetails.quantity)
            quality.postValue(instanceDetails.quality)
            comment.postValue(instanceDetails.comment)
            regularity.postValue(instanceDetails.regularity)
        }
    }

    fun saveChanges(
        newName: String,
        newInputType: Int,
        newQuantity: Double,
        newQuality: String,
        newComment: String,
        newRegularity: Int
    ) {
        val currentInstance = _instance.value
        if (currentInstance == null) {
            Log.e("InstanceDetailViewModel", "Current instance is null. Cannot save changes.")
            return
        }

        // Validate inputs and use defaults if necessary
        val validName = newName.ifEmpty { currentInstance.name }
        val validInputType = if (newInputType >= 0) newInputType else currentInstance.inputType
        val validQuantity = if (newQuantity >= 0) newQuantity else currentInstance.quantity
        val validQuality = newQuality.ifEmpty { currentInstance.quality }
        val validComment = newComment.ifEmpty { currentInstance.comment }
        val validRegularity = if (newRegularity in 0..1) newRegularity else currentInstance.regularity

        // Create the updated instance
        val updatedInstance = currentInstance.copy(
            name = validName,
            inputType = validInputType,
            quantity = validQuantity,
            quality = validQuality,
            comment = validComment,
            regularity = validRegularity
        )

        // Verify the updatedInstance before posting it
        if (updatedInstance.name.isNotEmpty() &&
            updatedInstance.inputType >= 0 &&
            updatedInstance.regularity in 0..1
        ) {
            // Update the LiveData
            _instance.postValue(updatedInstance)
            viewModelScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        instanceRepository.updateInstance(updatedInstance)
                    }
                    _instance.postValue(updatedInstance) // Update LiveData
                } catch (e: Exception) {
                    Log.e("InstanceDetailViewModel", "Failed to update instance: ${e.message}")
                }
            }
        } else {
            Log.e("InstanceDetailViewModel", "Validation failed for updated instance.")
        }
    }

}
