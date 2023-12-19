package com.example.lifetracer.data

import androidx.lifecycle.LiveData
import com.example.lifetracer.charts.ChartData

data class InstanceWithHistory(
    val instanceWithTask: InstanceWithTask,
    val history: LiveData<List<ChartData>>
)
