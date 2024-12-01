package com.example.lifetracer.data

import androidx.room.ColumnInfo
import androidx.room.DatabaseView

@DatabaseView(
    viewName = "main_tasks",
    value = """
        SELECT *
        FROM instances
        WHERE id NOT IN (SELECT subTaskId FROM task_relation)
    """
)
data class MainTask(
    val id: Long,
    val templateId: Long,
    val name: String,

    @ColumnInfo(name = "date_of_creation")
    val dateOfCreation: String,

    @ColumnInfo(name = "input_type")
    val inputType: Int,

    val regularity: Int,
    val date: String,
    val time: String,
    val activeStartTime: Long?,
    val pauseStartTime: Long?,
    val duration: Long,
    val totalPause: Long,
    val quantity: Double,
    val quality: String,
    val comment: String,
    val status: Int,
    val priority: Int
)
