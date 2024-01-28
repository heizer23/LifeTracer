package com.example.lifetracer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.Utilities.getCurrentTime

@Entity(tableName = "instances")
data class InstanceWithTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val templateId: Long = id,

    val name: String, // Default empty string

    @ColumnInfo(name = "date_of_creation")
    val dateOfCreation: String = "", // Default empty string

    @ColumnInfo(name = "input_type")
    val inputType: Int, // Default to an invalid value or a specific default

    @ColumnInfo(name = "regularity")
    val regularity: Int, // Default to an invalid value or a specific default

    val date: String = "", // Default empty string
    val time: String = "", // Default empty string

    val activeStartTime: Long? = null,
    val pauseStartTime: Long? = null,
    val duration: Long = 0, // Default value
    val totalPause: Long = 0, // Default value
    val quantity: Double = 0.0, // Default value
    val quality: String = "", // Default value
    val comment: String = "", // Default value
    val status: Int = STATUS_PLANNED, // Default value
    val priority: Int = 0 // Default value
) {
    companion object {
        const val STATUS_PLANNED = 0
        const val STATUS_STARTED = 1
        const val STATUS_PAUSED = 2
        const val STATUS_FINISHED = 99
    }
}

// Extension function for Instance class
fun InstanceWithTask.start(currentTime: Long): InstanceWithTask {
    // Assuming '0' indicates not started
    val isFirstStart = this.date == "0"

    val additionalPauseTime = this.calculateAdditionalPauseTime(currentTime)

    return if (isFirstStart) {
        this.copy(
            status = InstanceWithTask.STATUS_STARTED,
            date = getCurrentDate(),
            time = getCurrentTime(),
            activeStartTime = currentTime,
            pauseStartTime = null // Reset pause start time
        )
    } else {
        this.copy(
            status = InstanceWithTask.STATUS_STARTED,
            totalPause = this.totalPause + additionalPauseTime,
            activeStartTime = currentTime,
            pauseStartTime = null // Reset pause start time
        )
    }
}

fun InstanceWithTask.calculateAdditionalPauseTime(currentTime: Long): Long {
    return if (this.status == InstanceWithTask.STATUS_PAUSED) {
        (currentTime - (this.pauseStartTime ?: currentTime)) / 1000
    } else {
        0
    }
}
fun InstanceWithTask.pause(currentTime: Long): InstanceWithTask {
    val additionalDuration = if (this.status == InstanceWithTask.STATUS_STARTED) {
        // Calculate the additional duration only if the instance was started
        (currentTime - (this.activeStartTime ?: currentTime)) / 1000
    } else {
        0
    }
    return this.copy(
        status = InstanceWithTask.STATUS_PAUSED,
        duration = this.duration + additionalDuration,
        pauseStartTime = currentTime,
        activeStartTime = null
    )
}

fun InstanceWithTask.finish(currentTime: Long, inputQuality: String?, inputQuantity: String?, taskType: Int): InstanceWithTask {
    val finalDuration = if (this.status == InstanceWithTask.STATUS_STARTED) {
        // Calculate the final active duration only if the instance was started
        (currentTime - (this.activeStartTime ?: currentTime)) / 1000
    } else {
        0
    }
    return this.copy(
        status = InstanceWithTask.STATUS_FINISHED,
        duration = this.duration + finalDuration,
        date = getCurrentDate(),
        time = getCurrentTime(),
        quality = if (taskType == 1 || taskType == 3) inputQuality ?: this.quality else this.quality,
        quantity = if (taskType == 2 || taskType == 3) inputQuantity?.toDoubleOrNull() ?: this.quantity else this.quantity
    )
}
