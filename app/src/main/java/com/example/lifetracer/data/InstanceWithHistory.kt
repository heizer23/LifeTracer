package com.example.lifetracer.data

import com.example.lifetracer.charts.BarEntryData

data class InstanceWithHistory(
    val instanceWithTask: InstanceWithTask,
    val history: List<BarEntryData>
)
