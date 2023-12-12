package com.example.lifetracer.data

import androidx.room.Embedded
import androidx.room.Relation

data class InstanceWithTask(
    @Embedded
    val instance: Instance,

    @Relation(
        parentColumn = "task_id",
        entityColumn = "task_id"
    )
    val task: Task,
    val historical_data: String?
)