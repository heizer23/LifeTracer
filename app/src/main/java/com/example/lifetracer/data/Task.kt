package com.example.lifetracer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    val taskId: Long = 0,

    @ColumnInfo(name = "task_type")
    val taskType: Long? = null,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "task_quality")
    val taskQuality: String,

    @ColumnInfo(name = "date_of_creation")
    val dateOfCreation: String,

    @ColumnInfo(name = "regularity")
    val regularity: Int,

    @ColumnInfo(name = "input_type")
    val inputType: Int,

    @ColumnInfo(name = "fixed")
    val fixed: Boolean
) {
    companion object {
        //regularity
        const val TYPE_ONE_OFF = 0
        const val TYPE_RECURRING = 1
        const val TYPE_REGULAR = 2

        //taskType
        const val TYPE_NON = 0
        const val TYPE_QUALITY = 1
        const val TYPE_QUANTITY = 2
        const val TYPE_BOTH = 3
        const val TYPE_WORKOUT = 4

    }

}
