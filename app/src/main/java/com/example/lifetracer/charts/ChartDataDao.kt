package com.example.lifetracer.charts

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChartDataDao {
    @Query("SELECT * FROM chart_cw WHERE task_id = :taskId")
    fun getChartDataForTask(taskId: Long): List<ChartData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(chartData: ChartData)

    // Fetch aggregated data
    @Query("SELECT id, task_id, strftime('%Y.%W', date) as calendarWeek, COUNT(*) as numberOfInstances FROM instances WHERE task_id = :taskId AND status = 99 GROUP BY strftime('%Y.%W', date)")
    suspend fun getAggregatedDataForTask(taskId: Long): List<ChartData>

    // Other necessary queries
}
