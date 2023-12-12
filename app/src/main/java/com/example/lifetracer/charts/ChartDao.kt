package com.example.lifetracer.charts

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface ChartDao {
    @Query("SELECT date, quantity FROM instances") // Your complex query for chart data
    fun getBarChartData(): LiveData<List<BarEntryData>>

    // You can add more chart-related queries here
}
