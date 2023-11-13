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
)
