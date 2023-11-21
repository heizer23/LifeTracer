package com.example.lifetracer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    val taskId: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "task_quality")
    val taskQuality: String,

    @ColumnInfo(name = "date_of_creation")
    val dateOfCreation: String,

    @ColumnInfo(name = "regularity")
    val regularity: Int,

    @ColumnInfo(name = "fixed")
    val fixed: Boolean
) {
    companion object {
        const val TYPE_ONE_OFF = 0
        const val TYPE_RECURRING = 1
        const val TYPE_REGULAR = 2
    }
}
