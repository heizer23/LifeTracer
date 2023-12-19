package com.example.lifetracer.charts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chart_cw")
data class ChartData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long,
    val calendarWeek: Float, // Stored in "YYYY.WW" format
    val numberOfInstances: Float
)
