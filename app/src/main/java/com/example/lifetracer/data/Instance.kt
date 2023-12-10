package com.example.lifetracer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "instances")
data class Instance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "task_id")
    val taskId: Long, // Foreign key to link Instance to Task
    val date: String, // The date for this instance
    val time: String, // The time for this instance
    // actual Date, time and duration
    val duration: Int, // The duration of this instance
    val totalPause: Int, // The total pause for this instance
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