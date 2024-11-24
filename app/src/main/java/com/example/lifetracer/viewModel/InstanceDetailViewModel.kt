package com.example.lifetracer.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.model.InstanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InstanceDetailViewModel(
    private val instanceId: Long,
    private val instanceRepository: InstanceRepository,
    private val chartRepository: ChartRepository
) : ViewModel() {

    private val _instance = MutableLiveData<InstanceWithTask>()
    val instance: LiveData<InstanceWithTask> get() = _instance

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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val instanceDetails = instanceRepository.getInstance(instanceId)
                withContext(Dispatchers.Main) {
                    _instance.value = instanceDetails

                    // Populate editable fields
                    name.value = instanceDetails.name
                    inputType.value = instanceDetails.inputType
                    quantity.value = instanceDetails.quantity
                    quality.value = instanceDetails.quality
                    comment.value = instanceDetails.comment
                    regularity.value = instanceDetails.regularity
                }
            } catch (e: Exception) {
                Log.e("InstanceDetailViewModel", "Error fetching instance details: ${e.message}")
            }
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

        // Validate and update fields
        val updatedInstance = currentInstance.copy(
            name = newName.ifEmpty { currentInstance.name },
            inputType = if (newInputType >= 0) newInputType else currentInstance.inputType,
            quantity = if (newQuantity >= 0) newQuantity else currentInstance.quantity,
            quality = newQuality.ifEmpty { currentInstance.quality },
            comment = newComment.ifEmpty { currentInstance.comment },
            regularity = if (newRegularity in 0..1) newRegularity else currentInstance.regularity
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                instanceRepository.updateInstance(updatedInstance)
                withContext(Dispatchers.Main) {
                    _instance.value = updatedInstance
                }
            } catch (e: Exception) {
                Log.e("InstanceDetailViewModel", "Failed to update instance: ${e.message}")
            }
        }
    }
}
