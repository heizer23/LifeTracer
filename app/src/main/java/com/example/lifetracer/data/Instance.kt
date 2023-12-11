package com.example.lifetracer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.Utilities.getCurrentTime

@Entity(tableName = "instances")
data class Instance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "task_id")
    val taskId: Long, // Foreign key to link Instance to Task
    val date: String, // The date for this instance
    val time: String, // The time for this instance
    // todo actual Date, time and duration
    val activeStartTime: Long? = null,  // Timestamp when the instance was last started
    val pauseStartTime: Long? = null,
    val duration: Long, // The duration of this instance
    val totalPause: Long, // The total pause for this instance
    val quantity: Int, // The quantity for this instance
    val quality: String, // The quality for this instance
    val comment: String, // Any comments related to this instance
    val status: Int, // The status of this instance (e.g., planned, started, paused, finished)
    val priority: Int
) {
    companion object {
        const val STATUS_PLANNED = 0
        const val STATUS_STARTED = 1
        const val STATUS_PAUSED = 2
        const val STATUS_FINISHED = 99
    }
}

// Extension function for Instance class
fun Instance.start(currentTime: Long): Instance {
    // Assuming '0' indicates not started
    val isFirstStart = this.date == "0"

    val additionalPauseTime = this.calculateAdditionalPauseTime(currentTime)

    return if (isFirstStart) {
        this.copy(
            status = Instance.STATUS_STARTED,
            date = getCurrentDate(),
            time = getCurrentTime(),
            activeStartTime = currentTime,
            pauseStartTime = null // Reset pause start time
        )
    } else {
        this.copy(
            status = Instance.STATUS_STARTED,
            totalPause = this.totalPause + additionalPauseTime,
            activeStartTime = currentTime,
            pauseStartTime = null // Reset pause start time
        )
    }
}

fun Instance.calculateAdditionalPauseTime(currentTime: Long): Long {
    return if (this.status == Instance.STATUS_PAUSED) {
        (currentTime - (this.pauseStartTime ?: currentTime)) / 1000
    } else {
        0
    }
}
fun Instance.pause(currentTime: Long): Instance {
    val additionalDuration = if (this.status == Instance.STATUS_STARTED) {
        // Calculate the additional duration only if the instance was started
        (currentTime - (this.activeStartTime ?: currentTime)) / 1000
    } else {
        0
    }
    return this.copy(
        status = Instance.STATUS_PAUSED,
        duration = this.duration + additionalDuration,
        pauseStartTime = currentTime,
        activeStartTime = null
    )
}

fun Instance.finish(currentTime: Long, inputQuality: String?, inputQuantity: String?, taskType: Int): Instance {
    val finalDuration = if (this.status == Instance.STATUS_STARTED) {
        // Calculate the final active duration only if the instance was started
        (currentTime - (this.activeStartTime ?: currentTime)) / 1000
    } else {
        0
    }

    return this.copy(
        status = Instance.STATUS_FINISHED,
        duration = this.duration + finalDuration,
        quality = if (taskType == 1 || taskType == 3) inputQuality ?: this.quality else this.quality,
        quantity = if (taskType == 2 || taskType == 3) inputQuantity?.toIntOrNull() ?: this.quantity else this.quantity
    )
}
